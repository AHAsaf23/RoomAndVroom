
public class SharedVehicle {

    // Constants
    private static int MAX_BOOKINGS = 50;
    private static int DAYS_IN_WEEK = 7;
    private static int HOURS_IN_DAY = 24;

    // Day name lookup
    private static String[] DAY_NAMES = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

    /* Fields */
    private String licensePlate; // License Plate between 7 and 8 digits
    private VehicleBooking[] bookingList;    // 1D array — all bookings
    private int bookingCount;

    // 2D array: weeklySchedule[day][hour] = true if that slot is taken
    private boolean[][] weeklySchedule;     // [0-6 days][0-23 hours]

    /* Constructors */
    // default constructor
    public SharedVehicle() {
        this.licensePlate = "000-00-000";
        this.bookingList = new VehicleBooking[MAX_BOOKINGS];
        this.bookingCount = 0;
        this.weeklySchedule = new boolean[DAYS_IN_WEEK][HOURS_IN_DAY];
    }
    // full constructor
    public SharedVehicle(String licensePlate) {
        this.licensePlate = licensePlate;
        this.bookingList = new VehicleBooking[MAX_BOOKINGS];
        this.bookingCount = 0;
        this.weeklySchedule = new boolean[DAYS_IN_WEEK][HOURS_IN_DAY];
    }

    /* Getters & Setters */
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public int getBookingCount() { return bookingCount; }

    // Resets the bookings for the next week
    public void resetWeeklyBookings() {
        this.bookingCount = 0;
        this.weeklySchedule = new boolean[DAYS_IN_WEEK][HOURS_IN_DAY];
    }

    /* Methods */

    /**
     * Try to add a new vehicle booking.
     * checking if there is an overlap with another booking.
     * @return true if booking was added, false if there's an overlap.
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

        // No conflict = add the booking
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


    public String getAvailabilityStatus() {
        int activeCount = 0;
        for (int i = 0; i < bookingCount; i++) {
            if (!bookingList[i].isCanceled()) activeCount++;
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
     * Prints the weekly schedule using the 2D array.
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
            if (!bookingList[i].isCanceled()) {
                System.out.println("  " + (i + 1) + ". " + bookingList[i]);
                found = true;
            }
        }
        if (!found) System.out.println("  No active bookings.");
    }

    /* Private Helpers */

    /**
     * Maps a date string "DD/MM/YYYY" to a day-of-week index (0=Sunday).
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
                ", bookings=" + bookingCount +
                '}';
    }
}
