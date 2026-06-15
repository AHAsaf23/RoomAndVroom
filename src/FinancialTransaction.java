
public abstract class FinancialTransaction {

    // ===================== Shared Fields =====================
    protected double amount;
    protected Partner paidBy;
    protected String date;      // Format: "DD/MM/YYYY"

    // ===================== Constructor =====================
    public FinancialTransaction(double amount, Partner paidBy, String date) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive. Got: " + amount);
        }
        this.amount = amount;
        this.paidBy = paidBy;
        this.date = date;
    }

    // ===================== Getters & Setters =====================
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Partner getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(Partner paidBy) {
        this.paidBy = paidBy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // ===================== Abstract Methods =====================

    /**
     * Each subclass must define how it applies itself to the partners' balances.
     */
    public abstract void apply(Partner partnerA, Partner partnerB);

    /**
     * Each subclass must provide a short summary line.
     */
    public abstract String getSummary();
}


// =============================================================================

/**
 * Expense.java - Records a shared purchase paid by one partner.
 * Splits the cost evenly and updates both partners' financial balances.
 */
class Expense extends FinancialTransaction {

    private String category;    // e.g. "Groceries", "Rent", "Utilities"

    // ===================== Constructors =====================

    public Expense(double amount, Partner paidBy, String date, String category) {
        super(amount, paidBy, date);
        this.category = category;
    }

    // ===================== Getters & Setters =====================
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // ===================== Methods =====================

    /**
     * Splits the expense 50/50 between the two partners.
     * The paying partner gets credited half; the other is debited half.
     */
    @Override
    public void apply(Partner partnerA, Partner partnerB) {
        double half = this.amount / 2.0;

        // Identify who paid and who owes
        Partner payer = this.paidBy;
        Partner debtor = payer.getId().equals(partnerA.getId()) ? partnerB : partnerA;

        payer.updateFinancialBalance(half);     // payer is owed half back
        debtor.updateFinancialBalance(-half);   // debtor owes half

        System.out.println("Expense applied: " + payer.getName() + " paid " +
                String.format("%.2f", amount) + " NIS for '" + category + "'. " +
                debtor.getName() + " owes " + String.format("%.2f", half) + " NIS.");
    }


    @Override
    public String getSummary() {
        return "[EXPENSE] " + String.format("%.2f", amount) + " NIS | " +
                category + " | Paid by: " + paidBy.getName() + " | Date: " + date;
    }

    @Override
    public String toString() {
        return "Expense{amount=" + String.format("%.2f", amount) +
                " NIS, category='" + category + '\'' +
                ", paidBy='" + paidBy.getName() + '\'' +
                ", date='" + date + '\'' + '}';
    }
}

// =============================================================================

/**
 * DebtSettlement.java - Records a direct payment from one partner to another to settle debts.
 */
class DebtSettlement extends FinancialTransaction {

    // ===================== Constructors =====================
    public DebtSettlement(double amount, Partner paidBy, String date) {
        super(amount, paidBy, date);
    }

    // ===================== Methods =====================

    /**
     * Applies the debt settlement.
     * The payer's balance increases (gets closer to 0 or positive).
     * The receiver's balance decreases (gets closer to 0).
     */
    @Override
    public void apply(Partner partnerA, Partner partnerB) {
        Partner payer = this.paidBy;
        Partner receiver = payer.getId().equals(partnerA.getId()) ? partnerB : partnerA;

        payer.updateFinancialBalance(amount);
        receiver.updateFinancialBalance(-amount);

        System.out.println("✅ Debt Settlement applied: " + payer.getName() + 
                " paid " + String.format("%.2f", amount) + " NIS to " + receiver.getName() + ".");
    }

    @Override
    public String getSummary() {
        return "[SETTLEMENT] " + String.format("%.2f", amount) + " NIS | Paid by: " + 
               paidBy.getName() + " | Date: " + date;
    }

    @Override
    public String toString() {
        return "DebtSettlement{amount=" + String.format("%.2f", amount) +
                " NIS, paidBy='" + paidBy.getName() + '\'' +
                ", date='" + date + '\'' + '}';
    }
}


