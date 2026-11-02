/**
 * Billable Interface
 * Demonstrates: Interface Implementation (OOP Concept)
 * 
 * Defines the contract for any entity whose parking fee can be calculated.
 * Subclasses of Vehicle will implement this interface to provide their
 * respective hourly rates and calculate charges.
 */
public interface Billable {

    /**
     * Returns the hourly parking rate for the vehicle type.
     * @return rate per hour in Rupees
     */
    double getHourlyRate();

    /**
     * Calculates total parking fee based on chargeable hours.
     * @param chargeableHours number of started hours (minimum 1)
     * @return total fee in Rupees
     */
    double calculateFee(long chargeableHours);
}
