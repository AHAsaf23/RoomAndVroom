
public class Partner {

    /*fields */
    private String name;
    private double fBalance;    // Financial balance (positive = owed money, negative = owes money)
    private int chorePoints;    // Fairness points earned from completed chores

    /*constructors */

    public Partner() {
        this.name = "Unknown";
        this.fBalance = 0.0;
        this.chorePoints = 0;
    }

    public Partner(String name, double fBalance, int chorePoints) {
        this.name = name;
        this.fBalance = fBalance;
        this.chorePoints = chorePoints;
    }

    /*getters and setters*/


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

    /*methods*/

    public void updateFinancialBalance(double amount) {
        this.fBalance += amount;
    }


    public void addChorePoints(int pointsToAdd) {
        this.chorePoints += pointsToAdd;
    }


    /**
     * resets chore points to zero at the start of a new week
     */
    public void resetChorePoints() {
        this.chorePoints = 0;
        System.out.println("Chore points reset for " + this.name + ".");
    }

    /**
     * Checks if two partners are the same person by comparing their names.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Partner other)) return false;
        return this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return "Partner{" +
                "name='" + name + '\'' +
                ", fBalance=" + fBalance +
                ", chorePoints=" + chorePoints +
                '}';
    }
}
