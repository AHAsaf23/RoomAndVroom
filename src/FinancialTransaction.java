
public abstract class FinancialTransaction {

    /* Shared Fields */
    protected double amount;
    protected Partner paidBy;
    protected String date;      // Format: "DD/MM/YYYY"

    /* Constructor */
    // full constructor
    public FinancialTransaction(double amount, Partner paidBy, String date) {
        this.amount = amount;
        this.paidBy = paidBy;
        this.date = date;
    }

    /* Getters & Setters */
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

    /* Abstract Methods */

    // changes the balances of the partners based on the transaction
    public abstract void apply(Partner partnerA, Partner partnerB);

    // returns a short text describing the transaction
    public abstract String getSummary();
}


// ------------------------------------------------------------------

// represents a purchase paid by one partner that needs to be split
class Expense extends FinancialTransaction {

    private String category;    // e.g. "Groceries", "Rent", "Utilities"

    /* Constructors */
    // full constructor
    public Expense(double amount, Partner paidBy, String date, String category) {
        super(amount, paidBy, date);
        this.category = category;
    }

    /* Getters & Setters */
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /* Methods */

    // splits the cost equally between both partners
    @Override
    public void apply(Partner partnerA, Partner partnerB) {
        double half = this.amount / 2.0;

        // Identify who paid and who owes
        Partner payer = this.paidBy;
        Partner debtor = payer.getName().equals(partnerA.getName()) ? partnerB : partnerA;

        payer.updateFinancialBalance(half);     // payer is owed half back
        debtor.updateFinancialBalance(-half);   // debtor owes half

        System.out.println("Expense applied: " + payer.getName() + " paid " +
                String.format("%.2f", amount) + " NIS for '" + category + "'. " +
                debtor.getName() + " owes " + String.format("%.2f", half) + " NIS.");
    }


    // return the summary of who paid, how much was paid, the category that was paid for and the date it was processed
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

// ------------------------------------------------------------------

// represents a direct payment from one partner to the other to settle debts
class DebtSettlement extends FinancialTransaction {

    /* Constructors */
    public DebtSettlement(double amount, Partner paidBy, String date) {
        super(amount, paidBy, date);
    }

    /* Methods */

    // updates the balances after a direct payment is made
    @Override
    public void apply(Partner partnerA, Partner partnerB) {
        Partner payer = this.paidBy;
        Partner receiver = payer.getName().equals(partnerA.getName()) ? partnerB : partnerA;

        payer.updateFinancialBalance(amount);
        receiver.updateFinancialBalance(-amount);

        System.out.println("  Debt Settlement applied: " + payer.getName() + 
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


