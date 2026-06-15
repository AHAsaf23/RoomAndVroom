import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class RecurringChore extends Chore {

    // ===================== Fields =====================
    private int timesPerWeek;       // How many times it repeats per week
    private int timesCompleted;     // How many times this chore has been done total
    private int timesCompletedThisWeek; // How many times completed this week
    private String lastCompletedDate;

    // ===================== Constructors =====================

    public RecurringChore() {
        super();
        this.timesPerWeek = 1;
        this.timesCompleted = 0;
        this.timesCompletedThisWeek = 0;
        this.lastCompletedDate = null;
    }

    public RecurringChore(String description, int pointValue, Partner assignedPartner,
                          int timesPerWeek) {
        super(description, pointValue, assignedPartner);
        this.timesPerWeek = timesPerWeek;
        this.timesCompleted = 0;
        this.timesCompletedThisWeek = 0;
        this.lastCompletedDate = null;
    }

    // ===================== Getters & Setters =====================

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

    // ===================== Methods =====================

    /**
     * Completes the chore, records the date, then auto-resets for next cycle.
     * Overrides the parent completeChore() to add recurring logic.
     */
    @Override
    public void completeChore() {
        super.completeChore();              // Award points via parent logic
        this.timesCompleted++;
        this.timesCompletedThisWeek++;
        this.lastCompletedDate = Menu_func.getTodayString();

        System.out.println("🔄 Recurring chore completed.");
    }

    public void checkAndResetAvailability() {
        if (super.isCompleted() && lastCompletedDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate lastDate = LocalDate.parse(lastCompletedDate, formatter);
            LocalDate today = Menu_func.currentDate;
            
            long daysPassed = ChronoUnit.DAYS.between(lastDate, today);
            double requiredDays = 7.0 / timesPerWeek;
            if (daysPassed >= requiredDays) {
                System.out.println("⏰ Recurring cycle finished for: '" + getDescription() + "' -> Chore is available again!");
                super.reset();
                this.lastCompletedDate = null;
            }
        }
    }

    /**
     * Returns a human-readable description of how often this chore repeats.
     */
    public String getScheduleDescription() {
        if (timesPerWeek == 7) return "every day";
        if (timesPerWeek == 1) return "once a week";
        return timesPerWeek + " times a week";
    }

    @Override
    public String getLabel() {
        checkAndResetAvailability();
        String base = super.getLabel();
        return base + " [" + getScheduleDescription() + "]";
    }

    @Override
    public boolean isCompleted() {
        checkAndResetAvailability();
        return super.isCompleted();
    }

    @Override
    public String toString() {
        return "RecurringChore{" +
                "description='" + getDescription() + '\'' +
                ", timesPerWeek=" + timesPerWeek +
                ", timesCompleted=" + timesCompleted +
                '}';
    }
}