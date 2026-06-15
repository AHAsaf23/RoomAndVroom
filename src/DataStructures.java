//  Transaction history stack
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

    // Adds a new transaction to the top of the stack
    public void push(FinancialTransaction transaction) {
        Node newNode = new Node(transaction);
        newNode.next = top;
        top = newNode;
        System.out.println("Transaction added: " + transaction.getSummary());
    }

    // Removes and returns the most recent transaction from the stack
    public FinancialTransaction pop() {
        FinancialTransaction data = top.data;
        top = top.next;
        System.out.println("Undone: " + data.getSummary());
        return data;
    }
    // Checks if the stack is completely empty
    public boolean isEmpty() {
        return top == null;
    }

    // Prints all the transactions we saved from newest to oldest
    public void printAll() {
        System.out.println("── Transaction History ────────────────");
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



//  Pending vehicle booking requests (queue)
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

    // Adds a new booking request to the back of the queue and prints a message

    public void enqueue(VehicleBooking booking) {
        enqueueSilent(booking);
        System.out.println("Booking request added for " +
                booking.getBookingPartner().getName() + " on " + booking.getBookingDate());
    }

    // We use this method when we advance a day and need to put future bookings back into the queue
    public void enqueueSilent(VehicleBooking booking) {
        Node newNode = new Node(booking);
        if (rear != null) rear.next = newNode;
        rear = newNode;
        if (front == null) front = newNode;
    }

    // Removes the next booking request from the queue and prints a message to the screen
    public VehicleBooking dequeue() {
        VehicleBooking data = dequeueSilent();
        System.out.println("Processing booking for " +
                data.getBookingPartner().getName() + " on " + data.getBookingDate());
        return data;
    }

    // Removes the next booking request from the queue silently
    public VehicleBooking dequeueSilent() {
        VehicleBooking data = front.data;
        front = front.next;
        if (front == null) rear = null;
        return data;
    }

    // Lets us look at the next request in line without actually removing it
    public VehicleBooking peek() {
        return front.data;
    }

    public boolean isEmpty() {
        return front == null;
    }

    // Counts how many bookings are currently in the queue
    public int size() {
        int count = 0;
        Node current = front;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    public void printAll() {
        System.out.println("── Pending Booking Requests ───────────");
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

//  Active chore list (Linked list)
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

    // Adds a new chore to the very end of the linked list
    public void add(Chore chore) {
        Node newNode = new Node(chore);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) current = current.next;
            current.next = newNode;
        }

    }

    // Searches for a chore by its name and removes it from the list. Returns true if successful.
    public boolean remove(String description) {
        if (head == null) return false;

        if (head.data.getDescription().equals(description)) {
            head = head.next;
            return true;
        }

        Node current = head;
        while (current.next != null) {
            if (current.next.data.getDescription().equals(description)) {
                current.next = current.next.next;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Checks if the chore list is completely empty
    public boolean isEmpty() {
        return head == null;
    }

    // Prints all the chores one by one
    public void printAll() {
        System.out.println("── Chore List ─────────────────────────");
        if (isEmpty()) {
            System.out.println("  (no chores)");
            return;
        }
        Node current = head;
        int i = 1;
        while (current != null) {
            System.out.println("  " + i++ + ". " + current.data.toString());
            current = current.next;
        }
    }
}



//  Binary Search Tree of the expense categories with amount spent

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

    // Inserts a new expense into its category, or adds the amount to an existing one
    public void insert(String category, double amount) {
        root = insertRec(root, category, amount);
    }

    // Helper function that uses recursion to find the right spot in the tree
    private Node insertRec(Node node, String category, double amount) {
        if (node == null) {
            System.out.println("  New category: " + category + " (" + String.format("%.2f", amount) + " NIS)");
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
            System.out.println("  Updated " + category + ": total = " + String.format("%.2f", node.totalAmount) + " NIS");
        }
        return node;
    }


    // Prints all categories in alphabetical order (in-order traversal)
    public void printAll() {
        System.out.println("── Expense Categories ─────────────────");
        if (root == null) {
            System.out.println("  (no expenses recorded)");
            return;
        }
        printInOrder(root);
    }

    // Helper function that uses recursion to print the tree alphabetically (left, root, right)
    private void printInOrder(Node node) {
        if (node == null) return;
        printInOrder(node.left);
        System.out.printf("  %-15s %3d expense(s)  total: %.2f NIS%n",
                node.category, node.count, node.totalAmount);
        printInOrder(node.right);
    }
}