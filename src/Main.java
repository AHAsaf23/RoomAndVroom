public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Welcome to Room & Vroom");
        System.out.println("========================================\n");
        
        Menu_func.setupPartners();
        Menu_func.setupVehicle();
        Menu_func.setupChores();

        boolean running = true;
        while (running) {
            Menu_func.printMenu();
            int choice = Menu_func.readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    Menu_func.addExpense();
                    break;
                case 2:
                    Menu_func.requestBooking();
                    break;
                case 3:
                    Menu_func.processNextBooking();
                    break;
                case 4:
                    Menu_func.completeChore();
                    break;
                case 5:
                    Menu_func.undoLastTransaction();
                    break;
                case 6:
                    Menu_func.printStatus();
                    break;
                case 7:
                    Menu_func.printAllDataStructures();
                    break;
                case 8:
                    Menu_func.advanceDay();
                    break;
                case 9:
                    Menu_func.manageChores();
                    break;
                case 0:
                    System.out.println("\nGoodbye! Keep it fair.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        Menu_func.scanner.close();
    }
}