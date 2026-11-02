import java.time.LocalDateTime;

/**
 * EV (Electric Vehicle) Class
 * Demonstrates:
 *  1. Inheritance (EV extends Vehicle)
 *  2. Polymorphism (overrides getHourlyRate to return ₹25/hr and custom calculateFee)
 *  3. Specialized Domain Behavior (supports on-demand EV charging service)
 */
public class EV extends Vehicle {

    // Specific hourly rate for Electric Vehicles
    public static final double HOURLY_RATE = 25.0;

    // Optional flat service fee for using the high-speed charging dock
    public static final double FLAT_CHARGING_FEE = 50.0;

    private final boolean chargingRequested;

    public EV(String vehicleNumber) {
        this(vehicleNumber, false, LocalDateTime.now(), false);
    }

    public EV(String vehicleNumber, boolean chargingRequested) {
        this(vehicleNumber, chargingRequested, LocalDateTime.now(), false);
    }

    public EV(String vehicleNumber, boolean chargingRequested, LocalDateTime entryTime, boolean isVip) {
        super(vehicleNumber, "EV", entryTime, isVip);
        this.chargingRequested = chargingRequested;
    }

    public boolean isChargingRequested() {
        return chargingRequested;
    }

    @Override
    public double getHourlyRate() {
        return HOURLY_RATE;
    }

    /**
     * Overrides calculateFee to add charging dock fee if requested.
     * Demonstrates Runtime Polymorphism.
     */
    @Override
    public double calculateFee(long chargeableHours) {
        double baseFee = super.calculateFee(chargeableHours);
        if (chargingRequested) {
            baseFee += FLAT_CHARGING_FEE;
        }
        return baseFee;
    }

    @Override
    public String toString() {
        return String.format("[%s | Number: %s | Entry: %s | Rate: ₹%.1f/hr | Charging: %s | VIP: %s]",
                getVehicleType(), getVehicleNumber(), getFormattedEntryTime(),
                getHourlyRate(), (chargingRequested ? "YES (+₹50)" : "NO"),
                (isVipMember() ? "YES" : "NO"));
    }
}
