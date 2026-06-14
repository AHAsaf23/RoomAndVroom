import java.io.PrintStream;
import java.util.Scanner;

public class Menu_func {
    public static Scanner scanner = new Scanner(System.in);

    // Partners & vehicle
    public static Partner partnerA;
    public static Partner partnerB;
    public static SharedVehicle sharedVehicle;

    // 1D array of chores (for menu display)
    public static Chore[] choreList;
    public static int choreCount = 0;

    // Data structures
    public static TransactionStack transactionHistory = new TransactionStack();
    public static BookingQueue bookingQueue = new BookingQueue();
    public static ChoreLinkedList choreLinkedList = new ChoreLinkedList();
    public static ExpenseCategoryTree categoryTree = new ExpenseCategoryTree();
    public static java.time.LocalDate currentDate = java.time.LocalDate.now();
    public static java.time.LocalDate lastSummaryDate = currentDate;

    // ══════════════════════════════════════════════════════════
    //  SETUP
    // ══════════════════════════════════════════════════════════

    public static void setupPartners() {
        System.out.println("── Partner Setup ──────────────────────");
        System.out.print("Enter Partner A name: ");
        String nameA = scanner.nextLine().trim();
        System.out.print("Enter Partner B name: ");
        String nameB = scanner.nextLine().trim();
        partnerA = new Partner("001", nameA.isEmpty() ? "Partner A" : nameA, 0.0F, 0);
        partnerB = new Partner("002", nameB.isEmpty() ? "Partner B" : nameB, 0.0F, 0);
        PrintStream var10000 = System.out;
        String var10001 = partnerA.getName();
        var10000.println("Partners created: " + var10001 + " & " + partnerB.getName() + "\n");
    }

    public static void setupVehicle() {
        System.out.println("── Vehicle Setup ──────────────────────");
        System.out.print("Enter license plate (or Enter for default): ");
        String plate = scanner.nextLine().trim();
        if (plate.isEmpty()) plate = "123-45-678";
        sharedVehicle = new SharedVehicle(plate, 0);
        System.out.println("Vehicle registered: " + plate + "\n");
    }

    public static void setupChores() {
        // 1D array — for menu display
        choreList = new Chore[20];

        // Setup standard and recurring chores (times per week)
        choreList[choreCount++] = new RecurringChore("Take out the trash", 5, null, 7);
        choreList[choreCount++] = new RecurringChore("Wash the dishes", 8, null, 7);
        choreList[choreCount++] = new RecurringChore("Vacuum", 15, null, 2);
        choreList[choreCount++] = new RecurringChore("Do the laundry", 12, null, 1);
        choreList[choreCount++] = new RecurringChore("Clean the bathroom", 20, null, 1);

        // LinkedList — add all chores
        for (int i = 0; i < choreCount; i++) {
            choreLinkedList.add(choreList[i]);
        }

        System.out.println("Chores loaded: " + choreCount + " chores\n");
    }

    // ══════════════════════════════════════════════════════════
    //  MENU
    // ══════════════════════════════════════════════════════════

    public static void printMenu() {
        if (!bookingQueue.isEmpty()) {
            System.out.println("\n⚠️ You have " + bookingQueue.size() + " pending booking request(s) awaiting approval!");
        }
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║        ROOM & VROOM MENU           ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║  1. Add a shared expense           ║");
        System.out.println("║  2. Request vehicle booking        ║");
        System.out.println("║  3. booking request                ║");
        System.out.println("║  4. Mark a chore as done           ║");
        System.out.println("║  5. Undo last transaction          ║");
        System.out.println("║  6. View balances & status         ║");
        System.out.println("║  7. Settle up                      ║");
        System.out.println("║  8. Manage chores                  ║");
        System.out.println("║  9. Advance one day (Simulate time)║");
        System.out.println("║  0. Exit                           ║");
        System.out.println("╚════════════════════════════════════╝");
    }

    // ══════════════════════════════════════════════════════════
    //  1. ADD EXPENSE — uses Stack + Tree
    // ══════════════════════════════════════════════════════════

    public static void addExpense() {
        System.out.println("\n── Add Shared Expense ─────────────────");
        double amount = readDouble("Enter total amount (NIS): ");
        String category = selectCategory();

        System.out.println("Who paid?  1. " + partnerA.getName() + "   2. " + partnerB.getName());
        int payerChoice = readInt("Choice: ");
        Partner payer = (payerChoice == 2) ? partnerB : partnerA;

        try {
            Expense expense = new Expense(amount, payer, getTodayString(), category);
            expense.apply(partnerA, partnerB);
            transactionHistory.push(expense);
            categoryTree.insert(category, amount);
            System.out.println("\n" + expense.getSummary());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static String selectCategory() {
        System.out.println("\n  Select category:");
        System.out.println("  1. Groceries");
        System.out.println("  2. Rent");
        System.out.println("  3. Vehicle");
        System.out.println("  4. Utilities");
        System.out.println("  5. Other");

        int choice = readInt("  Choice: ");

        switch (choice) {
            case 1:
                return "Groceries";
            case 2:
                return "Rent";
            case 3:
                return selectSubCategory("Vehicle",
                        new String[]{"Fuel", "Insurance", "Maintenance"});
            case 4:
                return selectSubCategory("Utilities",
                        new String[]{"Gas", "Water", "Electricity", "Municipality tax", "Building committee"});
            case 5:
                return readCustomCategory();
            default:
                System.out.println("  Invalid choice, using 'General'.");
                return "General";
        }
    }

    public static String selectSubCategory(String parent, String[] options) {
        System.out.println("\n  Select " + parent + " sub-category:");
        for (int i = 0; i < options.length; i++) {
            System.out.println("  " + (i + 1) + ". " + options[i]);
        }
        System.out.println("  " + (options.length + 1) + ". Other");

        int choice = readInt("  Choice: ");

        if (choice >= 1 && choice <= options.length) {
            return parent + " - " + options[choice - 1];
        } else {
            return parent + " - " + readCustomCategory();
        }
    }

    public static String readCustomCategory() {
        System.out.print("  Enter category name: ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? "General" : input;
    }

    // ══════════════════════════════════════════════════════════
    //  2. REQUEST BOOKING — adds to Queue
    // ══════════════════════════════════════════════════════════

    public static void requestBooking() {
        System.out.println("\n── Request Vehicle Booking ────────────");
        System.out.print("Enter date (DD/MM/YYYY): ");
        String date = scanner.nextLine().trim();
        if (date.isEmpty()) date = getTodayString();

        int startHour = readInt("Start hour (0-23): ");
        int endHour = readInt("End hour   (0-23): ");

        System.out.println("Who is requesting?  1. " + partnerA.getName() + "   2. " + partnerB.getName());
        int who = readInt("Choice: ");
        Partner booker = (who == 2) ? partnerB : partnerA;

        try {
            VehicleBooking booking = new VehicleBooking(date, startHour, endHour, booker);
            bookingQueue.enqueue(booking);  // Queue
            System.out.println("Request queued. Use option 3 to confirm it.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  3. PROCESS NEXT BOOKING — dequeues + custom exception
    // ══════════════════════════════════════════════════════════

    public static void processNextBooking() {
        System.out.println("\n── Process Next Booking Request ───────");
        if (bookingQueue.isEmpty()) {
            System.out.println("No pending booking requests.");
            return;
        }

        // Peek at the next request without removing it yet
        VehicleBooking next = bookingQueue.peek();
        Partner requester = next.getBookingPartner();

        // Determine who is reviewing — the OTHER partner
        Partner reviewer = requester.getId().equals(partnerA.getId()) ? partnerB : partnerA;

        // Show the request details
        System.out.println("\n  Pending request:");
        System.out.println("  ┌─────────────────────────────────┐");
        System.out.printf("  │  From    : %-21s│%n", requester.getName());
        System.out.printf("  │  Date    : %-21s│%n", next.getBookingDate());
        System.out.printf("  │  Time    : %02d:00 – %02d:00        │%n", next.getStartHour(), next.getEndHour());
        System.out.println("  └─────────────────────────────────┘");

        System.out.println("\n  " + reviewer.getName() + ", what do you want to do?");
        System.out.println("  1. Approve the booking");
        System.out.println("  0. Decline and send a message to " + requester.getName());

        int choice = readInt("  Choice: ");

        if (choice == 1) {
            // Approve — dequeue and confirm
            bookingQueue.dequeue();
            try {
                boolean success = sharedVehicle.addBooking(next);
                if (!success) {
                    throw new BookingConflictException(
                            "Time conflict for " + requester.getName(),
                            next.getBookingDate(), next.getStartHour(), next.getEndHour()
                    );
                }
                next.sendAlert("Your booking on " + next.getBookingDate() +
                        " (" + next.getStartHour() + ":00-" + next.getEndHour() + ":00) was approved by " +
                        reviewer.getName() + ".");
            } catch (BookingConflictException e) {
                System.out.println("Booking failed — " + e.getMessage());
                System.out.println(e.getConflictDetails());
            }

        } else {
            // Decline — send a message to the requester
            bookingQueue.dequeue();
            System.out.println("\n  Write a message to " + requester.getName() + ":");
            System.out.print("  > ");
            String message = scanner.nextLine();

            // Display the sent message
            System.out.println("\n  --- Message ---");
            System.out.println("  From : " + reviewer.getName());
            System.out.println("  To   : " + requester.getName());
            System.out.println("  ---------------");
            System.out.println("  " + message);
            System.out.println("  ---------------");
            System.out.println("\n  Booking request declined. Message sent to " + requester.getName() + ".");
        }
    }

    // ══════════════════════════════════════════════════════════
    //  4. COMPLETE CHORE — removes from LinkedList
    // ══════════════════════════════════════════════════════════

    public static void completeChore() {
        System.out.println("\n── Mark Chore as Done ─────────────────");

        for (int i = 0; i < choreCount; i++) {
            System.out.println("  " + (i + 1) + ". " + choreList[i].getLabel());
        }

        int idx = readInt("Enter chore number (0 to cancel): ") - 1;
        if (idx == -1) return;
        if (idx < 0 || idx >= choreCount) {
            System.out.println("Invalid number.");
            return;
        }

        if (choreList[idx].isCompleted()) {
            System.out.println("This chore is already marked as [DONE].");
            return;
        }

        System.out.println("Who completed it?  1. " + partnerA.getName() + "   2. " + partnerB.getName());
        int who = readInt("Choice: ");
        Partner doer = (who == 2) ? partnerB : partnerA;

        try {
            choreList[idx].setAssignedPartner(doer);
            choreList[idx].completeChore();

            // Chores are no longer removed from the list when completed, per user request.

            System.out.printf("%n  %-15s %d pts%n", partnerA.getName() + ":", partnerA.getChorePoints());
            System.out.printf("  %-15s %d pts%n", partnerB.getName() + ":", partnerB.getChorePoints());
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  5. UNDO — pops from Stack
    // ══════════════════════════════════════════════════════════

    public static void undoLastTransaction() {
        System.out.println("\n── Undo Last Transaction ──────────────");
        try {
            FinancialTransaction last = transactionHistory.pop();   // Stack
            double half = last.getAmount() / 2;
            last.getPaidBy().updateFinancialBalance(-half);
            Partner other = last.getPaidBy().getId().equals(partnerA.getId()) ? partnerB : partnerA;
            other.updateFinancialBalance(half);
            System.out.println("Transaction reversed successfully.");
        } catch (IllegalStateException e) {
            System.out.println("Nothing to undo: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  6. STATUS DASHBOARD
    // ══════════════════════════════════════════════════════════

    public static void printStatus() {
        boolean subRunning = true;
        while (subRunning) {
            System.out.println("\n── View Status Sub-Menu ───────────────");
            System.out.println("  1. Financial Status");
            System.out.println("  2. Chores Status");
            System.out.println("  3. Vehicle Status");
            System.out.println("  0. Back to Main Menu");
            int choice = readInt("Choice: ");

            String n1 = truncate(partnerA.getName(), 8);
            String n2 = truncate(partnerB.getName(), 8);

            switch (choice) {
                case 1:
                    System.out.println("\n  FINANCES:");
                    System.out.printf("    %-8s %+.2f NIS%n", n1, partnerA.getfBalance());
                    System.out.printf("    %-8s %+.2f NIS%n", n2, partnerB.getfBalance());
                    break;
                case 2:
                    System.out.println("\n  CHORE POINTS:");
                    System.out.printf("    %-8s %d pts%n", n1, partnerA.getChorePoints());
                    System.out.printf("    %-8s %d pts%n", n2, partnerB.getChorePoints());
                    System.out.println("\n  CHORES STATUS:");
                    if (choreCount == 0) {
                        System.out.println("    (No chores available)");
                    } else {
                        for (int i = 0; i < choreCount; i++) {
                            System.out.println("    " + (i + 1) + ". " + choreList[i].getLabel());
                        }
                    }
                    break;
                case 3:
                    System.out.println("\n  VEHICLE:");
                    System.out.println("    " + sharedVehicle.getAvailabilityStatus());
                    System.out.println("------------------------");
                    sharedVehicle.printActiveBookings();
                    sharedVehicle.printWeeklySchedule();
                    break;
                case 0:
                    subRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public static String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 2) + "..";
    }

    // ══════════════════════════════════════════════════════════
    //  9. MANAGE CHORES — add / delete / edit
    // ══════════════════════════════════════════════════════════

    public static void manageChores() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║         MANAGE CHORES              ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║  1. Add a new chore                ║");
            System.out.println("║  2. Delete a chore                 ║");
            System.out.println("║  3. Edit a chore                   ║");
            System.out.println("║  4. View all chores                ║");
            System.out.println("║  0. Back to main menu              ║");
            System.out.println("╚════════════════════════════════════╝");

            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1:
                    addChore();
                    break;
                case 2:
                    deleteChore();
                    break;
                case 3:
                    editChore();
                    break;
                case 4:
                    listChores();
                    break;
                case 0:
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public static void listChores() {
        System.out.println("\n── Chore List ─────────────────────────");
        if (choreCount == 0) {
            System.out.println("  No chores defined.");
            return;
        }
        for (int i = 0; i < choreCount; i++) {
            System.out.println("  " + (i + 1) + ". " + choreList[i].getLabel());
        }
    }

    public static void addChore() {
        if (choreCount >= 20) {
            System.out.println("Chore list is full (max 20).");
            return;
        }
        System.out.println("\n── Add New Chore ──────────────────────");
        System.out.print("  Chore description: ");
        String desc = scanner.nextLine().trim();
        if (desc.isEmpty()) {
            System.out.println("  Description cannot be empty.");
            return;
        }

        int points = readInt("  Points for completing: ");
        int intervalDays = readFrequency();

        RecurringChore chore = new RecurringChore(desc, points, null, intervalDays);
        choreList[choreCount++] = chore;
        choreLinkedList.add(chore);
        System.out.println("  ✔ Chore added: " + chore.getLabel());
    }

    public static void deleteChore() {
        System.out.println("\n── Delete Chore ───────────────────────");
        listChores();
        if (choreCount == 0) return;

        int idx = readInt("  Enter chore number to delete: ") - 1;
        if (idx < 0 || idx >= choreCount) {
            System.out.println("  Invalid number.");
            return;
        }

        String desc = choreList[idx].getDescription();
        for (int i = idx; i < choreCount - 1; i++) {
            choreList[i] = choreList[i + 1];
        }
        choreList[choreCount - 1] = null;
        choreCount--;
        choreLinkedList.remove(desc);
        System.out.println("  ✔ Chore '" + desc + "' deleted.");
    }

    public static void editChore() {
        System.out.println("\n── Edit Chore ─────────────────────────");
        listChores();
        if (choreCount == 0) return;

        int idx = readInt("  Enter chore number to edit: ") - 1;
        if (idx < 0 || idx >= choreCount) {
            System.out.println("  Invalid number.");
            return;
        }

        RecurringChore chore = (RecurringChore) choreList[idx];

        System.out.println("  What do you want to edit?");
        System.out.println("  1. Description (now: " + chore.getDescription() + ")");
        System.out.println("  2. Points      (now: " + chore.getPointValue() + ")");
        System.out.println("  3. Frequency   (now: " + chore.getScheduleDescription() + ")");
        int field = readInt("  Choice: ");

        switch (field) {
            case 1:
                System.out.print("  New description: ");
                String newDesc = scanner.nextLine().trim();
                if (!newDesc.isEmpty()) {
                    choreLinkedList.remove(chore.getDescription());
                    chore.setDescription(newDesc);
                    choreLinkedList.add(chore);
                    System.out.println("  ✔ Description updated.");
                }
                break;
            case 2:
                int newPoints = readInt("  New point value: ");
                chore.setPointValue(newPoints);
                System.out.println("  ✔ Points updated to " + newPoints + ".");
                break;
            case 3:
                int newTimesPerWeek = readFrequency();
                chore.setTimesPerWeek(newTimesPerWeek);
                System.out.println("  ✔ Frequency updated to " + chore.getScheduleDescription() + ".");
                break;
            default:
                System.out.println("  Invalid choice.");
        }
    }

    public static int readFrequency() {
        System.out.println("  Frequency:");

        int timesPerWeek = readInt("  How many times per week? ");
        if (timesPerWeek <= 0) timesPerWeek = 1;
        if (timesPerWeek > 7) timesPerWeek = 7;
        return timesPerWeek;
    }

    // ══════════════════════════════════════════════════════════
    //  8. SETTLE UP (Debt Settlement)
    // ══════════════════════════════════════════════════════════

    public static void settleUp() {
        System.out.println("\n── Settle Up ──────────────────────────");
        double balanceA = partnerA.getfBalance();
        if (Math.abs(balanceA) < 0.01) {
            System.out.println("No debts to settle! Balances are even.");
            return;
        }

        Partner debtor, receiver;
        double amount = Math.abs(balanceA);
        if (balanceA < 0) {
            debtor = partnerA;
            receiver = partnerB;
        } else {
            debtor = partnerB;
            receiver = partnerA;
        }

        System.out.println(debtor.getName() + " owes " + receiver.getName() + " " + String.format("%.2f", amount) + " NIS.");
        System.out.println("Do you want to settle this debt now?");
        System.out.println("1. Yes, apply settlement");
        System.out.println("0. No, cancel");
        int choice = readInt("Choice: ");

        if (choice == 1) {
            DebtSettlement settlement = new DebtSettlement(amount, debtor, getTodayString());
            settlement.apply(partnerA, partnerB);
            transactionHistory.push(settlement);
            System.out.println("Debt settled successfully.");
        } else {
            System.out.println("Settlement cancelled.");
        }
    }

    // ══════════════════════════════════════════════════════════
    //  WEEKLY SUMMARY
    // ══════════════════════════════════════════════════════════

    public static void checkWeeklySummary() {
        long daysPassed = java.time.temporal.ChronoUnit.DAYS.between(lastSummaryDate, currentDate);

        if (daysPassed >= 7) {
            System.out.println("\n════════════════════════════════════════════════");
            System.out.println("            🌟 WEEKLY SUMMARY 🌟                ");
            System.out.println("════════════════════════════════════════════════");

            System.out.println("\n── Financial Status ──");
            System.out.printf("  %s: %.2f NIS%n", partnerA.getName(), partnerA.getfBalance());
            System.out.printf("  %s: %.2f NIS%n", partnerB.getName(), partnerB.getfBalance());

            System.out.println("\n── Vehicle Bookings ──");
            System.out.println("  Total bookings this week: " + sharedVehicle.getBookingCount());
            sharedVehicle.resetWeeklyBookings();

            System.out.println("\n── Chores Completed ──");
            int totalChoresA = 0, totalChoresB = 0;
            for (int i = 0; i < choreCount; i++) {
                if (choreList[i] instanceof RecurringChore rc) {
                    if (rc.getTimesCompletedThisWeek() > 0) {
                        System.out.println("  " + rc.getDescription() + " - Completed " + rc.getTimesCompletedThisWeek() + " times");
                    }
                }
            }

            System.out.println("\n── Chore Points ──");
            System.out.println("  " + partnerA.getName() + ": " + partnerA.getChorePoints() + " pts");
            System.out.println("  " + partnerB.getName() + ": " + partnerB.getChorePoints() + " pts");

            Partner winner = null;
            if (partnerA.getChorePoints() > partnerB.getChorePoints()) winner = partnerA;
            else if (partnerB.getChorePoints() > partnerA.getChorePoints()) winner = partnerB;

            if (winner != null) {
                System.out.println("\n🏆 WINNER THIS WEEK: " + winner.getName() + " 🏆");

                System.out.println(winner.getName() + " is the winner of the week, so S/He can choose one chore to not do this week!");
            } else {
                System.out.println("\nIt's a tie! No reward awarded this week.");
            }

            // Reset chore points and weekly tracking
            partnerA.resetChorePoints();
            partnerB.resetChorePoints();
            for (int i = 0; i < choreCount; i++) {
                if (choreList[i] instanceof RecurringChore) {
                    ((RecurringChore) choreList[i]).resetWeeklyStats();
                }
            }
            lastSummaryDate = currentDate;

            System.out.println("════════════════════════════════════════════════\n");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
        }
    }

    // ══════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static String getTodayString() {
        return String.format("%02d/%02d/%04d", currentDate.getDayOfMonth(), currentDate.getMonthValue(), currentDate.getYear());
    }

    public static void advanceDay() {
        currentDate = currentDate.plusDays(1);
        System.out.println("\n── Advancing 1 Day ─────────────────────");
        System.out.println("A day has passed! Current system date is now: " + getTodayString());

        // Advance recurring chores
        for (int i = 0; i < choreCount; i++) {
            if (choreList[i] instanceof RecurringChore) {
                ((RecurringChore) choreList[i]).checkAndResetAvailability();
            }
        }
    }
}
