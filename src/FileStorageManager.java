import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * FileStorageManager
 * Demonstrates: Java File I/O, Data Persistence, and CSV Handling
 * 
 * Records every completed exit transaction to a persistent CSV log file
 * so records can be audited, viewed, and exported even after program restart.
 */
public class FileStorageManager {

    private static final String DATA_DIR = "data";
    private static final String HISTORY_FILE = "data/parking_history.csv";
    private static final String CSV_HEADER = "VehicleNumber,VehicleType,SlotId,EntryTime,ExitTime,Duration,ChargeableHours,HourlyRate,TotalFee,IsVip";

    public FileStorageManager() {
        ensureDataDirectoryExists();
    }

    private void ensureDataDirectoryExists() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(HISTORY_FILE);
        if (!file.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
                pw.println(CSV_HEADER);
            } catch (IOException e) {
                System.err.println("Warning: Unable to initialize CSV history file: " + e.getMessage());
            }
        }
    }

    /**
     * Appends an exit receipt record to the CSV file
     */
    public synchronized void appendExitRecord(ParkingReceipt receipt) {
        ensureDataDirectoryExists();
        try (PrintWriter pw = new PrintWriter(new FileWriter(HISTORY_FILE, StandardCharsets.UTF_8, true))) {
            String line = String.format("%s,%s,%d,%s,%s,%s,%d,%.2f,%.2f,%s",
                    receipt.getVehicleNumber(),
                    receipt.getVehicleType(),
                    receipt.getSlotId(),
                    receipt.getEntryTime().format(Vehicle.FORMATTER),
                    receipt.getExitTime().format(Vehicle.FORMATTER),
                    receipt.getFormattedDuration(),
                    receipt.getChargeableHours(),
                    receipt.getHourlyRate(),
                    receipt.getTotalFee(),
                    (receipt.isVip() ? "YES" : "NO")
            );
            pw.println(line);
        } catch (IOException e) {
            System.err.println("Error saving exit record to CSV: " + e.getMessage());
        }
    }

    /**
     * Reads all history records from CSV file
     */
    public List<String[]> readAllRecords() {
        List<String[]> records = new ArrayList<>();
        File file = new File(HISTORY_FILE);
        if (!file.exists()) {
            return records;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirst = true;
            while ((line = br.readLine()) != null) {
                if (isFirst) {
                    isFirst = false; // Skip CSV header
                    continue;
                }
                if (!line.trim().isEmpty()) {
                    records.add(line.split(","));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading history CSV: " + e.getMessage());
        }
        return records;
    }

    /**
     * Returns raw CSV file content for web download
     */
    public String getRawCsvContent() {
        File file = new File(HISTORY_FILE);
        if (!file.exists()) {
            return CSV_HEADER + "\n";
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            return CSV_HEADER + "\n";
        }
        return sb.toString();
    }
}
