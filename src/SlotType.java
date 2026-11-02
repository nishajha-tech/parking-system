/**
 * SlotType Enum
 * Demonstrates: Java Enumerations (Enums) & Domain Typing
 * 
 * Defines specialized parking bay categories:
 * - BIKE: Dedicated spaces for two-wheelers (Slots 1-6)
 * - CAR: Standard sized spaces for regular cars (Slots 7-18)
 * - EV_CHARGING: Premium bays equipped with electric charging docks (Slots 19-24)
 */
public enum SlotType {
    BIKE("Bike Bay", 10.0),
    CAR("Car Bay", 20.0),
    EV_CHARGING("EV Charging Bay", 25.0);

    private final String displayName;
    private final double defaultRate;

    SlotType(String displayName, double defaultRate) {
        this.displayName = displayName;
        this.defaultRate = defaultRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDefaultRate() {
        return defaultRate;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
