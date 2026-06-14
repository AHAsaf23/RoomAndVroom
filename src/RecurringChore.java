import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Room & Vroom - Shared Household Management System
 * Authors: גל קסירר (318158466), אסף שוורץ (207812744), אסף חיון (214195331)
 * <p>
 * RecurringChore.java - A chore that repeats on a schedule.
 * Inherits from Chore and adds reset logic + completion counter.
 */
public class RecurringChore extends Chore {

    // ===================== Fields =====================
    private int intervalDays;       // How often it repeats (e.g. 7 = weekly)
    private int timesCompleted;     // How many times this chore has been done total
    private int timesCompletedThisWeek; // How many times completed this week
    private String lastCompletedDate;

    // ===================== Constructors =====================

    public RecurringChore() {
        super();
        this.intervalDays = 7;
        this.timesCompleted = 0;
        this.timesCompletedThisWeek = 0;
        this.lastCompletedDate = null;
    }

    public RecurringChore(String description, int pointValue, Partner assignedPartner,
                          int intervalDays) {
        super(description, pointValue, assignedPartner);
        this.intervalDays = intervalDays;
        this.timesCompleted = 0;
        this.timesCompletedThisWeek = 0;
        this.lastCompletedDate = null;
    }

    // ===================== Getters & Setters =====================

    public int getIntervalDays() {
        return intervalDays;
    }

    public void setIntervalDays(int intervalDays) {
        this.intervalDays = intervalDays;
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

        System.out.println("🔄 Recurring chore completed. It will return in " + intervalDays + " day(s).");
    }

    public void checkAndResetAvailability() {
        if (super.isCompleted() && lastCompletedDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate lastDate = LocalDate.parse(lastCompletedDate, formatter);
            LocalDate today = Menu_func.currentDate;
            
            long daysPassed = ChronoUnit.DAYS.between(lastDate, today);
            if (daysPassed >= intervalDays) {
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
        if (intervalDays == 1) return "every day";
        if (intervalDays == 7) return "every week";
        if (intervalDays == 14) return "every 2 weeks";
        if (intervalDays == 30) return "every month";
        return "every " + intervalDays + " days";
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
                ", intervalDays=" + intervalDays +
                ", timesCompleted=" + timesCompleted +
                '}';
    }
}