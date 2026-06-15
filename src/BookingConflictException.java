//Custom exception class that we throw when someone tries to book the car in the same time as the other partner
public class BookingConflictException extends Exception {
//The variables of the exception
    private String conflictingDate;
    private int conflictStartHour;
    private int conflictEndHour;

    // Constructor with a simple message for the exception
    public BookingConflictException(String message) {
        super(message);
        this.conflictingDate = "unknown";
        this.conflictStartHour = -1;
        this.conflictEndHour = -1;
    }
// Constructor with a more detailed message for the exception
    public BookingConflictException(String message, String date, int start, int end) {
        super(message);
        this.conflictingDate = date;
        this.conflictStartHour = start;
        this.conflictEndHour = end;
    }
    //returns the exception message for the user
    public String getConflictDetails() {
        return "Conflict on " + conflictingDate +
                " between " + conflictStartHour + ":00 and " + conflictEndHour + ":00";
    }
}
