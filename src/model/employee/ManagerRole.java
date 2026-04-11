package model.employee;

import service.OrderManager;

public class ManagerRole extends Role {
    @Override 
    public boolean canEditOrder(){
        return true;
    }

    @Override
    public boolean accessAdminPanel() {
        return true;
    }

    @Override
    public void displayMenu(String employeeName) {
        System.out.println("\n=== Employee Menu (Manager) ===");
        System.out.println("Welcome, " + employeeName);
        System.out.println("1. View Order");
        System.out.println("2. Search Order");
        System.out.println("3. Cancel Processing Order");
        System.out.println("4. Access Admin Panel");
        System.out.println("5. Logout");
        System.out.print("Choose: ");
    }

    @Override
    public int handleMenuChoice(int choice, OrderManager orderManager) {
        switch (choice) {
            case 1:
                handleOrder(orderManager);
                return 0;
            case 2:
                searchOrder(orderManager);
                return 0;
            case 3:
                cancelOrder(orderManager);
                return 0;
            case 4:
                return 2; // Access Admin Panel
            case 5:
                return 1; // Logout
            default:
                System.out.println("Invalid choice! Please enter 1-5.");
                return 0;
        }
    }
}
