import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class RecurringChore extends Chore {

    //defining all the chore's fields
    private int timesPerWeek;       // How many times it repeats per week
    private int timesCompleted;     // How many times this chore has been done total
    private int timesCompletedThisWeek; // How many times completed this week
    private String lastCompletedDate;


    //CONSTRUCTORS

    //default constructor, defines default values
    public RecurringChore() {
        super();
        this.timesPerWeek = 1;
        this.timesCompleted = 0;
        this.timesCompletedThisWeek = 0;
        this.lastCompletedDate = null;
    }

    //full constructor, builds a new chore
    public RecurringChore(String description, int pointValue, Partner assignedPartner,
                          int timesPerWeek) {
        super(description, pointValue, assignedPartner);
        this.timesPerWeek = timesPerWeek;
        this.timesCompleted = 0;
        this.timesCompletedThisWeek = 0;
        this.lastCompletedDate = null;
    }


    //GETTERS AND SETTERS
    public int getTimesPerWeek() {
        return timesPerWeek;
    }

    public void setTimesPerWeek(int timesPerWeek) {
        this.timesPerWeek = timesPerWeek;
    }

    public int getTimesCompleted() {
        return timesCompleted;
    }
    
    public int getTimesCompletedThisWeek() {
        return timesCompletedThisWeek;
    }
    
    public void resetWeeklyStats() {
        this.timesCompletedThisWeek = 0;
    }


   //METHODS
    @Override
    // marks a chore as done, updates the chore's value
    // awards the points to the assigned partner
    // and prints if succeeded
    public void markAsDone() {
        super.markAsDone();              // Award points via parent logic
        this.timesCompleted++;
        this.timesCompletedThisWeek++;
        this.lastCompletedDate = Menu_func.getTodayString();
    }

    //resets a chore if there has passed enough
    //time since last done
    public void checkAndResetAvailability() {
        if (super.isCompleted() && lastCompletedDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate lastDate = LocalDate.parse(lastCompletedDate, formatter);
            LocalDate today = Menu_func.currentDate;
            
            long daysPassed = ChronoUnit.DAYS.between(lastDate, today);
            double requiredDays = 7.0 / timesPerWeek;
            if (daysPassed >= requiredDays) {
                super.reset();
                this.lastCompletedDate = null;
            }
        }
    }

    //prints the chore's needed frequency
    public String getScheduleDescription() {
        if (timesPerWeek == 7) return "every day";
        if (timesPerWeek == 1) return "once a week";
        return timesPerWeek + " times a week";
    }

    @Override
    public String toString() {
        checkAndResetAvailability();
        String base = super.toString();
        return base + " [" + getScheduleDescription() + "]";
    }

    @Override
    public boolean isCompleted() {
        checkAndResetAvailability();
        return super.isCompleted();
    }
}