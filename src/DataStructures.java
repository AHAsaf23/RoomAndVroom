/**
 * Room & Vroom - Shared Household Management System
 * Authors: גל קסירר (318158466), אסף שוורץ (207812744), אסף חיון (214195331)
 * <p>
 * DataStructures.java - Contains all 4 required data structures:
 * <p>
 * 1. TransactionStack    — Stack  : undo history of financial transactions
 * 2. BookingQueue        — Queue  : pending vehicle booking requests
 * 3. ChoreLinkedList     — Linked List : active chore list
 * 4. ExpenseCategoryTree — Binary Search Tree : expense categories + totals
 */


// ══════════════════════════════════════════════════════════════════════════════
//  1. STACK — Transaction history (supports undo)
// ══════════════════════════════════════════════════════════════════════════════

class TransactionStack {

    private static class Node {
        FinancialTransaction data;
        Node next;

        Node(FinancialTransaction data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node top;

    public TransactionStack() {
        this.top = null;
    }

    /**
     * Push a transaction onto the stack
     */
    public void push(FinancialTransaction transaction) {
        Node newNode = new Node(transaction);
        newNode.next = top;
        top = newNode;
        System.out.println("📥 Transaction added: " + transaction.getSummary());
    }

    /**
     * Pop the most recent transaction (undo)
     */
    public FinancialTransaction pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot undo: transaction history is empty.");
        }
        FinancialTransaction data = top.data;
        top = top.next;
        System.out.println("↩️  Undone: " + data.getSummary());
        return data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public void printAll() {
        System.out.println("── Transaction History (Stack) ────────");
        if (isEmpty()) {
            System.out.println("  (empty)");
            return;
        }
        Node current = top;
        int i = 1;
        while (current != null) {
            System.out.println("  " + i++ + ". " + current.data.getSummary());
            current = current.next;
        }
    }
}


// ══════════════════════════════════════════════════════════════════════════════
//  2. QUEUE — Pending vehicle booking requests
// ══════════════════════════════════════════════════════════════════════════════

class BookingQueue {

    private static class Node {
        VehicleBooking data;
        Node next;

        Node(VehicleBooking data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node front;
    private Node rear;

    public BookingQueue() {
        this.front = null;
        this.rear = null;
    }

    /**
     * Add a booking request to the back of the queue
     */
    public void enqueue(VehicleBooking booking) {
        Node newNode = new Node(booking);
        if (rear != null) rear.next = newNode;
        rear = newNode;
        if (front == null) front = newNode;
        System.out.println("📋 Booking request added for " +
                booking.getBookingPartner().getName() + " on " + booking.getBookingDate());
    }

    /**
     * Process (remove) the next booking request
     */
    public VehicleBooking dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("No pending booking requests.");
        }
        VehicleBooking data = front.data;
        front = front.next;
        if (front == null) rear = null;
        System.out.println("✔ Processing booking for " +
                data.getBookingPartner().getName() + " on " + data.getBookingDate());
        return data;
    }

    /**
     * Peek at the next request without removing
     */
    public VehicleBooking peek() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty.");
        return front.data;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public void printAll() {
        System.out.println("── Pending Booking Requests (Queue) ───");
        if (isEmpty()) {
            System.out.println("  (no pending requests)");
            return;
        }
        Node current = front;
        int i = 1;
        while (current != null) {
            System.out.println("  " + i++ + ". " + current.data);
            current = current.next;
        }
    }
}


// ══════════════════════════════════════════════════════════════════════════════
//  3. LINKED LIST — Active chore list
// ══════════════════════════════════════════════════════════════════════════════

class ChoreLinkedList {

    private static class Node {
        Chore data;
        Node next;

        Node(Chore data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;

    public ChoreLinkedList() {
        this.head = null;
    }

    /**
     * Add a chore to the end of the list
     */
    public void add(Chore chore) {
        Node newNode = new Node(chore);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) current = current.next;
            current.next = newNode;
        }
        System.out.println("➕ Chore added: " + chore.getDescription());
    }

    /**
     * Remove a chore by description
     */
    public boolean remove(String description) {
        if (head == null) return false;

        if (head.data.getDescription().equals(description)) {
            head = head.next;
            System.out.println("➖ Removed: " + description);
            return true;
        }

        Node current = head;
        while (current.next != null) {
            if (current.next.data.getDescription().equals(description)) {
                current.next = current.next.next;
                System.out.println("➖ Removed: " + description);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void printAll() {
        System.out.println("── Chore List (LinkedList) ────────────");
        if (isEmpty()) {
            System.out.println("  (no chores)");
            return;
        }
        Node current = head;
        int i = 1;
        while (current != null) {
            System.out.println("  " + i++ + ". " + current.data.getLabel());
            current = current.next;
        }
    }
}


// ══════════════════════════════════════════════════════════════════════════════
//  4. BINARY SEARCH TREE — Expense categories with running totals
// ══════════════════════════════════════════════════════════════════════════════

class ExpenseCategoryTree {

    private static class Node {
        String category;
        double totalAmount;
        int count;
        Node left, right;

        Node(String category, double amount) {
            this.category = category;
            this.totalAmount = amount;
            this.count = 1;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;

    public ExpenseCategoryTree() {
        this.root = null;
    }

    /**
     * Insert an expense amount into its category node (or update if exists)
     */
    public void insert(String category, double amount) {
        root = insertRec(root, category, amount);
    }

    private Node insertRec(Node node, String category, double amount) {
        if (node == null) {
            System.out.println("🌿 New category: " + category + " (" + String.format("%.2f", amount) + " NIS)");
            return new Node(category, amount);
        }
        int cmp = category.compareToIgnoreCase(node.category);
        if (cmp < 0) {
            node.left = insertRec(node.left, category, amount);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, category, amount);
        } else {
            // Category exists — update total
            node.totalAmount += amount;
            node.count++;
            System.out.println("📊 Updated " + category + ": total = " + String.format("%.2f", node.totalAmount) + " NIS");
        }
        return node;
    }


    /**
     * Print all categories in alphabetical order (in-order traversal)
     */
    public void printAll() {
        System.out.println("── Expense Categories (Tree, A→Z) ─────");
        if (root == null) {
            System.out.println("  (no expenses recorded)");
            return;
        }
        printInOrder(root);
    }

    private void printInOrder(Node node) {
        if (node == null) return;
        printInOrder(node.left);
        System.out.printf("  %-15s %3d expense(s)  total: %.2f NIS%n",
                node.category, node.count, node.totalAmount);
        printInOrder(node.right);
    }
}
