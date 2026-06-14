/**
 * Room & Vroom - Shared Household Management System
 * Authors: גל קסירר (318158466), אסף שוורץ (207812744), אסף חיון (214195331)
 *
 * SharedVehicle.java - Manages the physical vehicle and its booking schedule.
 *
 * Arrays used:
 *   - bookingList[]        : 1D array of VehicleBooking objects
 *   - weeklySchedule[][]   : 2D boolean array [day 0-6][hour 0-23] → true = occupied
 */
public class SharedVehicle {

    // ===================== Constants =====================
    private static final int MAX_BOOKINGS = 50;
    private static final int DAYS_IN_WEEK = 7;
    private static final int HOURS_IN_DAY = 24;

    // Day name lookup
    private static final String[] DAY_NAMES = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

    // ===================== Fields =====================
    private String licensePlate;
    private double mileage;
    private VehicleBooking[] bookingList;    // 1D array — all bookings
    private int bookingCount;

    // 2D array: weeklySchedule[day][hour] = true if that slot is taken
    private boolean[][] weeklySchedule;     // [0-6 days][0-23 hours]

    // ===================== Constructors =====================

    public SharedVehicle() {
        this.licensePlate = "000-00-000";
        this.mileage = 0.0;
        this.bookingList = new VehicleBooking[MAX_BOOKINGS];
        this.bookingCount = 0;
        this.weeklySchedule = new boolean[DAYS_IN_WEEK][HOURS_IN_DAY];
    }

    public SharedVehicle(String licensePlate, double mileage) {
        this.licensePlate = licensePlate;
        this.mileage = mileage;
        this.bookingList = new VehicleBooking[MAX_BOOKINGS];
        this.bookingCount = 0;
        this.weeklySchedule = new boolean[DAYS_IN_WEEK][HOURS_IN_DAY];
    }

    // ===================== Getters & Setters =====================

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public double getMileage() { return mileage; }

    public int getBookingCount() { return bookingCount; }

    // ===================== Methods =====================

    /**
     * Attempts to add a new booking.
     * Checks against existing bookings for overlap before confirming.
     * @return true if booking was successfully added, false if there's a conflict
     */
    public boolean addBooking(VehicleBooking newBooking) {
        if (bookingCount >= MAX_BOOKINGS) {
            throw new IllegalStateException("Booking list is full. Cannot add more bookings.");
        }

        // Check for conflicts with all existing active bookings
        for (int i = 0; i < bookingCount; i++) {
            if (bookingList[i].validateNoOverlap(newBooking)) {
                System.out.println("❌ Conflict detected with existing booking: " + bookingList[i]);
                newBooking.sendAlert("⚠ Your booking request conflicts with " +
                        bookingList[i].getBookingPartner().getName() + "'s reservation.");
                return false;
            }
        }

        // No conflict — add the booking
        bookingList[bookingCount] = newBooking;
        bookingCount++;

        // Mark the weekly schedule (uses 2D array)
        int dayIndex = getDayIndex(newBooking.getBookingDate());
        if (dayIndex >= 0) {
            for (int h = newBooking.getStartHour(); h < newBooking.getEndHour(); h++) {
                weeklySchedule[dayIndex][h] = true;
            }
        }

        System.out.println("✔ Booking confirmed: " + newBooking.getBookingPartner().getName() +
                " on " + newBooking.getBookingDate() +
                " from " + newBooking.getStartHour() + ":00 to " + newBooking.getEndHour() + ":00");
        return true;
    }


    /**
     * Returns availability status string based on active bookings today.
     * Uses String method: indexOf, length
     */
    public String getAvailabilityStatus() {
        int activeCount = 0;
        for (int i = 0; i < bookingCount; i++) {
            if (!bookingList[i].isCancelled()) activeCount++;
        }

        String plate = licensePlate;
        int dashIndex = plate.indexOf('-');
        String shortPlate = (dashIndex >= 0) ? plate.substring(0, dashIndex) : plate;

        if (activeCount == 0) {
            return "Vehicle [" + shortPlate + "...] is AVAILABLE — no active bookings.";
        } else {
            return "Vehicle [" + shortPlate + "...] has " + activeCount + " active booking(s).";
        }
    }

    /**
     * Prints the weekly schedule grid using the 2D array.
     * Shows which hours are occupied per day.
     */
    public void printWeeklySchedule() {
        System.out.println("\n=== Weekly Vehicle Schedule (X = occupied) ===");
        System.out.printf("%-12s", "Hour →");
        for (int h = 6; h <= 22; h++) {
            System.out.printf("%3d", h);
        }
        System.out.println();

        for (int d = 0; d < DAYS_IN_WEEK; d++) {
            System.out.printf("%-12s", DAY_NAMES[d]);
            for (int h = 6; h <= 22; h++) {
                System.out.printf("%3s", weeklySchedule[d][h] ? "X" : ".");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Lists all active (non-cancelled) bookings.
     */
    public void printActiveBookings() {
        System.out.println("=== Active Bookings for " + licensePlate + " ===");
        boolean found = false;
        for (int i = 0; i < bookingCount; i++) {
            if (!bookingList[i].isCancelled()) {
                System.out.println("  " + (i + 1) + ". " + bookingList[i]);
                found = true;
            }
        }
        if (!found) System.out.println("  No active bookings.");
    }

    // ===================== Private Helpers =====================

    /**
     * Maps a date string "DD/MM/YYYY" to a day-of-week index (0=Sunday).
     * Simplified version — uses day-of-month mod 7 as a demo approximation.
     */
    private int getDayIndex(String date) {
        try {
            int day = Integer.parseInt(date.substring(0, 2));
            return day % DAYS_IN_WEEK;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "SharedVehicle{" +
                "licensePlate='" + licensePlate + '\'' +
                ", mileage=" + String.format("%.1f", mileage) + " km" +
                ", bookings=" + bookingCount +
                '}';
    }
}
