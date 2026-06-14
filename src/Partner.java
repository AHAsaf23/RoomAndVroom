/**
 * Room & Vroom - Shared Household Management System
 * Authors: גל קסירר (318158466), אסף שוורץ (207812744), אסף חיון (214195331)
 * <p>
 * Partner.java - Represents a partner/user in the shared household system.
 * Tracks financial balance and chore fairness points.
 */
public class Partner {

    // ===================== Fields =====================
    private String id;
    private String name;
    private double fBalance;    // Financial balance (positive = owed money, negative = owes money)
    private int chorePoints;    // Fairness points earned from completed chores

    // ===================== Constructors =====================

    /**
     * Default constructor
     */
    public Partner() {
        this.id = "000000000";
        this.name = "Unknown";
        this.fBalance = 0.0;
        this.chorePoints = 0;
    }

    /**
     * Full constructor
     */
    public Partner(String id, String name, double fBalance, int chorePoints) {
        this.id = id;
        this.name = name;
        this.fBalance = fBalance;
        this.chorePoints = chorePoints;
    }

    // ===================== Getters & Setters =====================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getfBalance() {
        return fBalance;
    }

    public void setfBalance(double fBalance) {
        this.fBalance = fBalance;
    }

    public int getChorePoints() {
        return chorePoints;
    }

    public void setChorePoints(int chorePoints) {
        this.chorePoints = chorePoints;
    }

    // ===================== Methods =====================

    /**
     * Updates the financial balance by the given amount.
     * Positive amount = partner received money / is owed.
     * Negative amount = partner spent money / owes.
     */
    public void updateFinancialBalance(double amount) {
        this.fBalance += amount;
    }

    /**
     * Adds fairness points for completing a chore.
     *
     * @param points number of points to add (must be positive)
     */
    public void addChorePoints(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Chore points cannot be negative.");
        }
        this.chorePoints += points;
    }


    /**
     * Resets chore points to zero at the start of each new week.
     */
    public void resetChorePoints() {
        this.chorePoints = 0;
        System.out.println("🔄 Chore points reset for " + this.name + ".");
    }

    /**
     * Checks if two partners are the same person by comparing their IDs.
     * Uses String method: equals
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Partner other)) return false;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return "Partner{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", fBalance=" + String.format("%.2f", fBalance) + " NIS" +
                ", chorePoints=" + chorePoints +
                '}';
    }
}

