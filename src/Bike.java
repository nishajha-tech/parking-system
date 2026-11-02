import java.time.LocalDateTime;

/**
 * Bike Class
 * Demonstrates:
 *  1. Inheritance (Bike extends Vehicle)
 *  2. Polymorphism (overrides getHourlyRate to return ₹10/hr)
 */
public class Bike extends Vehicle {

    // Specific hourly rate for Bikes
    private static final double HOURLY_RATE = 10.0;

    public Bike(String vehicleNumber) {
        super(vehicleNumber, "Bike");
    }

    public Bike(String vehicleNumber, LocalDateTime entryTime) {
        super(vehicleNumber, "Bike", entryTime, false);
    }

    public Bike(String vehicleNumber, LocalDateTime entryTime, boolean isVip) {
        super(vehicleNumber, "Bike", entryTime, isVip);
    }

    @Override
    public double getHourlyRate() {
        return HOURLY_RATE;
    }
}
