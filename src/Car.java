import java.time.LocalDateTime;

/**
 * Car Class
 * Demonstrates:
 *  1. Inheritance (Car extends Vehicle)
 *  2. Polymorphism (overrides getHourlyRate to return ₹20/hr)
 */
public class Car extends Vehicle {

    // Specific hourly rate for regular Cars
    private static final double HOURLY_RATE = 20.0;

    public Car(String vehicleNumber) {
        super(vehicleNumber, "Car");
    }

    public Car(String vehicleNumber, LocalDateTime entryTime) {
        super(vehicleNumber, "Car", entryTime, false);
    }

    public Car(String vehicleNumber, LocalDateTime entryTime, boolean isVip) {
        super(vehicleNumber, "Car", entryTime, isVip);
    }

    @Override
    public double getHourlyRate() {
        return HOURLY_RATE;
    }
}
