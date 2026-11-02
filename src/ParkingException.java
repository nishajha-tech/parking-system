/**
 * Custom Exception Hierarchy for Parking Lot Management System
 * Demonstrates: Exception Handling (OOP Concept)
 */
public class ParkingException extends Exception {
    public ParkingException(String message) {
        super(message);
    }
}

/**
 * Thrown when attempting to park in a lot that has no available slots.
 */
class ParkingLotFullException extends ParkingException {
    public ParkingLotFullException(String message) {
        super(message);
    }
}

/**
 * Thrown when attempting to park a vehicle that is already currently parked.
 */
class DuplicateVehicleException extends ParkingException {
    public DuplicateVehicleException(String message) {
        super(message);
    }
}

/**
 * Thrown when an action (exit, search) targets a vehicle registration number not currently parked.
 */
class VehicleNotFoundException extends ParkingException {
    public VehicleNotFoundException(String message) {
        super(message);
    }
}

/**
 * Thrown when user inputs invalid data (e.g. empty registration, invalid type).
 */
class InvalidVehicleDataException extends ParkingException {
    public InvalidVehicleDataException(String message) {
        super(message);
    }
}
