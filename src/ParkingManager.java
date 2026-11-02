import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * ParkingManager Class
 * Demonstrates:
 *  1. Console UI Orchestration
 *  2. Robust Input Handling and Validation (defensive programming)
 *  3. Support for 24 Specialized Commercial Parking Bays
 */
public class ParkingManager {

    private final ParkingLot parkingLot;
    private final Scanner scanner;

    public ParkingManager(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        printWelcomeBanner();

        while (running) {
            displayMenu();
            int choice = readIntChoice("Enter your choice (1-7): ", 1, 7);

            System.out.println();
            switch (choice) {
                case 1:
                    handleParkVehicle();
                    break;
                case 2:
                    handleVehicleExit();
                    break;
                case 3:
                    handleViewSlots();
                    break;
                case 4:
                    handleSearchVehicle();
                    break;
                case 5:
                    handleViewReport();
                    break;
                case 6:
                    handleViewFileHistory();
                    break;
                case 7:
                    running = false;
                    handleExitApplication();
                    break;
                default:
                    System.out.println("❌ Invalid choice. Please select between 1 and 7.");
            }

            if (running) {
                promptEnterToContinue();
            }
        }
    }

    private void printWelcomeBanner() {
        System.out.println("============================================================");
        System.out.println("     WELCOME TO SMART PARKING LOT MANAGEMENT SYSTEM        ");
        System.out.println("        Total Capacity: " + parkingLot.getTotalCapacity() + " Designated Commercial Bays        ");
        System.out.println("   Bays: Slots 1-6 Bike | Slots 7-18 Car | Slots 19-24 EV   ");
        System.out.println(" Rates: Bike: ₹10/hr | Car: ₹20/hr | EV: ₹25/hr (+₹50 chg) ");
        System.out.println("        ⭐ VIP Members Enjoy 20% Discount on Exit!          ");
        System.out.println("============================================================");
    }

    private void displayMenu() {
        System.out.println("\n========== PARKING LOT MANAGEMENT SYSTEM ==========");
        System.out.println(" 1. Park a Vehicle (Car / Bike / EV)");
        System.out.println(" 2. Remove Vehicle / Vehicle Exit & Bill");
        System.out.println(" 3. View All Parking Bays (1-" + parkingLot.getTotalCapacity() + ")");
        System.out.println(" 4. Search for a Vehicle");
        System.out.println(" 5. View Parking Report & Revenue Breakdown");
        System.out.println(" 6. View Exit History from CSV File");
        System.out.println(" 7. Exit Application");
        System.out.println("===================================================");
    }

    private void handleParkVehicle() {
        System.out.println("--- [1] PARK A VEHICLE ---");

        if (parkingLot.getAvailableSlotCount() == 0) {
            System.out.println("⚠️  SORRY! Parking Lot is completely FULL (" + parkingLot.getTotalCapacity() + "/" + parkingLot.getTotalCapacity() + " slots occupied).");
            return;
        }

        System.out.println("Select Vehicle Type:");
        System.out.println("  1. Car  (Rate: ₹20/hr - Bays 7-18)");
        System.out.println("  2. Bike (Rate: ₹10/hr - Bays 1-6)");
        System.out.println("  3. EV   (Rate: ₹25/hr - Bays 19-24 with optional charging)");
        int typeChoice = readIntChoice("Enter vehicle type (1-3): ", 1, 3);

        String regNumber = readSanitizedString("Enter Vehicle Registration Number (e.g., DL01AB1234): ");
        if (regNumber.isEmpty()) {
            System.out.println("❌ Registration number cannot be blank. Operation cancelled.");
            return;
        }

        boolean chargingNeeded = false;
        if (typeChoice == 3) {
            System.out.print("Do you require high-speed EV charging dock (+₹50 flat)? (y/N): ");
            String chg = scanner.nextLine().trim();
            chargingNeeded = chg.equalsIgnoreCase("y") || chg.equalsIgnoreCase("yes");
        }

        System.out.print("Is this vehicle a VIP / Monthly Pass holder (20% discount)? (y/N): ");
        String vipStr = scanner.nextLine().trim();
        boolean isVip = vipStr.equalsIgnoreCase("y") || vipStr.equalsIgnoreCase("yes");

        System.out.print("Simulate earlier entry time for testing duration & fees? (y/N): ");
        String simChoice = scanner.nextLine().trim();
        LocalDateTime entryTime = LocalDateTime.now();

        if (simChoice.equalsIgnoreCase("y") || simChoice.equalsIgnoreCase("yes")) {
            int pastHours = readIntChoice("Enter hours ago (1-72): ", 1, 72);
            int pastMins = readIntChoice("Enter additional minutes (0-59): ", 0, 59);
            entryTime = entryTime.minusHours(pastHours).minusMinutes(pastMins);
            System.out.println("ℹ️  Simulated Entry Time set to: " + entryTime.format(Vehicle.FORMATTER));
        }

        Vehicle vehicle;
        if (typeChoice == 1) {
            vehicle = new Car(regNumber, entryTime, isVip);
        } else if (typeChoice == 2) {
            vehicle = new Bike(regNumber, entryTime, isVip);
        } else {
            vehicle = new EV(regNumber, chargingNeeded, entryTime, isVip);
        }

        try {
            ParkingSlot assignedSlot = parkingLot.parkVehicle(vehicle);
            System.out.println("\n✅ VEHICLE PARKED SUCCESSFULLY!");
            System.out.println("--------------------------------------------------");
            System.out.println("  Vehicle Number : " + vehicle.getVehicleNumber());
            System.out.println("  Vehicle Type   : " + vehicle.getVehicleType());
            System.out.println("  Assigned Slot  : Slot " + assignedSlot.getSlotId() + " [" + assignedSlot.getSlotType() + "]");
            System.out.println("  VIP Pass       : " + (isVip ? "YES (20% Discount Active)" : "NO"));
            if (chargingNeeded) {
                System.out.println("  EV Charging    : DOCKED (+₹50 flat)");
            }
            System.out.println("  Entry Time     : " + vehicle.getFormattedEntryTime());
            System.out.println("  Hourly Rate    : ₹" + vehicle.getHourlyRate() + "/hr");
            System.out.println("  Slots Left     : " + parkingLot.getAvailableSlotCount() + " of " + parkingLot.getTotalCapacity());
            System.out.println("--------------------------------------------------");
        } catch (ParkingException e) {
            System.out.println("\n❌ FAILED TO PARK: " + e.getMessage());
        }
    }

    private void handleVehicleExit() {
        System.out.println("--- [2] REMOVE VEHICLE / VEHICLE EXIT ---");

        if (parkingLot.getOccupiedSlotCount() == 0) {
            System.out.println("ℹ️  Parking Lot is currently EMPTY. No vehicles to exit.");
            return;
        }

        String regNumber = readSanitizedString("Enter Vehicle Registration Number to Exit: ");
        if (regNumber.isEmpty()) {
            System.out.println("❌ Registration number cannot be blank.");
            return;
        }

        try {
            ParkingReceipt receipt = parkingLot.processVehicleExit(regNumber);
            System.out.println("\n✅ VEHICLE EXIT PROCESSED SUCCESSFULLY!");
            System.out.println(receipt.generateReceipt());
            System.out.println("🟢 Slot " + receipt.getSlotId() + " is now AVAILABLE for new vehicles.");
            System.out.println("💾 Receipt automatically logged to 'data/parking_history.csv'.");
        } catch (VehicleNotFoundException e) {
            System.out.println("\n❌ EXIT FAILED: " + e.getMessage());
        }
    }

    private void handleViewSlots() {
        System.out.println("--- [3] VIEW ALL " + parkingLot.getTotalCapacity() + " PARKING SLOTS ---");
        List<ParkingSlot> slots = parkingLot.getAllSlots();

        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.printf("%-8s | %-16s | %-10s | %-8s | %-16s | %-20s\n",
                "SLOT ID", "BAY CATEGORY", "STATUS", "TYPE", "VEHICLE NO.", "ENTRY TIME");
        System.out.println("---------------------------------------------------------------------------------------------");

        for (ParkingSlot slot : slots) {
            String bayType = slot.getSlotType().getDisplayName();
            if (slot.isOccupied() && slot.getParkedVehicle() != null) {
                Vehicle v = slot.getParkedVehicle();
                String vipTag = v.isVipMember() ? "[VIP]" : "";
                System.out.printf("Slot %02d  | %-16s | %-10s | %-8s | %-16s | %-20s\n",
                        slot.getSlotId(),
                        bayType,
                        "OCCUPIED",
                        v.getVehicleType(),
                        v.getVehicleNumber() + " " + vipTag,
                        v.getFormattedEntryTime());
            } else {
                System.out.printf("Slot %02d  | %-16s | %-10s | %-8s | %-16s | %-20s\n",
                        slot.getSlotId(),
                        bayType,
                        "AVAILABLE",
                        "-",
                        "-",
                        "-");
            }
        }
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.println("Summary: " + parkingLot.getOccupiedSlotCount() + " Occupied | "
                + parkingLot.getAvailableSlotCount() + " Available | Total: " + parkingLot.getTotalCapacity());
    }

    private void handleSearchVehicle() {
        System.out.println("--- [4] SEARCH FOR A VEHICLE ---");
        String regNumber = readSanitizedString("Enter Vehicle Registration Number to search: ");
        if (regNumber.isEmpty()) {
            System.out.println("❌ Registration number cannot be blank.");
            return;
        }

        ParkingSlot slot = parkingLot.findSlotByVehicleNumber(regNumber);
        if (slot != null && slot.getParkedVehicle() != null) {
            Vehicle v = slot.getParkedVehicle();
            java.time.Duration parkedDuration = java.time.Duration.between(v.getEntryTime(), LocalDateTime.now());
            long hours = parkedDuration.toHours();
            long mins = parkedDuration.toMinutes() % 60;

            System.out.println("\n🔍 VEHICLE FOUND!");
            System.out.println("==================================================");
            System.out.println("  Registration No : " + v.getVehicleNumber());
            System.out.println("  Vehicle Type    : " + v.getVehicleType());
            System.out.println("  Bay Category    : " + slot.getSlotType().getDisplayName());
            System.out.println("  Current Slot    : Slot " + slot.getSlotId());
            System.out.println("  VIP Pass        : " + (v.isVipMember() ? "YES" : "NO"));
            System.out.println("  Entry Time      : " + v.getFormattedEntryTime());
            System.out.println("  Time Elapsed    : " + hours + " hr " + mins + " min");
            System.out.println("  Hourly Rate     : ₹" + v.getHourlyRate() + "/hr");
            System.out.println("==================================================");
        } else {
            System.out.println("\n⚠️  No vehicle with registration [" + regNumber + "] found in the parking lot.");
        }
    }

    private void handleViewReport() {
        System.out.println("--- [5] PARKING LOT REPORT & FINANCIAL ANALYTICS ---");
        System.out.println("==================================================");
        System.out.println("  Total Slot Capacity       : " + parkingLot.getTotalCapacity());
        System.out.println("  Currently Occupied Slots  : " + parkingLot.getOccupiedSlotCount());
        System.out.println("  Currently Available Slots : " + parkingLot.getAvailableSlotCount());
        System.out.println("  Total Completed Exits     : " + parkingLot.getTotalVehiclesServed());
        System.out.println("--------------------------------------------------");
        System.out.println("  REVENUE BREAKDOWN BY VEHICLE TYPE:");
        System.out.printf ("    • Cars Revenue (₹20/hr)   : ₹%.2f\n", parkingLot.getCarRevenue());
        System.out.printf ("    • Bikes Revenue (₹10/hr)  : ₹%.2f\n", parkingLot.getBikeRevenue());
        System.out.printf ("    • EVs Revenue (₹25/hr)    : ₹%.2f\n", parkingLot.getEvRevenue());
        System.out.printf ("  TOTAL REVENUE COLLECTED   : ₹%.2f\n", parkingLot.getTotalFeesCollected());
        System.out.println("==================================================");

        List<Vehicle> active = parkingLot.getActiveVehicles();
        if (active.isEmpty()) {
            System.out.println("  Currently Active Vehicles: None (Lot is empty)");
        } else {
            System.out.println("  Active Parked Vehicles (" + active.size() + "):");
            for (int i = 0; i < active.size(); i++) {
                Vehicle v = active.get(i);
                System.out.printf("    %d. %s - %s%s (Entered: %s)\n",
                        (i + 1), v.getVehicleType(), v.getVehicleNumber(),
                        (v.isVipMember() ? " [VIP]" : ""), v.getFormattedEntryTime());
            }
        }
        System.out.println("==================================================");
    }

    private void handleViewFileHistory() {
        System.out.println("--- [6] PARKING EXIT HISTORY (FROM CSV FILE) ---");
        List<String[]> records = parkingLot.getFileStorageManager().readAllRecords();

        if (records.isEmpty()) {
            System.out.println("ℹ️  No completed exit transactions recorded in CSV yet.");
            return;
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-14s | %-6s | %-6s | %-18s | %-18s | %-14s | %-8s | %-6s\n",
                "REG NO", "TYPE", "SLOT", "ENTRY", "EXIT", "DURATION", "FEE", "VIP");
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        for (String[] row : records) {
            if (row.length >= 10) {
                System.out.printf("%-14s | %-6s | Slot %-2s | %-18s | %-18s | %-14s | ₹%-7s | %-6s\n",
                        row[0], row[1], row[2], row[3], row[4], row[5], row[8], row[9]);
            }
        }
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println("Total Persistent Records Logged: " + records.size() + " (Stored in 'data/parking_history.csv')");
    }

    private void handleExitApplication() {
        System.out.println("\n==================================================");
        System.out.println(" Thank you for using Parking Lot Management System!");
        System.out.printf (" Total Revenue for Session: ₹%.2f\n", parkingLot.getTotalFeesCollected());
        System.out.println(" Application shutdown cleanly. Goodbye!");
        System.out.println("==================================================");
    }

    private int readIntChoice(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.println("⚠️  Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Invalid input! Please enter a valid number.");
            }
        }
    }

    private String readSanitizedString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        if (input == null) return "";
        return input.trim().toUpperCase();
    }

    private void promptEnterToContinue() {
        System.out.print("\nPress [Enter] to return to main menu...");
        scanner.nextLine();
    }
}
