
interface Alertable {
    /**
     * Sends an alert message to the relevant partner(s).
     * @param message the alert content
     */
    void sendAlert(String message);

}


// =============================================================================


public class VehicleBooking implements Alertable {

    // ===================== Fields =====================
    private String bookingDate;         // Format: "DD/MM/YYYY"
    private int startHour;              // 0–23
    private int endHour;                // 0–23, must be > startHour
    private Partner bookingPartner;
    private boolean isCancelled;

    // ===================== Constructors =====================

    public VehicleBooking() {
        this.bookingDate = "01/01/2000";
        this.startHour = 0;
        this.endHour = 1;
        this.bookingPartner = null;
        this.isCancelled = false;
    }

    public VehicleBooking(String bookingDate, int startHour, int endHour, Partner bookingPartner) {
        if (endHour <= startHour) {
            throw new IllegalArgumentException("End hour (" + endHour + ") must be after start hour (" + startHour + ").");
        }
        this.bookingDate = bookingDate;
        this.startHour = startHour;
        this.endHour = endHour;
        this.bookingPartner = bookingPartner;
        this.isCancelled = false;
    }

    // ===================== Getters & Setters =====================

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public int getStartHour() { return startHour; }
    public void setStartHour(int startHour) { this.startHour = startHour; }

    public int getEndHour() { return endHour; }
    public void setEndHour(int endHour) { this.endHour = endHour; }

    public Partner getBookingPartner() { return bookingPartner; }
    public void setBookingPartner(Partner bookingPartner) { this.bookingPartner = bookingPartner; }

    public boolean isCancelled() { return isCancelled; }

    // ===================== Methods =====================

    /**
     * Checks whether this booking overlaps with another booking on the same date.
     * @param other another VehicleBooking to compare against
     * @return true if there is a time conflict
     */
    public boolean validateNoOverlap(VehicleBooking other) {
        if (!this.bookingDate.equals(other.bookingDate)) return false; // different days — no conflict
        if (other.isCancelled) return false;

        // Overlap exists if one booking starts before the other ends
        return this.startHour < other.endHour && other.startHour < this.endHour;
    }


    // ===================== Alertable Implementation =====================

    @Override
    public void sendAlert(String message) {
        String partnerName = (bookingPartner != null) ? bookingPartner.getName() : "Unknown";
        // Uses String method: startsWith
        String prefix = message.startsWith("⚠") ? "" : "🔔 ALERT → ";
        System.out.println(prefix + "[" + partnerName + "] " + message);
    }

    @Override
    public String toString() {
        String partnerName = (bookingPartner != null) ? bookingPartner.getName() : "None";
        String status = isCancelled ? "CANCELLED" : "ACTIVE";
        return "VehicleBooking{" +
                "date='" + bookingDate + '\'' +
                ", time=" + startHour + ":00–" + endHour + ":00" +
                ", partner='" + partnerName + '\'' +
                ", status=" + status +
                '}';
    }
}
