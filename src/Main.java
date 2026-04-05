import model.pizza.*;
import model.Member;
import model.command.AddToppingCommand;
import model.command.CommandHistory;
import model.size.*;
import model.order.*;
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
            
            // Set references
            orderManager.setMemberManager(memberManager);
            
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
        
        int choice = getIntInput();
        if (choice == -1) return;
        
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
                    System.out.println("Invalid choice! Please enter 1-5.");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void showMemberMenu() {
        Member current = memberManager.getCurrentMember();
        
        System.out.println("\n=== Welcome, " + current.getName() + " ===");
        System.out.println("Level: " + current.getLevelDisplay());
        System.out.println("Points: " + current.getPoints());
        
        int pointsToNext = current.getPointsToNextLevel();
        if (pointsToNext > 0) {
            System.out.println(pointsToNext + " more points to reach VIP!");
        }
        
        System.out.println("\n1. Show Menu");
        System.out.println("2. Place Order");
        System.out.println("3. View My Orders");
        System.out.println("4. Search Order by ID");
        System.out.println("5. View Member Info");
        System.out.println("6. Logout");
        System.out.print("Choose: ");
        
        int choice = getIntInput();
        if (choice == -1) return;
        
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
                    memberManager.displayMemberInfo();
                    break;
                case 6:
                    memberManager.logout();
                    System.out.println("Logged out successfully!");
                    break;
                default:
                    System.out.println("Invalid choice! Please enter 1-6.");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static int getIntInput() {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine();
                return input;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input! Please enter a number: ");
                scanner.nextLine();
            }
        }
    }
    
    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        return getIntInput();
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
        List<BasePizza> pizzas = menuLoader.getPizzas();
        for (int i = 0; i < pizzas.size(); i++) {
            BasePizza p = pizzas.get(i);
            System.out.printf("%d. %s - $%.2f (%d points)%n",
                    i+1, p.getName(), p.getBasePrice(), p.getPoints());
        }
        System.out.println("\nNote: You can add extra toppings after selecting your pizza.");
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    
    private static void placeOrder(boolean isMember) throws IOException {
        List<OrderItem> items = new ArrayList<>();
        boolean addingPizzas = true;
        
        while (addingPizzas) {
            System.out.println("\n=== Build Your Pizza ===");
            
            // Show pizza menu
            showPizzaMenu();
            int pizzaIndex = getIntInput("Choose pizza number (0 to finish): ") - 1;
            
            if (pizzaIndex == -1) {
                if (items.isEmpty()) {
                    System.out.println("No pizzas selected. Returning to menu.");
                    return;
                }
                break;
            }
            
            if (pizzaIndex < 0 || pizzaIndex >= menuLoader.getPizzas().size()) {
                System.out.println("Invalid pizza choice!");
                continue;
            }
            
            // Start with base pizza
            BasePizza selectedBase = menuLoader.getPizzas().get(pizzaIndex);

            // Choose size
            SizeFactory.displaySizeOptions();
            int sizeChoice = getIntInput("Choose size (input number): ");
            Size selectedSize;
            try {
                selectedSize = SizeFactory.getSize(sizeChoice);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid size choice!");
                continue;
            }

            double baseWithSizePrice = selectedBase.getBasePrice() * selectedSize.getMultiplier();

            MutablePizzaWrapper pizzaWrapper = new MutablePizzaWrapper(
                new BasePizza(selectedBase.getName(), baseWithSizePrice, selectedBase.getPoints())
            );
            
            // Initialize command history for this pizza
            CommandHistory history = new CommandHistory();
            
            // Add toppings with Undo/Redo support
            boolean addingToppings = true;
            while (addingToppings) {
                PizzaFactory.displayToppingMenu();
                System.out.println("\nCurrent pizza: " + pizzaWrapper.getDescription());
                System.out.printf("Current price: $%.2f%n", pizzaWrapper.getPrice());
                System.out.printf("Current points: %d%n", pizzaWrapper.getPoints());
                
                if (history.canUndo()) {
                    System.out.println("\nCommands: u=Undo, r=Redo, 0=Finish, or enter topping number");
                } else {
                    System.out.println("\nCommands: 0=Finish, or enter topping number");
                }
                
                String input = getStringInput("Enter choice: ");
                
                // Check for undo/redo commands
                if (input.equalsIgnoreCase("u") || input.equalsIgnoreCase("undo")) {
                    if (history.undo()) {
                        System.out.println("Undo successful!");
                        System.out.println("Current pizza: " + pizzaWrapper.getDescription());
                        System.out.printf("Current price: $%.2f%n", pizzaWrapper.getPrice());
                    } else {
                        System.out.println("Nothing to undo!");
                    }
                    continue;
                }
                
                if (input.equalsIgnoreCase("r") || input.equalsIgnoreCase("redo")) {
                    if (history.redo()) {
                        System.out.println("Redo successful!");
                        System.out.println("Current pizza: " + pizzaWrapper.getDescription());
                        System.out.printf("Current price: $%.2f%n", pizzaWrapper.getPrice());
                    } else {
                        System.out.println("Nothing to redo!");
                    }
                    continue;
                }
                
                // Parse topping choice
                int toppingChoice;
                try {
                    toppingChoice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a number, u for undo, or r for redo.");
                    continue;
                }
                
                if (toppingChoice == 0) {
                    addingToppings = false;
                    break;
                }
                
                // Check if topping is valid
                List<String> toppingNames = PizzaFactory.getToppingNames();
                if (toppingChoice < 1 || toppingChoice > toppingNames.size()) {
                    System.out.println("Invalid topping choice!");
                    continue;
                }
                
                String toppingName = toppingNames.get(toppingChoice - 1);
                
                // Check if topping already selected
                if (history.isToppingSelected(toppingName)) {
                    System.out.println("You already selected " + toppingName + "! Each topping can only be added once.");
                    continue;
                }
                
                // Create and execute command
                AddToppingCommand command = new AddToppingCommand(pizzaWrapper, toppingChoice, toppingName);
                history.executeCommand(command);
                System.out.println("Added: " + toppingName);
            }
            
            // Display final pizza
            System.out.println("\n--- Pizza Summary ---");
            System.out.println("Pizza: " + pizzaWrapper.getDescription());
            System.out.printf("Price: $%.2f%n", pizzaWrapper.getPrice());
            System.out.printf("Points: %d%n", pizzaWrapper.getPoints());
            
            // Choose quantity
            int quantity = getIntInput("Enter quantity (1-99): ");
            if (quantity < 1) quantity = 1;
            
            // Create order item
            OrderItem item = new OrderItem(pizzaWrapper.getPizza(), selectedSize, quantity);
            items.add(item);
            
            // Display current order total
            double currentTotal = calculateCurrentTotal(items);
            System.out.printf("\nCurrent order total: $%.2f%n", currentTotal);
            
            System.out.print("\nAdd another pizza? (y/n): ");
            String another = scanner.nextLine().toLowerCase();
            if (!another.equals("y")) {
                addingPizzas = false;
            }
        }
        
        String orderId = generateOrderId();
        
        // Set customer info and create order
        Order order;
        if (isMember) {
            Member member = memberManager.getCurrentMember();
            order = new Order(member.getId(), member.getName(), member.getPhone(), items);
        } else {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            System.out.print("Enter your phone number: ");
            String phone = scanner.nextLine();
            order = new Order(null, name, phone, items);
        }
        
        // 設置 orderId
        order.setOrderId(orderId);
        
        // Apply member discount
        if (isMember) {
            Member member = memberManager.getCurrentMember();
            double discountRate = member.getDiscount();
            if (discountRate > 0) {
                order.applyDiscount(discountRate);
                System.out.println("\nVIP discount applied!");
            }
        }
        
        // Display and confirm order
        order.displayOrder();
        
        System.out.print("\nConfirm order? (y/n): ");
        String confirm = scanner.nextLine().toLowerCase();
        if (confirm.equals("y")) {

            orderManager.placeOrder(order);
            
            if (isMember) {
                Member member = memberManager.getCurrentMember();
                memberManager.updateMemberPoints(member.getId(), order.getTotalPoints());
            }
            
            System.out.println("\nOrder placed successfully!");
            System.out.println("Your Order ID: " + orderId);
            System.out.println("Please save this ID for future reference.");
        } else {
            System.out.println("Order cancelled.");
        }
    }
    
    private static double calculateCurrentTotal(List<OrderItem> items) {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getItemTotal();
        }
        return total;
    }
    
    private static void showPizzaMenu() {
        System.out.println("\n--- Pizza Menu ---");
        List<BasePizza> pizzas = menuLoader.getPizzas();
        for (int i = 0; i < pizzas.size(); i++) {
            BasePizza p = pizzas.get(i);
            System.out.printf("%d. %s - $%.2f (%d points)%n", 
                i + 1, p.getName(), p.getBasePrice(), p.getPoints());
        }
        System.out.println("0. Finish / Checkout");
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
    
    private static void memberSearchOrderById() {
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine();
        
        Order order = orderManager.getOrderById(orderId);
        
        if (order == null) {
            System.out.println("Order not found!");
            return;
        }
        
        Member member = memberManager.getCurrentMember();
        
        if (order.getMemberId() != null && !order.getMemberId().equals(member.getId())) {
            System.out.println("You are not authorized to view this order!");
            return;
        }
        
        orderManager.displayOrder(order);
    }
    
    private static String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }
}