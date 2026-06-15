
public class BookingConflictException extends Exception {

    private String conflictingDate;
    private int conflictStartHour;
    private int conflictEndHour;

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
