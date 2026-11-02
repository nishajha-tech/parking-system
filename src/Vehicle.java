import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Vehicle (Abstract Class)
 * Demonstrates: 
 *  1. Abstraction (cannot be instantiated directly; defines abstract contract)
 *  2. Encapsulation (private fields with public getters/setters)
 *  3. Interface Implementation (implements Billable contract)
 */
public abstract class Vehicle implements Billable {

    // Private fields - Encapsulation
    private final String vehicleNumber;
    private final String vehicleType;
    private final LocalDateTime entryTime;
    private boolean vipMember; // New Feature: VIP/Member pass holder

    // Standard date-time formatter for user-friendly receipt and slot display
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    /**
     * Constructor for Vehicle with current entry timestamp
     * @param vehicleNumber vehicle registration plate number (sanitized)
     * @param vehicleType type of vehicle (e.g., "Car", "Bike", "EV")
     */
    public Vehicle(String vehicleNumber, String vehicleType) {
        this(vehicleNumber, vehicleType, LocalDateTime.now(), false);
    }

    /**
     * Overloaded constructor with custom entry time
     */
    public Vehicle(String vehicleNumber, String vehicleType, LocalDateTime entryTime) {
        this(vehicleNumber, vehicleType, entryTime, false);
    }

    /**
     * Full constructor including VIP membership flag
     */
    public Vehicle(String vehicleNumber, String vehicleType, LocalDateTime entryTime, boolean vipMember) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.entryTime = entryTime != null ? entryTime : LocalDateTime.now();
        this.vipMember = vipMember;
    }

    // Public Getters and Setters (Encapsulation)
    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public boolean isVipMember() {
        return vipMember;
    }

    public void setVipMember(boolean vipMember) {
        this.vipMember = vipMember;
    }

    /**
     * Formats the entry timestamp into a human-readable string.
     */
    public String getFormattedEntryTime() {
        return entryTime.format(FORMATTER);
    }

    /**
     * Abstract method from Billable interface.
     * Each child class (Car, Bike, EV) MUST provide its own implementation.
     * Demonstrates Polymorphism & Abstraction.
     */
    @Override
    public abstract double getHourlyRate();

    /**
     * Implements Billable interface method.
     * Base calculation common to all vehicles: chargeableHours * hourlyRate.
     * Subclasses can override or extend this calculation.
     */
    @Override
    public double calculateFee(long chargeableHours) {
        if (chargeableHours <= 0) {
            chargeableHours = 1; // Minimum charge is 1 hour
        }
        return chargeableHours * getHourlyRate();
    }

    @Override
    public String toString() {
        return String.format("[%s | Number: %s | Entry: %s | Rate: ₹%.1f/hr | VIP: %s]",
                vehicleType, vehicleNumber, getFormattedEntryTime(), getHourlyRate(), (vipMember ? "YES" : "NO"));
    }
}
