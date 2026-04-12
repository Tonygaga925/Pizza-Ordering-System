package view;

import java.util.List;
import model.order.OrderItem;

public class MainView {
    public static void displayMessage(String msg) {
        System.out.println(msg);
    }
    
    public static void displayMainMenu() {
        System.out.println("\n=== Pizza Ordering System ===");
        System.out.println("1. Member Login");
        System.out.println("2. Member Register");
        System.out.println("3. Continue as Guest");
        System.out.println("4. Search Order by ID");
        System.out.println("5. Employee Login");
        System.out.println("6. Exit");
        System.out.print("Choose: ");
    }
    
    public static void displayCart(List<OrderItem> items) {
        System.out.println("\n=== Current Cart ===");
        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            double itemTotal = item.getItemTotal();
            total += itemTotal;
            System.out.printf("%d. %s - $%.2f each, Total: $%.2f%n",
                    i + 1,
                    item.getDescription(),
                    item.getSingleItemTotal(),
                    itemTotal);
        }
        System.out.printf("Cart Total: $%.2f%n", total);
        System.out.println("==================");
    }
}
