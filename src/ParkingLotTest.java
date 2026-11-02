import java.time.LocalDateTime;

/**
 * Automated Verification Test Suite for 24-Bay Commercial Parking Lot
 * Validates core and advanced OOP features:
 * - 24 Commercial Bays (Bike 1-6, Car 7-18, EV Charging 19-24)
 * - EV Fast Charging Service (+₹50)
 * - VIP Membership 20% Discount
 * - SlotType Specialization & Smart Allocation
 * - File I/O Persistence
 */
public class ParkingLotTest {

    private static int testsPassed = 0;
    private static int testsTotal = 0;

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println(" RUNNING AUTOMATED UNIT & INTEGRATION TESTS (24 BAYS LOT)   ");
        System.out.println("============================================================\n");

        testSuccessfulCarEntry();
        testSuccessfulBikeEntry();
        testElectricVehicleWithCharging();
        testVipMembershipDiscount();
        testSlotTypeSpecialization();
        testDuplicateVehiclePrevention();
        testLotFullScenario();
        testVehicleSearch();
        testVehicleExitAndFeeCalculation();
        testExitNonExistentVehicle();
        testSlotReleaseAndReassignment();
        testParkingReportAndRevenue();
        testCsvFilePersistence();

        System.out.println("\n============================================================");
        System.out.println(" TEST RESULTS: " + testsPassed + " / " + testsTotal + " TESTS PASSED");
        if (testsPassed == testsTotal) {
            System.out.println(" ALL 24-BAY TEST SCENARIOS PASSED WITH 100% SUCCESS!");
        } else {
            System.out.println(" SOME TESTS FAILED. PLEASE REVIEW ABOVE LOGS.");
        }
        System.out.println("============================================================");
    }

    private static void assertTrue(String testName, boolean condition, String detail) {
        testsTotal++;
        if (condition) {
            testsPassed++;
            System.out.println("  PASS: " + testName + " -> " + detail);
        } else {
            System.err.println("  FAIL: " + testName + " -> " + detail);
        }
    }

    private static void testSuccessfulCarEntry() {
        System.out.println("[Test 1] Testing Successful Car Entry into Car Bay (Slots 7-18)...");
        ParkingLot lot = new ParkingLot(24);
        try {
            Vehicle car = new Car("DL01CA1001");
            ParkingSlot slot = lot.parkVehicle(car);
            // Slots 1-6 are Bike, Slot 7 is first Car Bay
            assertTrue("Car Bay Assignment", slot != null && slot.getSlotId() == 7 && slot.getSlotType() == SlotType.CAR,
                    "Car correctly assigned to dedicated Car Bay: Slot " + slot.getSlotId());
        } catch (Exception e) {
            assertTrue("Car Bay Assignment", false, e.getMessage());
        }
    }

    private static void testSuccessfulBikeEntry() {
        System.out.println("\n[Test 2] Testing Successful Bike Entry into Bike Bay (Slots 1-6)...");
        ParkingLot lot = new ParkingLot(24);
        try {
            Vehicle bike = new Bike("DL02BI2002");
            ParkingSlot slot = lot.parkVehicle(bike);
            // Slots 1-6 are Bike Bays
            assertTrue("Bike Bay Assignment", slot != null && slot.getSlotId() == 1 && slot.getSlotType() == SlotType.BIKE,
                    "Bike assigned to Bike Bay: Slot 1 with rate ₹10/hr.");
        } catch (Exception e) {
            assertTrue("Bike Bay Assignment", false, e.getMessage());
        }
    }

    private static void testElectricVehicleWithCharging() {
        System.out.println("\n[Test 3] Testing EV Entry with Fast Charging Service (Slots 19-24)...");
        ParkingLot lot = new ParkingLot(24);
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime entry = now.minusHours(2); // 2 hours
            EV ev = new EV("DL08EV7777", true, entry, false);

            ParkingSlot slot = lot.parkVehicle(ev);
            // Slots 19-24 are EV Charging Bays
            assertTrue("EV Bay Allocation", slot != null && slot.getSlotId() == 19 && slot.getSlotType() == SlotType.EV_CHARGING,
                    "EV assigned to dedicated EV Charging Bay: Slot 19.");

            ParkingReceipt r = lot.processVehicleExit("DL08EV7777", now);
            // 2 hrs @ ₹25/hr = ₹50 + ₹50 charging dock fee = ₹100
            boolean feeMatch = (r.getTotalFee() == 100.0) && (r.getChargingFee() == 50.0);
            assertTrue("EV Charging Fee Calculation", feeMatch,
                    "Base: ₹" + r.getBaseFee() + " + Charging: ₹" + r.getChargingFee() + " = Total: ₹" + r.getTotalFee());
        } catch (Exception e) {
            assertTrue("EV Charging Test", false, e.getMessage());
        }
    }

    private static void testVipMembershipDiscount() {
        System.out.println("\n[Test 4] Testing VIP Membership Pass (20% Discount)...");
        ParkingLot lot = new ParkingLot(24);
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime entry = now.minusHours(2); // 2 hours @ ₹20/hr = ₹40
            Car vipCar = new Car("VIPCAR01", entry, true);

            lot.parkVehicle(vipCar);
            ParkingReceipt r = lot.processVehicleExit("VIPCAR01", now);

            // Subtotal: ₹40, VIP Discount (20%): ₹8, Total: ₹32
            boolean discountMatch = (r.isVip()) && (r.getVipDiscount() == 8.0) && (r.getTotalFee() == 32.0);
            assertTrue("VIP 20% Discount", discountMatch,
                    "Subtotal: ₹" + r.getBaseFee() + " - Discount: ₹" + r.getVipDiscount() + " = Final: ₹" + r.getTotalFee());
        } catch (Exception e) {
            assertTrue("VIP Discount Test", false, e.getMessage());
        }
    }

    private static void testSlotTypeSpecialization() {
        System.out.println("\n[Test 5] Testing Slot Specialization (6 Bikes in 1-6)...");
        ParkingLot lot = new ParkingLot(24);
        try {
            for (int i = 1; i <= 6; i++) {
                Vehicle b = new Bike("B" + i);
                ParkingSlot s = lot.parkVehicle(b);
                assertTrue("Bike in Bike Bay " + i, s.getSlotId() == i, "Bike " + i + " in Slot " + s.getSlotId());
            }

            // 7th bike overflows into Car Bay (Slot 7)
            Vehicle b7 = new Bike("B7");
            ParkingSlot s7 = lot.parkVehicle(b7);
            assertTrue("Bike overflow into Car Bay", s7.getSlotId() == 7, "Bike 7 overflow into Car Bay: Slot 7.");
        } catch (Exception e) {
            assertTrue("Slot Specialization", false, e.getMessage());
        }
    }

    private static void testDuplicateVehiclePrevention() {
        System.out.println("\n[Test 6] Testing Duplicate Vehicle Prevention...");
        ParkingLot lot = new ParkingLot(24);
        try {
            lot.parkVehicle(new Car("DL03DU1111"));
            try {
                lot.parkVehicle(new Bike("DL03DU1111"));
                assertTrue("Duplicate Check", false, "Failed! Duplicate vehicle allowed.");
            } catch (DuplicateVehicleException e) {
                assertTrue("Duplicate Check", true, "Duplicate caught: " + e.getMessage());
            }
        } catch (Exception e) {
            assertTrue("Duplicate Check", false, e.getMessage());
        }
    }

    private static void testLotFullScenario() {
        System.out.println("\n[Test 7] Testing 24-Bay Lot Full Scenario...");
        ParkingLot lot = new ParkingLot(24);
        try {
            for (int i = 1; i <= 6; i++) lot.parkVehicle(new Bike("B" + i));
            for (int i = 7; i <= 18; i++) lot.parkVehicle(new Car("C" + i));
            for (int i = 19; i <= 24; i++) lot.parkVehicle(new EV("E" + i, true));

            assertTrue("All 24 Bays Filled", lot.getOccupiedSlotCount() == 24 && lot.getAvailableSlotCount() == 0,
                    "Lot has 24/24 slots full.");

            try {
                lot.parkVehicle(new Car("OVERFLOW"));
                assertTrue("25th Vehicle Rejection", false, "Allowed vehicle when full.");
            } catch (ParkingLotFullException e) {
                assertTrue("25th Vehicle Rejection", true, "Rejected correctly: " + e.getMessage());
            }
        } catch (Exception e) {
            assertTrue("Lot Full Test", false, e.getMessage());
        }
    }

    private static void testVehicleSearch() {
        System.out.println("\n[Test 8] Testing Vehicle Search...");
        ParkingLot lot = new ParkingLot(24);
        try {
            lot.parkVehicle(new Car("HR26DK8888"));
            ParkingSlot found = lot.findSlotByVehicleNumber("HR26DK8888");
            assertTrue("Search Existing", found != null && found.getSlotId() == 7, "Car found in Slot 7.");

            ParkingSlot notFound = lot.findSlotByVehicleNumber("UNKNOWN999");
            assertTrue("Search Absent", notFound == null, "Absent vehicle returns null.");
        } catch (Exception e) {
            assertTrue("Search Test", false, e.getMessage());
        }
    }

    private static void testVehicleExitAndFeeCalculation() {
        System.out.println("\n[Test 9] Testing Exit and Partial-Hour Round-up Rules...");
        ParkingLot lot = new ParkingLot(24);
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime carEntry = now.minusHours(2).minusMinutes(15);
            lot.parkVehicle(new Car("DL01TESTCAR", carEntry));

            ParkingReceipt r = lot.processVehicleExit("DL01TESTCAR", now);
            boolean feeCorrect = (r.getChargeableHours() == 3) && (r.getTotalFee() == 60.0);
            assertTrue("Car Exit (2h 15m -> 3h @ ₹20)", feeCorrect, "Hours: " + r.getChargeableHours() + ", Fee: ₹" + r.getTotalFee());
        } catch (Exception e) {
            assertTrue("Exit Calculation", false, e.getMessage());
        }
    }

    private static void testExitNonExistentVehicle() {
        System.out.println("\n[Test 10] Testing Exit for Non-existent Vehicle...");
        ParkingLot lot = new ParkingLot(24);
        try {
            try {
                lot.processVehicleExit("GHOST9999");
                assertTrue("Ghost Exit Check", false, "Allowed exit for non-existent vehicle.");
            } catch (VehicleNotFoundException e) {
                assertTrue("Ghost Exit Check", true, "VehicleNotFoundException thrown correctly: " + e.getMessage());
            }
        } catch (Exception e) {
            assertTrue("Ghost Exit Check", false, e.getMessage());
        }
    }

    private static void testSlotReleaseAndReassignment() {
        System.out.println("\n[Test 11] Testing Slot Release and Reuse...");
        ParkingLot lot = new ParkingLot(24);
        try {
            Vehicle car1 = new Car("CAR1");
            ParkingSlot s = lot.parkVehicle(car1);
            int slotId = s.getSlotId();

            lot.processVehicleExit("CAR1");
            assertTrue("Slot Released", !s.isOccupied(), "Slot " + slotId + " is now available.");

            Vehicle car2 = new Car("CAR2");
            ParkingSlot reassigned = lot.parkVehicle(car2);
            assertTrue("Slot Reused", reassigned.getSlotId() == slotId, "Slot " + slotId + " was immediately reused.");
        } catch (Exception e) {
            assertTrue("Slot Release & Reuse", false, e.getMessage());
        }
    }

    private static void testParkingReportAndRevenue() {
        System.out.println("\n[Test 12] Testing Financial Revenue Breakdown...");
        ParkingLot lot = new ParkingLot(24);
        try {
            LocalDateTime now = LocalDateTime.now();
            lot.parkVehicle(new Car("C1", now.minusHours(1))); // ₹20
            lot.parkVehicle(new Bike("B1", now.minusHours(1))); // ₹10

            lot.processVehicleExit("C1", now);
            lot.processVehicleExit("B1", now);

            boolean revenueMatch = (lot.getCarRevenue() == 20.0) && (lot.getBikeRevenue() == 10.0)
                    && (lot.getTotalFeesCollected() == 30.0);
            assertTrue("Revenue Breakdown", revenueMatch,
                    "Car: ₹" + lot.getCarRevenue() + ", Bike: ₹" + lot.getBikeRevenue() + ", Total: ₹" + lot.getTotalFeesCollected());
        } catch (Exception e) {
            assertTrue("Revenue Breakdown Check", false, e.getMessage());
        }
    }

    private static void testCsvFilePersistence() {
        System.out.println("\n[Test 13] Testing CSV Data Persistence...");
        ParkingLot lot = new ParkingLot(24);
        try {
            LocalDateTime now = LocalDateTime.now();
            lot.parkVehicle(new Bike("PERSIST_BIKE", now.minusHours(1)));
            lot.processVehicleExit("PERSIST_BIKE", now);

            int recordsCount = lot.getFileStorageManager().readAllRecords().size();
            assertTrue("CSV File History", recordsCount > 0, "Successfully logged exit record to 'data/parking_history.csv'. Records: " + recordsCount);
        } catch (Exception e) {
            assertTrue("CSV Persistence Check", false, e.getMessage());
        }
    }
}
