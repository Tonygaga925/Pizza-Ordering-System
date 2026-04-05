import model.Order;
import model.Pizza;
import model.Member;
import model.size.*;
import model.topping.*;
import model.topping.Topping;
import model.topping.ToppingFactory;
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
            orderManager.setMenuLoader(menuLoader);
            
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
        if (choice == -1) return; // Invalid input
        
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
    
    // Helper method to safely get integer input
    private static int getIntInput() {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // consume newline
                return input;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input! Please enter a number: ");
                scanner.nextLine(); // clear invalid input
            }
        }
    }
    
    // Overloaded method with custom prompt
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
        List<Pizza> pizzas = menuLoader.getPizzas();
        for (int i = 0; i < pizzas.size(); i++) {
            Pizza p = pizzas.get(i);
            System.out.printf("%d. %s - $%.2f %n",
                    i+1, p.getName(), p.getBasePrice());
        }
    }
    
    private static void placeOrder(boolean isMember) throws IOException {
    showMenu();
    
    int pizzaIndex = getIntInput("Choose pizza number: ") - 1;
    
    if (pizzaIndex < 0 || pizzaIndex >= menuLoader.getPizzas().size()) {
        System.out.println("Invalid pizza choice!");
        return;
    }
    
    Pizza selectedPizza = menuLoader.getPizzas().get(pizzaIndex);
    
    // Display size options using factory
    SizeFactory.displaySizeOptions();
    int sizeChoice = getIntInput("Choose size (1-3): ");
    
    Size selectedSize;
    try {
        selectedSize = SizeFactory.getSize(sizeChoice);
    } catch (IllegalArgumentException e) {
        System.out.println("Invalid size choice!");
        return;
    }
    
    // Select extra toppings using factory pattern
    // Select extra toppings using factory pattern
    List<Topping> selectedToppings = selectToppings();  // List<Topping>
    
    // Calculate costs
    double baseWithSize = selectedPizza.getBasePrice() * selectedSize.getMultiplier();
    double extraCost = 0;
    int totalPointsFromToppings = 0;
    
    for (Topping topping : selectedToppings) {
        extraCost += topping.getPrice();
        totalPointsFromToppings += topping.getPointsValue();
    }
    
    double originalTotal = baseWithSize + extraCost;

    // Calculate final total after discount
    double finalTotal = originalTotal;
    if (isMember) {
        Member member = memberManager.getCurrentMember();
        finalTotal = originalTotal * (1 - member.getDiscount());
        
        System.out.println("\n--- Order Summary ---");
        System.out.println("Pizza: " + selectedPizza.getName());
        System.out.println("Size: " + selectedSize.getName() + " (x" + selectedSize.getMultiplier() + ")");
        System.out.println("Base price: $" + String.format("%.2f", baseWithSize));
        
        if (!selectedToppings.isEmpty()) {
            System.out.println("Extra toppings:");
            for (Topping topping : selectedToppings) {
                System.out.println("  - " + topping.getName() + ": $" + String.format("%.2f", topping.getPrice()));
            }
            System.out.println("Toppings total: $" + String.format("%.2f", extraCost));
        }
        
        System.out.println("Original total: $" + String.format("%.2f", originalTotal));
        
        if (member.getDiscount() > 0) {
            System.out.println("VIP discount (10%): -$" + String.format("%.2f", originalTotal * member.getDiscount()));
            System.out.println("Final total: $" + String.format("%.2f", finalTotal));
        } else {
            System.out.println("Total: $" + String.format("%.2f", finalTotal));
        }
    } else {
        System.out.println("\n--- Order Summary ---");
        System.out.println("Pizza: " + selectedPizza.getName());
        System.out.println("Size: " + selectedSize.getName() + " (x" + selectedSize.getMultiplier() + ")");
        System.out.println("Base price: $" + String.format("%.2f", baseWithSize));
        
        if (!selectedToppings.isEmpty()) {
            System.out.println("Extra toppings:");
            for (Topping topping : selectedToppings) {
                System.out.println("  - " + topping.getName() + ": $" + String.format("%.2f", topping.getPrice()));
            }
            System.out.println("Toppings total: $" + String.format("%.2f", extraCost));
        }
        
        System.out.println("Total: $" + String.format("%.2f", originalTotal));
    }
    
    // Convert Topping objects to strings for order storage
    List<String> toppingNames = new ArrayList<>();
    for (Topping topping : selectedToppings) {
        toppingNames.add(topping.getName());
    }
    
    String orderId;
    if (isMember) {
        Member member = memberManager.getCurrentMember();
        orderId = orderManager.placeOrder(
            member.getId(), member.getName(), member.getPhone(),
            selectedPizza, selectedSize, selectedToppings, originalTotal 
        );
    } else {
        System.out.print("Enter your name: ");
        String customerName = scanner.nextLine();
        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine();
        orderId = orderManager.placeOrder(
            customerName, phone, selectedPizza, selectedSize, selectedToppings, originalTotal
        );
    }
    
    System.out.printf("\nOrder placed successfully! Order ID: %s%n", orderId);
    System.out.println("Please save your Order ID for future reference!");
}

private static List<Topping> selectToppings() {
    List<Topping> selectedToppings = new ArrayList<>();
    Set<Integer> selectedNumbers = new HashSet<>();
    
    ToppingFactory.displayToppingMenu();
    
    while (true) {
        int choice = getIntInput("Enter topping number (0 to finish): ");
        
        if (choice == 0) {
            if (selectedToppings.isEmpty()) {
                System.out.println("No toppings selected. Continuing without extra toppings.");
            } else {
                System.out.println("\nToppings selected:");
                double totalToppingCost = 0;
                int totalToppingPoints = 0;
                for (Topping t : selectedToppings) {
                    System.out.println("  - " + t.getName() + " ($" + String.format("%.2f", t.getPrice()) + ", " + t.getPointsValue() + " points)");
                    totalToppingCost += t.getPrice();
                    totalToppingPoints += t.getPointsValue();
                }
                System.out.printf("Total topping cost: $%.2f%n", totalToppingCost);
                System.out.println("Total topping points: " + totalToppingPoints);
            }
            break;
        }
        
        try {
            if (selectedNumbers.contains(choice)) {
                System.out.println("You already selected this topping! Please choose a different one.");
                continue;
            }
            
            Topping topping = ToppingFactory.getTopping(choice);
            selectedToppings.add(topping);
            selectedNumbers.add(choice);
            System.out.println("Added: " + topping.getName() + " ($" + String.format("%.2f", topping.getPrice()) + ")");
            
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid choice! Please enter a number between 0 and " + ToppingFactory.getAllToppings().size());
        }
    }
    
    return selectedToppings;
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
}