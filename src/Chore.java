/**
 * Room & Vroom - Shared Household Management System
 * Authors: גל קסירר (318158466), אסף שוורץ (207812744), אסף חיון (214195331)
 * <p>
 * Chore.java - Represents a household task that earns fairness points upon completion.
 */
public class Chore {

    // ===================== Fields =====================
    private String description;     // e.g. "Take out the trash"
    private int pointValue;         // Fairness points awarded upon completion
    private Partner assignedPartner;
    private boolean isCompleted;

    // ===================== Constructors =====================

    /**
     * Default constructor
     */
    public Chore() {
        this.description = "Unknown chore";
        this.pointValue = 1;
        this.assignedPartner = null;
        this.isCompleted = false;
    }

    /**
     * Full constructor
     */
    public Chore(String description, int pointValue, Partner assignedPartner) {
        this.description = description;
        this.pointValue = pointValue;
        this.assignedPartner = assignedPartner;
        this.isCompleted = false;
    }

    // ===================== Getters & Setters =====================

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPointValue() {
        return pointValue;
    }

    public void setPointValue(int pointValue) {
        this.pointValue = pointValue;
    }

    public Partner getAssignedPartner() {
        return assignedPartner;
    }

    public void setAssignedPartner(Partner assignedPartner) {
        this.assignedPartner = assignedPartner;
    }


    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // ===================== Methods =====================

    /**
     * Marks the chore as completed and awards points to the assigned partner.
     * Throws exception if no partner is assigned or chore is already done.
     */
    public void completeChore() {
        if (assignedPartner == null) {
            throw new IllegalStateException("Cannot complete chore: no partner assigned to '" + description + "'.");
        }
        if (isCompleted) {
            throw new IllegalStateException("Chore '" + description + "' is already completed.");
        }
        this.isCompleted = true;
        assignedPartner.addChorePoints(this.pointValue);
        System.out.println("✔ Chore completed: '" + description + "' | +" + pointValue + " pts → " + assignedPartner.getName());
    }


    /**
     * Resets the chore for re-assignment (used for recurring chores).
     */
    public void reset() {
        this.isCompleted = false;
        System.out.println("Chore '" + description + "' has been reset and is ready to be assigned again.");
    }

    /**
     * Returns a formatted chore label with status indicator.
     * Uses String methods: substring, toUpperCase, charAt, trim
     */
    public String getLabel() {
        String shortDesc = description.length() > 20
                ? description.substring(0, 20).trim() + "..."
                : description;

        // Capitalize each word for a clean display
        String[] words = shortDesc.split(" ");
        StringBuilder capitalized = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                capitalized.append(Character.toUpperCase(words[i].charAt(0)))
                           .append(words[i].substring(1));
                if (i < words.length - 1) capitalized.append(" ");
            }
        }

        String status = isCompleted ? "[DONE]   " : "[PENDING]";
        return status + " " + capitalized + " (" + pointValue + " pts)";
    }

    @Override
    public String toString() {
        String partnerName = (assignedPartner != null) ? assignedPartner.getName() : "Unassigned";
        return "Chore{" +
                "description='" + description + '\'' +
                ", points=" + pointValue +
                ", assignedTo='" + partnerName + '\'' +
                ", completed=" + isCompleted +
                '}';
    }
}
