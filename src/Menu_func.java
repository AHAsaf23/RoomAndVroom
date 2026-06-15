import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    public static LocalDate currentDate = LocalDate.now();
    public static LocalDate lastSummaryDate = currentDate;

    // ==========================================================
    //  SETUP
    // ==========================================================

    public static void setupPartners() {
        System.out.println("── Partner Setup ──────────────────────");
        String nameA = readName("Enter Partner A name: ");
        String nameB = readName("Enter Partner B name: ");
        partnerA = new Partner("001", nameA.isEmpty() ? "Partner A" : nameA, 0.0F, 0);
        partnerB = new Partner("002", nameB.isEmpty() ? "Partner B" : nameB, 0.0F, 0);
        System.out.println("Partners created: " + partnerA.getName() + " & " + partnerB.getName() + "\n");
    }

    public static void setupVehicle() {
        System.out.println("── Vehicle Setup ──────────────────────");
        String plate;
        while (true) {
            System.out.print("Enter license plate (7-8 digits, or Enter for default): ");
            plate = scanner.nextLine().trim();
            if (plate.isEmpty()) {
                plate = "12345678";
                break;
            }
            String digitsOnly = plate.replace("-", "");
            if (digitsOnly.length() >= 7 && digitsOnly.length() <= 8 && digitsOnly.matches("\\d+")) {
                plate = digitsOnly;
                break;
            }
            System.out.println("Invalid plate. Must contain 7-8 digits only (dashes are allowed).");
        }
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

    // ==========================================================
    //  MENU
    // ==========================================================

    public static void printMenu() {
        if (!bookingQueue.isEmpty()) {
            System.out.println("\n⚠️ You have " + bookingQueue.size() + " pending booking request(s) awaiting approval!");
        }
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║        ROOM & VROOM MENU           ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║  1. Add a shared expense           ║");
        System.out.println("║  2. Request vehicle booking        ║");
        System.out.println("║  3. Process next booking request   ║");
        System.out.println("║  4. Mark a chore as done           ║");
        System.out.println("║  5. Undo last transaction          ║");
        System.out.println("║  6. View balances & status         ║");
        System.out.println("║  7. Settle up debts                ║");
        System.out.println("║  8. Manage chores (Add/Edit)       ║");
        System.out.println("║  9. Advance one day (Simulate time)║");
        System.out.println("║  0. Exit                           ║");
        System.out.println("╚════════════════════════════════════╝");
    }

    // ==========================================================
    //  1. ADD EXPENSE — uses Stack + Tree
    // ==========================================================

    public static void addExpense() {
        System.out.println("\n── Add Shared Expense ─────────────────");
        System.out.println("  Here you can log a shared expense between both partners.");
        System.out.println("  The cost will be split 50/50 and added to your balance.\n");

        double amount;
        while (true) {
            amount = readDouble("Enter total amount (NIS, or 0 to cancel): ");
            if (amount == 0) { System.out.println("  Cancelled."); return; }
            if (amount > 0) break;
            System.out.println("  Amount must be positive.");
        }

        String category = selectCategory();
        if (category == null) { System.out.println("  Cancelled."); return; }

        System.out.println("Who paid?  1. " + partnerA.getName() + "   2. " + partnerB.getName());
        Partner payer = readPartnerChoice();

        if (!confirmAction("Add expense of " + String.format("%.2f", amount) + " NIS in '" + category + "' paid by " + payer.getName() + "?")) {
            System.out.println("  Cancelled.");
            return;
        }

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
        while (true) {
            System.out.println("\n  Select category:");
            System.out.println("  1. Groceries");
            System.out.println("  2. Rent");
            System.out.println("  3. Vehicle");
            System.out.println("  4. Utilities");
            System.out.println("  5. Other");
            System.out.println("  0. Cancel");

            int choice = readInt("  Choice: ");

            switch (choice) {
                case 0: return null;
                case 1: return "Groceries";
                case 2: return "Rent";
                case 3:
                    String vehicleSub = selectSubCategory("Vehicle",
                            new String[]{"Fuel", "Insurance", "Maintenance"});
                    if (vehicleSub == null) continue;
                    return vehicleSub;
                case 4:
                    String utilitySub = selectSubCategory("Utilities",
                            new String[]{"Gas", "Water", "Electricity", "Municipality tax", "Building committee"});
                    if (utilitySub == null) continue;
                    return utilitySub;
                case 5: return readCustomCategory();
                default:
                    System.out.println("  Invalid choice. Please enter 0-5.");
            }
        }
    }

    public static String selectSubCategory(String parent, String[] options) {
        while (true) {
            System.out.println("\n  Select " + parent + " sub-category:");
            for (int i = 0; i < options.length; i++) {
                System.out.println("  " + (i + 1) + ". " + options[i]);
            }
            System.out.println("  " + (options.length + 1) + ". Other");
            System.out.println("  0. Back");

            int choice = readInt("  Choice: ");

            if (choice == 0) return null;
            if (choice >= 1 && choice <= options.length) {
                return parent + " - " + options[choice - 1];
            } else if (choice == options.length + 1) {
                return parent + " - " + readCustomCategory();
            }
            System.out.println("  Invalid choice. Please enter 0-" + (options.length + 1) + ".");
        }
    }

    public static String readCustomCategory() {
        System.out.print("  Enter category name: ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? "General" : input;
    }

    // ==========================================================
    //  2. REQUEST BOOKING — adds to Queue
    // ==========================================================

    public static void requestBooking() {
        System.out.println("\n── Request Vehicle Booking ────────────");
        System.out.println("  Here you can request a time slot for the shared vehicle.");
        System.out.println("  Your partner will need to approve it before it's confirmed.\n");
        String date;
        while (true) {
            System.out.print("Enter date (DD/MM/YYYY, or Enter for today): ");
            date = scanner.nextLine().trim();
            if (date.isEmpty()) {
                date = getTodayString();
                break;
            }
            if (date.matches("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/(19|20)\\d\\d$")) {
                LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                if (parsedDate.isBefore(currentDate)) {
                    System.out.println("  Cannot book a vehicle in the past. Current date is " + getTodayString());
                } else {
                    break;
                }
            } else {
                System.out.println("  Invalid date format. Please use DD/MM/YYYY.");
            }
        }

        int startHour;
        while (true) {
            startHour = readInt("Start hour (0-23): ");
            if (startHour >= 0 && startHour <= 23) break;
            System.out.println("  Hour must be between 0 and 23.");
        }
        int endHour;
        while (true) {
            endHour = readInt("End hour   (0-23): ");
            if (endHour >= 0 && endHour <= 23) {
                if (endHour > startHour) break;
                System.out.println("  End hour must be after start hour (" + startHour + ").");
            } else {
                System.out.println("  Hour must be between 0 and 23.");
            }
        }

        System.out.println("Who is requesting?  1. " + partnerA.getName() + "   2. " + partnerB.getName());
        Partner booker = readPartnerChoice();

        if (!confirmAction("Submit booking request for " + date + " (" + startHour + ":00-" + endHour + ":00) by " + booker.getName() + "?")) {
            System.out.println("  Cancelled.");
            return;
        }

        try {
            VehicleBooking booking = new VehicleBooking(date, startHour, endHour, booker);
            bookingQueue.enqueue(booking);  // Queue
            System.out.println("Request queued. Use option 3 to confirm it.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ==========================================================
    //  3. PROCESS NEXT BOOKING — dequeues + custom exception
    // ==========================================================

    public static void processNextBooking() {
        System.out.println("\n── Process Next Booking Request ───────");
        System.out.println("  Here you can view all pending booking requests and approve or decline them.");
        System.out.println("  If declined, you can send a message to explain why.\n");
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
        System.out.println("  2. Decline and send a message to " + requester.getName());
        System.out.println("  0. Skip for now (keep in queue)");

        int choice;
        while (true) {
            choice = readInt("  Choice: ");
            if (choice == 0 || choice == 1 || choice == 2) break;
            System.out.println("  Please enter 0, 1, or 2.");
        }

        if (choice == 0) {
            System.out.println("  Skipped. The request stays in the queue.");
            return;
        }

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

    // ==========================================================
    //  4. COMPLETE CHORE — removes from LinkedList
    // ==========================================================

    public static void completeChore() {
        System.out.println("\n── Mark Chore as Done ─────────────────");
        System.out.println("  Here you can mark a household chore as completed and earn points.");
        System.out.println("  The more chores you do, the closer you are to winning the week!\n");

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
            System.out.println("Do you want to complete it again?  1. Yes  0. No");
            int again;
            while (true) {
                again = readInt("Choice (1 or 0): ");
                if (again == 1 || again == 0) break;
                System.out.println("Please enter 1 (Yes) or 0 (No).");
            }
            if (again != 1) return;
            choreList[idx].reset();
        }

        System.out.println("Who completed it?  1. " + partnerA.getName() + "   2. " + partnerB.getName());
        Partner doer = readPartnerChoice();

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

    // ==========================================================
    //  5. UNDO — pops from Stack
    // ==========================================================

    public static void undoLastTransaction() {
        System.out.println("\n── Undo Last Transaction ──────────────");
        System.out.println("  Here you can reverse the most recent financial transaction.");
        System.out.println("  This will restore both partners' balances to their previous state.\n");
        if (transactionHistory.isEmpty()) {
            System.out.println("  Nothing to undo: transaction history is empty.");
            return;
        }
        if (!confirmAction("Undo the last transaction?")) {
            System.out.println("  Cancelled.");
            return;
        }
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

    // ==========================================================
    //  6. STATUS DASHBOARD
    // ==========================================================

    public static void printStatus() {
        System.out.println("\n── View Balances & Status ─────────────");
        System.out.println("  Here you can check the current financial balances, chore points,");
        System.out.println("  vehicle schedule, and full transaction history.\n");
        boolean subRunning = true;
        while (subRunning) {
            System.out.println("\n── View Status Sub-Menu ───────────────");
            System.out.println("  1. Financial Status");
            System.out.println("  2. Chores Status");
            System.out.println("  3. Vehicle Status");
            System.out.println("  4. Transaction History");
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
                case 4:
                    System.out.println();
                    transactionHistory.printAll();
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

    // ==========================================================
    //  9. MANAGE CHORES — add / delete / edit
    // ==========================================================

    public static void manageChores() {
        System.out.println("\n  Here you can add new chores, delete existing ones, edit their");
        System.out.println("  details (name, points, frequency), or view the full chore list.\n");
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

            int choice;
            while (true) {
                choice = readInt("Enter your choice: ");
                if (choice >= 0 && choice <= 4) break;
                System.out.println("Please enter a number between 0 and 4.");
            }
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
        System.out.print("  Chore description (or 0 to cancel): ");
        String desc = scanner.nextLine().trim();
        if (desc.equals("0") || desc.isEmpty()) {
            System.out.println("  Cancelled.");
            return;
        }

        int points = readInt("  Points for completing: ");
        int intervalDays = readFrequency();

        if (!confirmAction("Add chore '" + desc + "' (" + points + " pts)?")) {
            System.out.println("  Cancelled.");
            return;
        }

        RecurringChore chore = new RecurringChore(desc, points, null, intervalDays);
        choreList[choreCount++] = chore;
        choreLinkedList.add(chore);
        System.out.println("  Chore added: " + chore.getLabel());
    }

    public static void deleteChore() {
        System.out.println("\n── Delete Chore ───────────────────────");
        listChores();
        if (choreCount == 0) return;

        int idx = readInt("  Enter chore number to delete (0 to cancel): ") - 1;
        if (idx == -1) return;
        if (idx < 0 || idx >= choreCount) {
            System.out.println("  Invalid number.");
            return;
        }

        String desc = choreList[idx].getDescription();
        if (!confirmAction("Delete chore '" + desc + "'?")) {
            System.out.println("  Cancelled.");
            return;
        }

        for (int i = idx; i < choreCount - 1; i++) {
            choreList[i] = choreList[i + 1];
        }
        choreList[choreCount - 1] = null;
        choreCount--;
        choreLinkedList.remove(desc);
        System.out.println("  Chore '" + desc + "' deleted.");
    }

    public static void editChore() {
        System.out.println("\n── Edit Chore ─────────────────────────");
        listChores();
        if (choreCount == 0) return;

        int idx = readInt("  Enter chore number to edit (0 to cancel): ") - 1;
        if (idx == -1) return;
        if (idx < 0 || idx >= choreCount) {
            System.out.println("  Invalid number.");
            return;
        }

        RecurringChore chore = (RecurringChore) choreList[idx];

        System.out.println("  What do you want to edit?");
        System.out.println("  1. Description (now: " + chore.getDescription() + ")");
        System.out.println("  2. Points      (now: " + chore.getPointValue() + ")");
        System.out.println("  3. Frequency   (now: " + chore.getScheduleDescription() + ")");
        System.out.println("  0. Cancel");

        int field;
        while (true) {
            field = readInt("  Choice: ");
            if (field >= 0 && field <= 3) break;
            System.out.println("  Please enter 0-3.");
        }
        if (field == 0) return;

        switch (field) {
            case 1:
                System.out.print("  New description: ");
                String newDesc = scanner.nextLine().trim();
                if (!newDesc.isEmpty()) {
                    choreLinkedList.remove(chore.getDescription());
                    chore.setDescription(newDesc);
                    choreLinkedList.add(chore);
                    System.out.println("  Description updated.");
                }
                break;
            case 2:
                int newPoints = readInt("  New point value: ");
                chore.setPointValue(newPoints);
                System.out.println("  Points updated to " + newPoints + ".");
                break;
            case 3:
                int newTimesPerWeek = readFrequency();
                chore.setTimesPerWeek(newTimesPerWeek);
                System.out.println("  Frequency updated to " + chore.getScheduleDescription() + ".");
                break;
        }
    }

    public static int readFrequency() {
        System.out.println("  Frequency:");

        int timesPerWeek = readInt("  How many times per week? ");
        if (timesPerWeek <= 0) timesPerWeek = 1;
        if (timesPerWeek > 7) timesPerWeek = 7;
        return timesPerWeek;
    }

    // ==========================================================
    //  8. SETTLE UP (Debt Settlement)
    // ==========================================================

    public static void settleUp() {
        System.out.println("\n── Settle Up ──────────────────────────");
        System.out.println("  Here you can clear all outstanding debts between partners.");
        System.out.println("  If one partner owes the other, this will zero out the balance.\n");
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
        if (!confirmAction("Settle this debt now?")) {
            System.out.println("  Settlement cancelled.");
            return;
        }

        DebtSettlement settlement = new DebtSettlement(amount, debtor, getTodayString());
        settlement.apply(partnerA, partnerB);
        transactionHistory.push(settlement);
        System.out.println("Debt settled successfully.");
    }

    // ==========================================================
    //  WEEKLY SUMMARY
    // ==========================================================

    public static void checkWeeklySummary() {
        long daysPassed = java.time.temporal.ChronoUnit.DAYS.between(lastSummaryDate, currentDate);

        if (daysPassed >= 7) {
            System.out.println("\n════════════════════════════════════════════════");
            System.out.println("               WEEKLY SUMMARY                   ");
            System.out.println("════════════════════════════════════════════════");

            System.out.println("\n── Financial Status ──");
            System.out.printf("  %s: %.2f NIS%n", partnerA.getName(), partnerA.getfBalance());
            System.out.printf("  %s: %.2f NIS%n", partnerB.getName(), partnerB.getfBalance());

            System.out.println("\n── Vehicle Bookings ──");
            System.out.println("  Total bookings this week: " + sharedVehicle.getBookingCount());
            sharedVehicle.resetWeeklyBookings();

            System.out.println("\n── Chores Completed ──");

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
                System.out.println("\n🏆 " + winner.getName() + " wins this week with " + winner.getChorePoints() + " pts!");
                System.out.println("  " + winner.getName() + ", as this week's winner you can tell your partner");
                System.out.println("  which chore you're skipping — they'll cover it for next week. Good job! 🎉");
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

    // ==========================================================
    //  HELPERS
    // ==========================================================

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

    /**
     * Reads a name from the user. Names can only contain letters and spaces.
     */
    public static String readName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                return name;
            }
            if (name.matches("[a-zA-Z\u0590-\u05FF ]+")) {
                return name;
            }
            System.out.println("Invalid name. Names can only contain letters and spaces.");
        }
    }

    /**
     * Asks the user to choose between partner 1 or 2. Loops until valid input.
     */
    public static Partner readPartnerChoice() {
        while (true) {
            int choice = readInt("Choice (1 or 2): ");
            if (choice == 1) return partnerA;
            if (choice == 2) return partnerB;
            System.out.println("Please enter 1 or 2.");
        }
    }

    /**
     * Reads an integer in a specific range. Loops until valid.
     * Returns the chosen value, or -1 if the user enters 0 to cancel.
     */
    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int val = readInt(prompt);
            if (val == 0) return -1;
            if (val >= min && val <= max) return val;
            System.out.println("Please enter a number between " + min + " and " + max + " (or 0 to cancel).");
        }
    }

    /**
     * Asks a yes/no confirmation question. Returns true if confirmed.
     */
    public static boolean confirmAction(String message) {
        System.out.println("\n  " + message);
        System.out.println("  1. Yes  0. No");
        while (true) {
            int choice = readInt("  Confirm (1 or 0): ");
            if (choice == 1) return true;
            if (choice == 0) return false;
            System.out.println("  Please enter 1 (Yes) or 0 (No).");
        }
    }

    public static String getTodayString() {
        return String.format("%02d/%02d/%04d", currentDate.getDayOfMonth(), currentDate.getMonthValue(), currentDate.getYear());
    }

    public static void advanceDay() {
        System.out.println("\n── Advance One Day ─────────────────────");
        System.out.println("  Here you can simulate the passage of time by advancing one day.");
        System.out.println("  This will update recurring chores and may trigger a weekly summary.\n");
        System.out.println("  Current date: " + getTodayString());
        if (!confirmAction("Advance to the next day?")) {
            System.out.println("  Cancelled.");
            return;
        }
        currentDate = currentDate.plusDays(1);
        System.out.println("A day has passed! Current system date is now: " + getTodayString());

        // Advance recurring chores
        for (int i = 0; i < choreCount; i++) {
            if (choreList[i] instanceof RecurringChore) {
                ((RecurringChore) choreList[i]).checkAndResetAvailability();
            }
        }

        // Auto-approve expired bookings
        int queueSize = bookingQueue.size();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (int i = 0; i < queueSize; i++) {
            VehicleBooking booking = bookingQueue.dequeueSilent();
            LocalDate bookingDate = LocalDate.parse(booking.getBookingDate(), formatter);

            if (bookingDate.isBefore(currentDate)) {
                // Expired! Auto approve
                try {
                    boolean success = sharedVehicle.addBooking(booking);
                    if (success) {
                        System.out.println("  Booking on " + booking.getBookingDate() + " for " + booking.getBookingPartner().getName() + " was auto-approved because the date passed.");
                        booking.sendAlert("Your booking on " + booking.getBookingDate() + " was automatically approved.");
                    } else {
                        System.out.println("  Auto-approval failed for " + booking.getBookingPartner().getName() + " on " + booking.getBookingDate() + " (Conflict).");
                    }
                } catch (Exception e) {
                    System.out.println("  Error auto-approving: " + e.getMessage());
                }
            } else {
                // Still valid, keep in queue
                bookingQueue.enqueueSilent(booking);
            }
        }
    }
}