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
    boolean ordering = true;
    
    while (ordering) {
        System.out.println("\n=== Build Your Pizza ===");
        
        if (!items.isEmpty()) {
            displayCart(items);
            System.out.println("\nOptions:");
            System.out.println("1. Add new pizza");
            System.out.println("2. Modify existing pizza");
            System.out.println("3. Remove a pizza");
            System.out.println("4. Proceed to checkout");
            System.out.print("Choose: ");
            
            int option = getIntInput();
            switch (option) {
                case 1:
                    addNewPizza(items, isMember);
                    break;
                case 2:
                    modifyPizzaQuantity(items);
                    break;
                case 3:
                    removePizza(items);
                    break;
                case 4:
                    ordering = false;
                    break;
                default:
                    System.out.println("Invalid option!");
                    continue;
            }
        } else {
            System.out.println("Your cart is empty. Please add a pizza.");
            addNewPizza(items, isMember);
            
            System.out.print("\nAdd another pizza or Modify the cart? (y/n): ");
            String another = scanner.nextLine().toLowerCase();
            if (!another.equals("y")) {
                ordering = false;
            }
        }
    }

    if (items.isEmpty()) {
        System.out.println("No pizzas selected. Returning to menu.");
        return;
    }
    
    System.out.println("\n=== Final Cart ===");
    displayCart(items);
    
    // 生成訂單 ID
    String orderId = generateOrderId();
    
    // 設置客戶信息並創建訂單
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
    
    order.setOrderId(orderId);
    
    // 應用會員折扣
    if (isMember) {
        Member member = memberManager.getCurrentMember();
        double discountRate = member.getDiscount();
        if (discountRate > 0) {
            order.applyDiscount(discountRate);
            System.out.println("\nVIP discount applied!");
        }
    }
    
    // 顯示和確認訂單
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

private static void displayCart(List<OrderItem> items) {
    System.out.println("\n=== Current Cart ===");
    double total = 0;
    for (int i = 0; i < items.size(); i++) {
        OrderItem item = items.get(i);
        double itemTotal = item.getItemTotal();
        total += itemTotal;
        System.out.printf("%d. %s x%d - $%.2f each, Total: $%.2f%n",
            i + 1,
            item.getPizza().getDescription(),
            item.getQuantity(),
            item.getSingleItemTotal(),
            itemTotal);
    }
    System.out.printf("Cart Total: $%.2f%n", total);
    System.out.println("==================");
}

private static void addNewPizza(List<OrderItem> items, boolean isMember) throws IOException {
    System.out.println("\n--- Add New Pizza ---");
    
    // Show pizza menu
    showPizzaMenu();
    int pizzaIndex = getIntInput("Choose pizza number: ") - 1;
    
    if (pizzaIndex < 0 || pizzaIndex >= menuLoader.getPizzas().size()) {
        System.out.println("Invalid pizza choice!");
        return;
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
        return;
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
    
    System.out.println("Pizza added to cart!");
}

private static void modifyPizzaQuantity(List<OrderItem> items) {
    if (items.isEmpty()) {
        System.out.println("No pizzas to modify.");
        return;
    }
    
    displayCart(items);
    System.out.print("Enter pizza number to modify quantity: ");
    int index = getIntInput() - 1;
    
    if (index < 0 || index >= items.size()) {
        System.out.println("Invalid pizza number!");
        return;
    }
    
    OrderItem item = items.get(index);
    System.out.printf("Current quantity: %d%n", item.getQuantity());
    System.out.print("Enter new quantity (0 to remove): ");
    int newQuantity = getIntInput();
    
    if (newQuantity <= 0) {
        items.remove(index);
        System.out.println("Pizza removed from cart.");
    } else {
        item.setQuantity(newQuantity);
        System.out.println("Quantity updated!");
    }
    
    displayCart(items);
}

private static void removePizza(List<OrderItem> items) {
    if (items.isEmpty()) {
        System.out.println("No pizzas to remove.");
        return;
    }
    
    displayCart(items);
    System.out.print("Enter pizza number to remove: ");
    int index = getIntInput() - 1;
    
    if (index < 0 || index >= items.size()) {
        System.out.println("Invalid pizza number!");
        return;
    }
    
    OrderItem removed = items.remove(index);
    System.out.printf("Removed: %s%n", removed.getPizza().getDescription());
    
    if (!items.isEmpty()) {
        displayCart(items);
    } else {
        System.out.println("Cart is now empty.");
    }
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
// for member use    
 private static void viewMyOrders() {
    Member member = memberManager.getCurrentMember();
    List<Order> orders = orderManager.getOrdersByMemberId(member.getId());
    
    if (orders.isEmpty()) {
        System.out.println("You have no orders yet.");
        return;
    }
    
    System.out.println("\n=== Your Orders ===");
    orderManager.displayOrders(orders);
    
    System.out.print("\nEnter order ID to view details, or 'r' to reorder a previous order: ");
    String input = scanner.nextLine();
    
    if (input.trim().isEmpty()) {
        return;
    }
    
    if (input.equalsIgnoreCase("r")) {
        reorderPreviousOrder(orders);
        return;
    }
    
    Order order = orderManager.getOrderById(input);
    if (order != null && order.getMemberId() != null && order.getMemberId().equals(member.getId())) {
        orderManager.displayOrder(order);
        
        System.out.print("\nWould you like to reorder this? (y/n): ");
        String reorderChoice = scanner.nextLine().toLowerCase();
        if (reorderChoice.equals("y")) {
            reorderOrder(order);
        }
    } else {
        System.out.println("Order not found or you don't have permission!");
    }
}

private static void reorderPreviousOrder(List<Order> orders) {
    if (orders.isEmpty()) {
        System.out.println("No orders to reorder.");
        return;
    }
    
    System.out.println("\n=== Select Order to Reorder ===");
    for (int i = 0; i < orders.size(); i++) {
        Order order = orders.get(i);
        String timestamp = order.getTimestamp();
        if (timestamp != null && timestamp.length() > 19) {
            timestamp = timestamp.substring(0, 19);
        }
        System.out.printf("%d. %s - %d item(s) - $%.2f%n", 
            i + 1, timestamp, order.getItems().size(), order.getFinalTotal());
    }
    
    System.out.print("\nChoose order number (1-" + orders.size() + "): ");
    int choice = getIntInput() - 1;
    
    if (choice < 0 || choice >= orders.size()) {
        System.out.println("Invalid choice!");
        return;
    }
    
    Order selectedOrder = orders.get(choice);
    reorderOrder(selectedOrder);
}

private static void reorderOrder(Order originalOrder) {
    System.out.println("\n=== Reordering Previous Order ===");
    System.out.println("Original order date: " + originalOrder.getTimestamp());
    
    List<OrderItem> newItems = new ArrayList<>();

    // copy the original items
    for (OrderItem originalItem : originalOrder.getItems()) {

        Pizza recreatedPizza = recreatePizza(originalItem.getPizza());
        OrderItem newItem = new OrderItem(recreatedPizza, originalItem.getSize(), originalItem.getQuantity());
        newItems.add(newItem);
        
        System.out.println("Added: " + newItem.getDescription());
    }
    
    System.out.println("\n=== Reorder Summary ===");
    double total = 0;
    for (OrderItem item : newItems) {
        double itemTotal = item.getItemTotal();
        total += itemTotal;
        System.out.printf("  %s - $%.2f%n", item.getDescription(), itemTotal);
    }
    System.out.printf("Total: $%.2f%n", total);
    
    System.out.print("\nConfirm reorder? (y/n): ");
    String confirm = scanner.nextLine().toLowerCase();
    if (!confirm.equals("y")) {
        System.out.println("Reorder cancelled.");
        return;
    }
    
    Member currentMember = memberManager.getCurrentMember();
    
    Order newOrder = new Order(currentMember.getId(), currentMember.getName(), currentMember.getPhone(), newItems);
    String orderId = generateOrderId();
    newOrder.setOrderId(orderId);
    
    double discountRate = currentMember.getDiscount();
    if (discountRate > 0) {
        newOrder.applyDiscount(discountRate);
        System.out.println("VIP discount applied!");
    }
    
    newOrder.displayOrder();
    
    System.out.print("\nPlace this order? (y/n): ");
    String placeConfirm = scanner.nextLine().toLowerCase();
    if (placeConfirm.equals("y")) {
        try {
            orderManager.placeOrder(newOrder);
            memberManager.updateMemberPoints(currentMember.getId(), newOrder.getTotalPoints());
            System.out.println("\nReorder placed successfully!");
            System.out.println("Your new Order ID: " + orderId);
        } catch (IOException e) {
            System.out.println("Error placing reorder: " + e.getMessage());
        }
    } else {
        System.out.println("Reorder cancelled.");
    }
}

private static Pizza recreatePizza(Pizza originalPizza) {

    BasePizza basePizza = getBasePizza(originalPizza);
    if (basePizza == null) {
        System.out.println("Error: Cannot recreate pizza");
        return originalPizza;
    }
    

    Pizza newPizza = new BasePizza(basePizza.getName(), basePizza.getBasePrice(), basePizza.getPoints());
    
    List<String> toppings = getToppingsFromPizza(originalPizza);
    
    for (String toppingName : toppings) {
        int toppingChoice = getToppingChoiceByName(toppingName);
        if (toppingChoice != -1) {
            try {
                newPizza = PizzaFactory.addTopping(newPizza, toppingChoice);
            } catch (Exception e) {
                System.out.println("Warning: Could not add topping " + toppingName);
            }
        }
    }
    
    return newPizza;
}

private static BasePizza getBasePizza(Pizza pizza) {
    Pizza current = pizza;
    while (current instanceof ToppingDecorator) {
        current = ((ToppingDecorator) current).getPizza();
    }
    if (current instanceof BasePizza) {
        return (BasePizza) current;
    }
    return null;
}

private static List<String> getToppingsFromPizza(Pizza pizza) {
    List<String> toppings = new ArrayList<>();
    Pizza current = pizza;
    
    while (current instanceof ToppingDecorator) {
        String description = current.getDescription();

        ToppingDecorator decorator = (ToppingDecorator) current;
        String toppingName = decorator.getClass().getSimpleName().replace("Topping", "");
        toppings.add(toppingName);
        current = decorator.getPizza();
    }
    
    return toppings;
}

private static int getToppingChoiceByName(String toppingName) {
    List<String> toppingNames = PizzaFactory.getToppingNames();
    for (int i = 0; i < toppingNames.size(); i++) {
        if (toppingNames.get(i).equalsIgnoreCase(toppingName)) {
            return i + 1;
        }
    }
    return -1;
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
    
    System.out.print("\nWould you like to reorder this? (y/n): ");
    String reorderChoice = scanner.nextLine().toLowerCase();
    if (reorderChoice.equals("y")) {
        guestReorderOrder(order);
    }
}

// reorder by guest
private static void guestReorderOrder(Order originalOrder) {
    System.out.println("\n=== Reorder as Guest ===");
    System.out.println("Original order date: " + originalOrder.getTimestamp());
    System.out.println("Customer: " + originalOrder.getCustomerName());
    System.out.println("Phone: " + originalOrder.getPhone());
    
    List<OrderItem> newItems = copyOrderItems(originalOrder);
    
    displayReorderSummary(newItems);
    
    System.out.print("\nConfirm reorder? (y/n): ");
    String confirm = scanner.nextLine().toLowerCase();
    if (!confirm.equals("y")) {
        System.out.println("Reorder cancelled.");
        return;
    }
    
    String name = originalOrder.getCustomerName();
    String phone = originalOrder.getPhone();
   
    Order newOrder = new Order(null, name, phone, newItems);
    String newOrderId = generateOrderId();
    newOrder.setOrderId(newOrderId);
    
    try {
        orderManager.placeOrder(newOrder);
        System.out.println("\nReorder placed successfully!");
        System.out.println("Your new Order ID: " + newOrderId);
        System.out.println("Please save this ID for future reference.");
    } catch (IOException e) {
        System.out.println("Error placing reorder: " + e.getMessage());
    }
}

// copy previous order item
private static List<OrderItem> copyOrderItems(Order originalOrder) {
    List<OrderItem> newItems = new ArrayList<>();
    
    for (OrderItem originalItem : originalOrder.getItems()) {

        Pizza recreatedPizza = recreatePizza(originalItem.getPizza());
        OrderItem newItem = new OrderItem(recreatedPizza, originalItem.getSize(), originalItem.getQuantity());
        newItems.add(newItem);
        
        System.out.println("Added: " + newItem.getDescription());
    }
    
    return newItems;
}

private static void displayReorderSummary(List<OrderItem> items) {
    System.out.println("\n=== Reorder Summary ===");
    double total = 0;
    for (OrderItem item : items) {
        double itemTotal = item.getItemTotal();
        total += itemTotal;
        System.out.printf("  %s - $%.2f%n", item.getDescription(), itemTotal);
    }
    System.out.printf("Total: $%.2f%n", total);
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