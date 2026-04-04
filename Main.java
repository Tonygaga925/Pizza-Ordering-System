import model.Pizza;
import model.Order;
import service.MenuLoader;
import service.OrderManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            MenuLoader menuLoader = new MenuLoader("data/menu.json");
            OrderManager orderManager = new OrderManager("data/orders.json");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n=== Pizza Ordering System ===");
                System.out.println("1. Show Menu");
                System.out.println("2. Place Order");
                System.out.println("3. View Orders History");
                System.out.println("4. Exit");
                System.out.print("Choose: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        showMenu(menuLoader);
                        break;
                    case 2:
                        placeOrder(scanner, menuLoader, orderManager);
                        break;
                    case 3:
                        orderManager.showAllOrders();
                        break;
                    case 4:
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice");
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading files: " + e.getMessage());
        }
    }

    private static void showMenu(MenuLoader menuLoader) {
        System.out.println("\n--- Menu ---");
        for (int i = 0; i < menuLoader.getPizzas().size(); i++) {
            Pizza p = menuLoader.getPizzas().get(i);
            System.out.printf("%d. %s - $%.2f (Toppings: %s)%n",
                    i+1, p.getName(), p.getBasePrice(), String.join(", ", p.getToppings()));
        }
        System.out.println("Sizes: Small(x1.0), Medium(x1.3), Large(x1.6)");
        System.out.printf("Extra topping price: $%.2f%n", menuLoader.getExtraToppingPrice());
    }

    private static void placeOrder(Scanner scanner, MenuLoader menuLoader, OrderManager orderManager) throws IOException {
        System.out.print("Enter your name: ");
        String customerName = scanner.nextLine();

        showMenu(menuLoader);
        System.out.print("Choose pizza number: ");
        int pizzaIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (pizzaIndex < 0 || pizzaIndex >= menuLoader.getPizzas().size()) {
            System.out.println("Invalid pizza");
            return;
        }
        Pizza selected = menuLoader.getPizzas().get(pizzaIndex);

        System.out.print("Enter size (Small/Medium/Large): ");
        String size = scanner.nextLine();
        if (!menuLoader.getSizeMultiplier().containsKey(size)) {
            System.out.println("Invalid size");
            return;
        }
        double multiplier = menuLoader.getSizeMultiplier().get(size);

        List<String> extraToppings = new ArrayList<>();
        System.out.print("Extra toppings? (comma-separated, e.g., Mushrooms,Olives) or press enter to skip: ");
        String extraLine = scanner.nextLine();
        if (!extraLine.trim().isEmpty()) {
            extraToppings = Arrays.asList(extraLine.split("\\s*,\\s*"));
        }

        double baseWithSize = selected.getBasePrice() * multiplier;
        double extraCost = extraToppings.size() * menuLoader.getExtraToppingPrice();
        double total = baseWithSize + extraCost;

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Order order = new Order(customerName, selected.getName(), size, extraToppings, total, timestamp);
        orderManager.saveOrder(order);

        System.out.printf("Order placed! Total: $%.2f%n", total);
    }
}