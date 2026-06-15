
public class Chore {

    // ===================== Fields =====================
    private String description;     // e.g. "Take out the trash"
    private int pointValue;         // Fairness points awarded upon completion
    private Partner assignedPartner;
    private boolean isCompleted;

    // ===================== Constructors =====================

    // this is a default constructor for the chore class
    public Chore() {
        this.description = "Unknown chore";
        this.pointValue = 1;
        this.assignedPartner = null;
        this.isCompleted = false;
    }

    // a full constructor, defines what the chore is,
    // how many points, which partner on it, and updates status to false
    public Chore(String description, int pointValue, Partner assignedPartner) {
        this.description = description;
        this.pointValue = pointValue;
        this.assignedPartner = assignedPartner;
        this.isCompleted = false;
    }

    // getters and setters

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

    // methods

    /**
     * Marks the chore as completed and awards points to the assigned partner.
     */
    // checks for assigned partner, marks the chore as completed
    // and prints if succeeded the chore's details
    public void markAsDone() {
        this.isCompleted = true;
        assignedPartner.addChorePoints(this.pointValue);
        System.out.println("✔ Chore completed: '" + description + "' | +" + pointValue + " pts → " + assignedPartner.getName());
    }


    // resets a chore and prints when done
    public void reset() {
        this.isCompleted = false;
        System.out.println("Chore '" + description + "' has been reset and is ready to be assigned again.");
    }

    @Override
    public String toString() {
        // capitalize for a better viewing experience
        String[] words = description.split(" ");
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
}
