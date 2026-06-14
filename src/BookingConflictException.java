/**
 * Room & Vroom - Shared Household Management System
 * Authors: גל קסירר (318158466), אסף שוורץ (207812744), אסף חיון (214195331)
 * <p>
 * BookingConflictException.java - Custom exception thrown when a vehicle booking
 * overlaps with an existing reservation.
 */
public class BookingConflictException extends Exception {

    private final String conflictingDate;
    private final int conflictStartHour;
    private final int conflictEndHour;

    public BookingConflictException(String message) {
        super(message);
        this.conflictingDate = "unknown";
        this.conflictStartHour = -1;
        this.conflictEndHour = -1;
    }

    public BookingConflictException(String message, String date, int start, int end) {
        super(message);
        this.conflictingDate = date;
        this.conflictStartHour = start;
        this.conflictEndHour = end;
    }

    public String getConflictDetails() {
        return "Conflict on " + conflictingDate +
                " between " + conflictStartHour + ":00 and " + conflictEndHour + ":00";
    }
}
