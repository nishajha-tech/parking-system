import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ParkingReceipt Class
 * Demonstrates: Encapsulation, Single Responsibility Principle & Financial Auditing
 * 
 * Captures all details of a completed parking session:
 * - Base parking charges
 * - On-demand add-ons (EV charging dock fee)
 * - Discounts (VIP 20% discount)
 * - Final net bill
 */
public class ParkingReceipt {

    private final String vehicleNumber;
    private final String vehicleType;
    private final int slotId;
    private final LocalDateTime entryTime;
    private final LocalDateTime exitTime;
    private final Duration duration;
    private final long chargeableHours;
    private final double hourlyRate;
    private final double baseFee;
    private final double chargingFee;
    private final boolean isVip;
    private final double vipDiscount;
    private final double totalFee;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public ParkingReceipt(String vehicleNumber, String vehicleType, int slotId,
                          LocalDateTime entryTime, LocalDateTime exitTime,
                          Duration duration, long chargeableHours,
                          double hourlyRate, double baseFee, double chargingFee,
                          boolean isVip, double vipDiscount, double totalFee) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.slotId = slotId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.duration = duration;
        this.chargeableHours = chargeableHours;
        this.hourlyRate = hourlyRate;
        this.baseFee = baseFee;
        this.chargingFee = chargingFee;
        this.isVip = isVip;
        this.vipDiscount = vipDiscount;
        this.totalFee = totalFee;
    }

    public String getVehicleNumber() { return vehicleNumber; }
    public String getVehicleType() { return vehicleType; }
    public int getSlotId() { return slotId; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public Duration getDuration() { return duration; }
    public long getChargeableHours() { return chargeableHours; }
    public double getHourlyRate() { return hourlyRate; }
    public double getBaseFee() { return baseFee; }
    public double getChargingFee() { return chargingFee; }
    public boolean isVip() { return isVip; }
    public double getVipDiscount() { return vipDiscount; }
    public double getTotalFee() { return totalFee; }

    /**
     * Formats duration into "X hours Y mins Z secs"
     */
    public String getFormattedDuration() {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append(" hr ");
        }
        sb.append(minutes).append(" min ");
        sb.append(seconds).append(" sec");
        return sb.toString().trim();
    }

    /**
     * Generates a beautifully formatted parking bill receipt
     */
    public String generateReceipt() {
        String border = "============================================================";
        String line   = "------------------------------------------------------------";

        StringBuilder sb = new StringBuilder();
        sb.append(border).append("\n");
        sb.append("                PARKING LOT EXIT RECEIPT                    \n");
        sb.append(border).append("\n");
        sb.append(String.format("  Vehicle Number    : %s %s\n", vehicleNumber, (isVip ? "⭐ [VIP MEMBER]" : "")));
        sb.append(String.format("  Vehicle Type      : %s\n", vehicleType));
        sb.append(String.format("  Assigned Slot     : Slot %d\n", slotId));
        sb.append(line).append("\n");
        sb.append(String.format("  Entry Timestamp   : %s\n", entryTime.format(FORMATTER)));
        sb.append(String.format("  Exit Timestamp    : %s\n", exitTime.format(FORMATTER)));
        sb.append(String.format("  Actual Duration   : %s\n", getFormattedDuration()));
        sb.append(String.format("  Chargeable Hours  : %d hour(s) [Round-up rule, Min 1 hr]\n", chargeableHours));
        sb.append(String.format("  Base Hourly Rate  : ₹%.2f / hour\n", hourlyRate));
        sb.append(String.format("  Base Parking Fee  : ₹%.2f\n", baseFee));

        if (chargingFee > 0) {
            sb.append(String.format("  EV Charging Dock  : +₹%.2f (Flat Service Fee)\n", chargingFee));
        }

        if (isVip && vipDiscount > 0) {
            sb.append(String.format("  VIP Discount (20%): -₹%.2f\n", vipDiscount));
        }

        sb.append(line).append("\n");
        sb.append(String.format("  TOTAL AMOUNT PAID : ₹%.2f\n", totalFee));
        sb.append("  PAYMENT STATUS    : SUCCESS (Paid via UPI / Cash / Card)\n");
        sb.append(border).append("\n");
        sb.append("         Thank you for parking with us! Drive Safe!         \n");
        sb.append(border).append("\n");

        return sb.toString();
    }
}
