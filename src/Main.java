/**
 * Main Class - Application Entry Point
 * Demonstrates: Object instantiation and application bootstrapping
 * 
 * Project: Parking Lot Management System in Java
 * Target: Second-Year CSE College Project & Viva
 */
public class Main {

    public static void main(String[] args) {
        // Initialize Parking Lot with 10 slots as per project requirements
        ParkingLot parkingLot = new ParkingLot(ParkingLot.DEFAULT_CAPACITY);

        // Initialize Console User Interface Manager
        ParkingManager manager = new ParkingManager(parkingLot);

        // Start the menu-driven application
        manager.start();
    }
}
