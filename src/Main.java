import model.Order;
import model.Pizza;
import model.Member;
import service.MenuLoader;
import service.OrderManager;
import service.MemberManager;

import java.io.IOException;
import java.util.*;

public class Main {
    private static MenuLoader menuLoader;
    private static OrderManager orderManager;
    private static MemberManager memberManager;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        try {
            menuLoader = new MenuLoader("data/menu.json");
            orderManager = new OrderManager("data/orders.json");
            memberManager = new MemberManager("data/members.json");
            scanner = new Scanner(System.in);
            
            while (true) {
                if (memberManager.isLoggedIn()) {
                    showMemberMenu();
                } else {
                    showMainMenu();
                }
            }
        } catch (IOException e) {
            System.err.println("System error: " + e.getMessage());
        }
    }
    
    private static void showMainMenu() {
        System.out.println("\n=== Pizza Ordering System ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Continue as Guest");
        System.out.println("4. Search Order by ID (Guest)");
        System.out.println("5. Exit");
        System.out.print("Choose: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        try {
            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    guestOrderFlow();
                    break;
                case 4:
                    guestSearchOrderById();
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void showMemberMenu() {
        Member current = memberManager.getCurrentMember();
        System.out.println("\n=== Welcome, " + current.getName() + "! ===");
        System.out.println("1. Show Menu");
        System.out.println("2. Place Order");
        System.out.println("3. View My Orders");
        System.out.println("4. Search Order by ID");
        System.out.println("5. Logout");
        System.out.print("Choose: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        try {
            switch (choice) {
                case 1:
                    showMenu();
                    break;
                case 2:
                    placeOrder(true);
                    break;
                case 3:
                    viewMyOrders();
                    break;
                case 4:
                    memberSearchOrderById();
                    break;
                case 5:
                    memberManager.logout();
                    System.out.println("Logged out successfully!");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void login() throws IOException {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        if (memberManager.login(username, password)) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password!");
        }
    }
    
    private static void register() throws IOException {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Full Name: ");
        String name = scanner.nextLine();
        System.out.print("Phone Number: ");
        String phone = scanner.nextLine();
        
        if (memberManager.register(username, password, name, phone)) {
            System.out.println("Registration successful! Please login.");
        } else {
            System.out.println("Username already exists!");
        }
    }
    
    private static void guestOrderFlow() throws IOException {
        System.out.println("\n--- Guest Order ---");
        placeOrder(false);
    }
    
    private static void showMenu() {
        System.out.println("\n--- Menu ---");
        List<Pizza> pizzas = menuLoader.getPizzas();
        for (int i = 0; i < pizzas.size(); i++) {
            Pizza p = pizzas.get(i);
            System.out.printf("%d. %s - $%.2f (Toppings: %s)%n",
                    i+1, p.getName(), p.getBasePrice(), String.join(", ", p.getToppings()));
        }
        System.out.println("Sizes: Small(x1.0), Medium(x1.3), Large(x1.6)");
        System.out.printf("Extra topping price: $%.2f%n", menuLoader.getExtraToppingPrice());
    }
    
    private static void placeOrder(boolean isMember) throws IOException {
        showMenu();
        
        System.out.print("Choose pizza number: ");
        int pizzaIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (pizzaIndex < 0 || pizzaIndex >= menuLoader.getPizzas().size()) {
            System.out.println("Invalid pizza choice!");
            return;
        }
        
        Pizza selected = menuLoader.getPizzas().get(pizzaIndex);
        
        System.out.print("Enter size (Small/Medium/Large): ");
        String size = scanner.nextLine();
        if (!menuLoader.getSizeMultiplier().containsKey(size)) {
            System.out.println("Invalid size!");
            return;
        }
        
        double multiplier = menuLoader.getSizeMultiplier().get(size);
        
        System.out.print("Extra toppings? (comma-separated, e.g., Mushrooms,Olives) or press enter to skip: ");
        String extraLine = scanner.nextLine();
        List<String> extraToppings = new ArrayList<>();
        if (!extraLine.trim().isEmpty()) {
            extraToppings = Arrays.asList(extraLine.split("\\s*,\\s*"));
        }
        
        double baseWithSize = selected.getBasePrice() * multiplier;
        double extraCost = extraToppings.size() * menuLoader.getExtraToppingPrice();
        double total = baseWithSize + extraCost;
        
        String orderId;
        if (isMember) {
            Member member = memberManager.getCurrentMember();
            orderId = orderManager.placeOrder(
                member.getId(), member.getName(), member.getPhone(),
                selected.getName(), size, extraToppings, total
            );
        } else {
            System.out.print("Enter your name: ");
            String customerName = scanner.nextLine();
            System.out.print("Enter your phone number: ");
            String phone = scanner.nextLine();
            orderId = orderManager.placeOrder(
                customerName, phone, selected.getName(), size, extraToppings, total
            );
        }
        
        System.out.printf("Order placed successfully! Order ID: %s%n", orderId);
        System.out.printf("Total: $%.2f%n", total);
        System.out.println("Please save your Order ID for future reference!");
    }
    
    private static void viewMyOrders() {
        Member member = memberManager.getCurrentMember();
        List<Order> orders = orderManager.getOrdersByMemberId(member.getId());
        
        if (orders.isEmpty()) {
            System.out.println("You have no orders yet.");
            return;
        }
        
        System.out.println("\n=== Your Orders ===");
        orderManager.displayOrders(orders);
        
        System.out.print("Enter order ID to view details (or press enter to skip): ");
        String orderId = scanner.nextLine();
        if (!orderId.trim().isEmpty()) {
            Order order = orderManager.getOrderById(orderId);
            if (order != null && order.getMemberId() != null && order.getMemberId().equals(member.getId())) {
                orderManager.displayOrder(order);
            } else {
                System.out.println("Order not found or you don't have permission!");
            }
        }
    }
    
    // Guest check order by id
    private static void guestSearchOrderById() {
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine();
        
        Order order = orderManager.getOrderById(orderId);
        
        if (order == null) {
            System.out.println("Order not found!");
            return;
        }
        
        System.out.println("\n=== Order Details (Guest View) ===");
        orderManager.displayOrder(order);
    }
    
    // Member check order
    private static void memberSearchOrderById() {
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine();
        
        Order order = orderManager.getOrderById(orderId);
        
        if (order == null) {
            System.out.println("Order not found!");
            return;
        }
        
        Member member = memberManager.getCurrentMember();
        
        // Check permission: Members can only view their own orders
        if (order.getMemberId() != null && !order.getMemberId().equals(member.getId())) {
            System.out.println("You are not authorized to view this order!");
            return;
        }
        
        orderManager.displayOrder(order);
    }
}