package view;

public class EmployeeView {
    public static void displayManagerMenu(String employeeName) {
        System.out.println("\n=== Employee Menu (Manager) ===");
        System.out.println("Welcome, " + employeeName);
        System.out.println("1. View Order");
        System.out.println("2. Search Order");
        System.out.println("3. Cancel Processing Order");
        System.out.println("4. Manage Coupons");
        System.out.println("5. Access Admin Panel");
        System.out.println("6. Logout");
        System.out.print("Choose: ");
    }

    public static void displayStaffMenu(String employeeName) {
        System.out.println("\n=== Employee Menu (Staff) ===");
        System.out.println("Welcome, " + employeeName);
        System.out.println("1. View Order");
        System.out.println("2. Search Order");
        System.out.println("3. Cancel Processing Order");
        System.out.println("4. Logout");
        System.out.print("Choose: ");
    }
}
