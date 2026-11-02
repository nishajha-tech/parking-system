/**
 * ParkingSlot Class
 * Demonstrates: Encapsulation, Enums, and State Management
 * 
 * Represents an individual parking space in the parking lot.
 * Supports specialized SlotTypes (Bike Bay, Car Bay, EV Charging Bay).
 */
public class ParkingSlot {

    // Private fields - Encapsulation
    private final int slotId;
    private final SlotType slotType;
    private boolean occupied;
    private Vehicle parkedVehicle;

    /**
     * Initializes an empty parking slot with default Car type
     */
    public ParkingSlot(int slotId) {
        this(slotId, SlotType.CAR);
    }

    /**
     * Initializes an empty parking slot with a designated SlotType
     * @param slotId 1-indexed slot identifier (e.g. 1 to 10)
     * @param slotType category of the slot (Bike, Car, EV Charging)
     */
    public ParkingSlot(int slotId, SlotType slotType) {
        this.slotId = slotId;
        this.slotType = slotType != null ? slotType : SlotType.CAR;
        this.occupied = false;
        this.parkedVehicle = null;
    }

    // Getters
    public int getSlotId() {
        return slotId;
    }

    public SlotType getSlotType() {
        return slotType;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    /**
     * Checks if a vehicle can be accommodated in this bay based on its type.
     */
    public boolean isCompatibleWith(Vehicle vehicle) {
        if (vehicle == null) return false;

        if (vehicle instanceof Bike) {
            // Bikes can fit in Bike bays or standard Car bays
            return slotType == SlotType.BIKE || slotType == SlotType.CAR;
        }

        if (vehicle instanceof EV) {
            EV ev = (EV) vehicle;
            if (ev.isChargingRequested()) {
                // Must be in EV Charging Bay if charging requested
                return slotType == SlotType.EV_CHARGING;
            }
            return slotType == SlotType.EV_CHARGING || slotType == SlotType.CAR;
        }

        if (vehicle instanceof Car) {
            // Regular cars use Car bays (or EV bays if vacant as overflow)
            return slotType == SlotType.CAR || slotType == SlotType.EV_CHARGING;
        }

        return true;
    }

    /**
     * Assigns a vehicle to this slot.
     * @param vehicle vehicle to park
     * @return true if assignment succeeded, false if occupied or incompatible
     */
    public boolean assignVehicle(Vehicle vehicle) {
        if (this.occupied || vehicle == null) {
            return false;
        }
        this.parkedVehicle = vehicle;
        this.occupied = true;
        return true;
    }

    /**
     * Releases the vehicle from this slot.
     * @return the Vehicle that was parked, or null if slot was already empty
     */
    public Vehicle releaseVehicle() {
        if (!this.occupied) {
            return null;
        }
        Vehicle departingVehicle = this.parkedVehicle;
        this.parkedVehicle = null;
        this.occupied = false;
        return departingVehicle;
    }

    /**
     * Returns a neat formatted string showing slot status
     */
    public String getStatusDescription() {
        if (occupied && parkedVehicle != null) {
            return String.format("Slot %02d [%s] : [OCCUPIED] - %s (%s) | Parked At: %s%s",
                    slotId,
                    slotType.getDisplayName(),
                    parkedVehicle.getVehicleNumber(),
                    parkedVehicle.getVehicleType(),
                    parkedVehicle.getFormattedEntryTime(),
                    (parkedVehicle.isVipMember() ? " [VIP]" : ""));
        } else {
            return String.format("Slot %02d [%s] : [AVAILABLE] - Empty", slotId, slotType.getDisplayName());
        }
    }

    @Override
    public String toString() {
        return getStatusDescription();
    }
}
