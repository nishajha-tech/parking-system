import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ParkingLot Class
 * Demonstrates:
 *  1. Collections (ArrayList of ParkingSlot objects)
 *  2. Scalable Capacity Management (Default 24 designated bays)
 *  3. Encapsulation & Separation of Concerns (Business Logic)
 *  4. Polymorphism & Smart Slot Allocation by SlotType
 *  5. File Persistence (FileStorageManager integration)
 *  6. Custom Exception Handling
 */
public class ParkingLot {

    // Upgraded default capacity to 24 commercial bays
    public static final int DEFAULT_CAPACITY = 24;
    public static final double VIP_DISCOUNT_PERCENT = 0.20; // 20% discount for VIP pass holders

    private final int totalCapacity;
    private final List<ParkingSlot> slots;
    private final FileStorageManager fileStorageManager;

    private double totalFeesCollected;
    private int totalVehiclesServed;
    private double carRevenue;
    private double bikeRevenue;
    private double evRevenue;

    /**
     * Initializes a Parking Lot with default 24 specialized slots
     */
    public ParkingLot() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Initializes a Parking Lot with configurable capacity.
     * Proportionally distributes bays:
     * - First 25% (Slots 1-6 for cap 24): BIKE
     * - Next 50% (Slots 7-18 for cap 24): CAR
     * - Last 25% (Slots 19-24 for cap 24): EV_CHARGING
     */
    public ParkingLot(int capacity) {
        this.totalCapacity = (capacity > 0) ? capacity : DEFAULT_CAPACITY;
        this.slots = new ArrayList<>(this.totalCapacity);
        this.fileStorageManager = new FileStorageManager();
        this.totalFeesCollected = 0.0;
        this.totalVehiclesServed = 0;
        this.carRevenue = 0.0;
        this.bikeRevenue = 0.0;
        this.evRevenue = 0.0;

        int bikeLimit = Math.max(1, (int) Math.round(this.totalCapacity * 0.25));
        int carLimit = Math.max(bikeLimit + 1, (int) Math.round(this.totalCapacity * 0.75));

        // Initialize specialized parking bays (1-indexed)
        for (int i = 1; i <= this.totalCapacity; i++) {
            SlotType type;
            if (i <= bikeLimit) {
                type = SlotType.BIKE;
            } else if (i <= carLimit) {
                type = SlotType.CAR;
            } else {
                type = SlotType.EV_CHARGING;
            }
            this.slots.add(new ParkingSlot(i, type));
        }
    }

    /**
     * Smart Slot Allocation:
     * First searches for a vacant slot matching the vehicle's primary type.
     * If all primary slots are occupied, searches for any vacant compatible overflow slot.
     */
    public ParkingSlot findBestAvailableSlot(Vehicle vehicle) {
        if (vehicle == null) return null;

        // Pass 1: Try finding an exact designated slot
        for (ParkingSlot slot : slots) {
            if (!slot.isOccupied()) {
                if (vehicle instanceof Bike && slot.getSlotType() == SlotType.BIKE) {
                    return slot;
                }
                if (vehicle instanceof EV && ((EV) vehicle).isChargingRequested()
                        && slot.getSlotType() == SlotType.EV_CHARGING) {
                    return slot;
                }
                if (vehicle instanceof Car && slot.getSlotType() == SlotType.CAR) {
                    return slot;
                }
            }
        }

        // Pass 2: Fallback to any compatible empty slot
        for (ParkingSlot slot : slots) {
            if (!slot.isOccupied() && slot.isCompatibleWith(vehicle)) {
                return slot;
            }
        }

        return null;
    }

    /**
     * Case-insensitive search for a slot by vehicle number
     */
    public ParkingSlot findSlotByVehicleNumber(String vehicleNumber) {
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            return null;
        }
        String cleanNumber = vehicleNumber.trim().toUpperCase();
        for (ParkingSlot slot : slots) {
            if (slot.isOccupied() && slot.getParkedVehicle() != null) {
                if (slot.getParkedVehicle().getVehicleNumber().equalsIgnoreCase(cleanNumber)) {
                    return slot;
                }
            }
        }
        return null;
    }

    /**
     * Checks if a vehicle is already parked in the lot
     */
    public boolean isVehicleParked(String vehicleNumber) {
        return findSlotByVehicleNumber(vehicleNumber) != null;
    }

    /**
     * Parks a vehicle into the best available slot.
     */
    public ParkingSlot parkVehicle(Vehicle vehicle)
            throws DuplicateVehicleException, ParkingLotFullException, InvalidVehicleDataException {

        if (vehicle == null || vehicle.getVehicleNumber() == null || vehicle.getVehicleNumber().trim().isEmpty()) {
            throw new InvalidVehicleDataException("Error: Invalid vehicle details. Registration number cannot be empty.");
        }

        // Rule: Prevent duplicate entry
        if (isVehicleParked(vehicle.getVehicleNumber())) {
            throw new DuplicateVehicleException("Error: Vehicle [" + vehicle.getVehicleNumber() +
                    "] is ALREADY parked in the lot! Duplicate entry not allowed.");
        }

        // Rule: Find compatible vacant slot
        ParkingSlot availableSlot = findBestAvailableSlot(vehicle);
        if (availableSlot == null) {
            throw new ParkingLotFullException("Error: No available parking bay can accommodate this " +
                    vehicle.getVehicleType() + "! All " + totalCapacity + " bays are occupied or incompatible.");
        }

        // Assign vehicle to slot
        availableSlot.assignVehicle(vehicle);
        return availableSlot;
    }

    /**
     * Processes exit for a parked vehicle with current timestamp
     */
    public ParkingReceipt processVehicleExit(String vehicleNumber) throws VehicleNotFoundException {
        return processVehicleExit(vehicleNumber, LocalDateTime.now());
    }

    /**
     * Overloaded method allowing custom exit time
     */
    public ParkingReceipt processVehicleExit(String vehicleNumber, LocalDateTime exitTime)
            throws VehicleNotFoundException {

        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            throw new VehicleNotFoundException("Error: Registration number cannot be empty.");
        }

        ParkingSlot slot = findSlotByVehicleNumber(vehicleNumber);
        if (slot == null) {
            throw new VehicleNotFoundException("Error: Vehicle [" + vehicleNumber.trim().toUpperCase() +
                    "] is NOT parked in this parking lot.");
        }

        Vehicle vehicle = slot.getParkedVehicle();
        LocalDateTime entryTime = vehicle.getEntryTime();
        if (exitTime == null || exitTime.isBefore(entryTime)) {
            exitTime = LocalDateTime.now();
        }

        // Calculate parking duration
        Duration duration = Duration.between(entryTime, exitTime);
        long chargeableHours = calculateChargeableHours(duration);

        // Calculate base fee (hourlyRate * hours)
        double baseFee = chargeableHours * vehicle.getHourlyRate();

        // Calculate charging fee if vehicle is EV and charging was used
        double chargingFee = 0.0;
        if (vehicle instanceof EV && ((EV) vehicle).isChargingRequested()) {
            chargingFee = EV.FLAT_CHARGING_FEE;
        }

        double subtotal = baseFee + chargingFee;

        // VIP Discount (20% off) if vehicle is VIP member
        double vipDiscount = 0.0;
        if (vehicle.isVipMember()) {
            vipDiscount = subtotal * VIP_DISCOUNT_PERCENT;
        }

        double totalFee = subtotal - vipDiscount;

        // Release the slot
        int slotId = slot.getSlotId();
        slot.releaseVehicle();

        // Update overall session metrics
        this.totalFeesCollected += totalFee;
        this.totalVehiclesServed++;

        if (vehicle instanceof Car) {
            this.carRevenue += totalFee;
        } else if (vehicle instanceof Bike) {
            this.bikeRevenue += totalFee;
        } else if (vehicle instanceof EV) {
            this.evRevenue += totalFee;
        }

        // Build exit receipt
        ParkingReceipt receipt = new ParkingReceipt(
                vehicle.getVehicleNumber(),
                vehicle.getVehicleType(),
                slotId,
                entryTime,
                exitTime,
                duration,
                chargeableHours,
                vehicle.getHourlyRate(),
                baseFee,
                chargingFee,
                vehicle.isVipMember(),
                vipDiscount,
                totalFee
        );

        // Persist to CSV file
        fileStorageManager.appendExitRecord(receipt);

        return receipt;
    }

    /**
     * Calculates chargeable hours:
     * - Minimum charge: 1 hour
     * - Positive partial hour rounds up to next chargeable hour
     */
    public static long calculateChargeableHours(Duration duration) {
        long totalSeconds = duration.getSeconds();
        if (totalSeconds <= 0) {
            return 1;
        }

        long fullHours = totalSeconds / 3600;
        long remainderSeconds = totalSeconds % 3600;

        long chargeableHours = (remainderSeconds > 0) ? fullHours + 1 : fullHours;
        return Math.max(1, chargeableHours);
    }

    // Getters and reporting

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public List<ParkingSlot> getAllSlots() {
        return Collections.unmodifiableList(slots);
    }

    public int getOccupiedSlotCount() {
        int count = 0;
        for (ParkingSlot slot : slots) {
            if (slot.isOccupied()) count++;
        }
        return count;
    }

    public int getAvailableSlotCount() {
        return totalCapacity - getOccupiedSlotCount();
    }

    public double getTotalFeesCollected() {
        return totalFeesCollected;
    }

    public int getTotalVehiclesServed() {
        return totalVehiclesServed;
    }

    public double getCarRevenue() { return carRevenue; }
    public double getBikeRevenue() { return bikeRevenue; }
    public double getEvRevenue() { return evRevenue; }

    public FileStorageManager getFileStorageManager() {
        return fileStorageManager;
    }

    public List<Vehicle> getActiveVehicles() {
        List<Vehicle> activeList = new ArrayList<>();
        for (ParkingSlot slot : slots) {
            if (slot.isOccupied() && slot.getParkedVehicle() != null) {
                activeList.add(slot.getParkedVehicle());
            }
        }
        return activeList;
    }
}
