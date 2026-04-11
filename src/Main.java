import model.pizza.*;
import model.Member;
import model.command.AddToppingCommand;
import model.command.CommandHistory;
import model.size.*;
import model.order.*;
import service.MenuLoader;
import service.OrderManager;
import service.RecommendationService;
import service.MemberManager;
import service.EmployeeManager;
import model.employee.Employee;

import java.io.IOException;
import java.util.*;

public class Main implements RecommendationService.MainCallback {
    private static MenuLoader menuLoader;
    private static OrderManager orderManager;
    private static MemberManager memberManager;
    private static EmployeeManager employeeManager;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        try {
            menuLoader = new MenuLoader();
            orderManager = new OrderManager("data/orders.json");
            memberManager = new MemberManager("data/members.json");
            employeeManager = new EmployeeManager("data/Staff.json");

            // Set references
            orderManager.setMemberManager(memberManager);

            scanner = new Scanner(System.in);

            while (true) {
                if (employeeManager.isLoggedIn()) {
                    showEmployeeMenu();
                } else if (memberManager.isLoggedIn()) {
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
        System.out.println("1. Member Login");
        System.out.println("2. Member Register");
        System.out.println("3. Continue as Guest");
        System.out.println("4. Search Order by ID");
        System.out.println("5. Employee Login");
        System.out.println("6. Exit");
        System.out.print("Choose: ");

        int choice = getIntInput();
        if (choice == -1)
            return;

        try {
            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    placeOrderWithBuilder(false, null);
                    break;
                case 4:
                    searchOrderById(false);
                    break;
                case 5:
                    employeeLogin();
                    break;
                case 6:
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Please enter 1-6.");
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
        System.out.println("6. Get Pizza Recommendations");
        System.out.println("7. Logout");
        System.out.print("Choose: ");

        int choice = getIntInput();
        if (choice == -1)
            return;

        try {
            switch (choice) {
                case 1:
                    showMenu();
                    break;
                case 2:
                    placeOrderWithBuilder(true, null);
                    break;
                case 3:
                    viewMyOrders();
                    break;
                case 4:
                    searchOrderById(true);
                    break;
                case 5:
                    memberManager.displayMemberInfo();
                    break;
                case 6:
                    getRecommendation();
                    break;
                case 7:
                    memberManager.logout();
                    System.out.println("Logged out successfully!");
                    break;
                default:
                    System.out.println("Invalid choice! Please enter 1-7.");
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
        System.out.println("\n--- Member Login (-1 to go back to previous step) ---");
        String username = "";
        String password = "";
        int step = 0;
        
        while (step < 2) {
            if (step == 0) {
                System.out.print("Username: ");
                username = scanner.nextLine();
                if (username.equals("-1")) return;
                step++;
            } else if (step == 1) {
                System.out.print("Password: ");
                password = scanner.nextLine();
                if (password.equals("-1")) {
                    step--;
                } else {
                    step++;
                }
            }
        }

        if (memberManager.login(username, password)) {
            System.out.println("Login successful");
        } else {
            System.out.println("Invalid username or password!");
        }
    }

    private static void registerEmployee() throws IOException{
        System.out.println("\n=== Create Staff Account [-1 to go back to previous step] ===");
        String username = "";
        String password = "";
        String name = "";
        int step = 0;
        while (step < 3) {
            if (step == 0) {
                System.out.print("Username: ");
                username = scanner.nextLine().trim();
                if (username.equals("-1")) return;
                
                if (username.isEmpty()) {
                    System.out.println("Username cannot be empty.");
                } else {
                    step++;
                }
            }
            else if (step == 1) {
                System.out.print("Password: ");
                password = scanner.nextLine().trim();
                if (password.equals("-1")) {
                    step--; // Go back to Username
                } else if (password.isEmpty()) {
                    System.out.println("Password cannot be empty.");
                } else {
                    step++;
                }
            }
            else if (step == 2) {
                System.out.print("Staff Name: ");
                name = scanner.nextLine().trim();
                if (name.equals("-1")) {
                    step--; // Go back to Password
                } else if (name.isEmpty()) {
                    System.out.println("Name cannot be empty.");
                } else {
                    step++;
                }
            }
        }
        if (employeeManager.registerStaff(username, password, name)) {
            System.out.println("Staff account for '" + name + "' created successfully.");
        } else {
            System.out.println("Registration failed: Username already exists!");
        }
    }

    private static void register() throws IOException {
        System.out.println("\n--- Register (-1 to go back to previous step) ---");
        String username = "";
        String password = "";
        String name = "";
        String phone = "";
        int step = 0;

        while (step < 4) {
            if (step == 0) {
                System.out.print("Username: ");
                username = scanner.nextLine();
                if (username.equals("-1")) return;
                step++;
            } else if (step == 1) {
                System.out.print("Password: ");
                password = scanner.nextLine();
                if (password.equals("-1")) {
                    step--;
                } else {
                    step++;
                }
            } else if (step == 2) {
                System.out.print("Your Name: ");
                name = scanner.nextLine();
                if (name.equals("-1")) {
                    step--;
                } else {
                    step++;
                }
            } else if (step == 3) {
                System.out.print("Phone Number: ");
                phone = scanner.nextLine();
                if (phone.equals("-1")) {
                    step--;
                } else if (phone.length() != 8) {
                    System.out.println("Phone number must be exactly 8 digits.");
                } else if (!phone.matches("\\d+")) {
                    System.out.println("Phone number can only contain numbers.");
                } else {
                    step++;
                }
            }
        }

        if (memberManager.register(username, password, name, phone)) {
            System.out.println("Registration successful. Please login.");
        } else {
            System.out.println("Username already exists!");
        }
    }

    private static void employeeLogin() {
        int step = 0;
        String username = "";
        String password = "";
        System.out.println("\n--- Employee Login (-1 to go back to previous step) ---");
        while(step<2){
            if(step == 0){
                System.out.print("Username: ");
                username = scanner.nextLine();
                if(username.equals("-1")) return;
                step++;
            }
            else if(step == 1){
                System.out.print("Password: ");
                password = scanner.nextLine();
                if(password.equals("-1")) 
                    step--;
                else 
                    step++;
            }
        }

        if (employeeManager.login(username, password)) {
            System.out.println("Employee login successful! Welcome, " + employeeManager.getCurrentEmployee().getName());
        } else {
            System.out.println("Invalid employee username or password!");
        }
    }

    private static void showEmployeeMenu() {
        Employee current = employeeManager.getCurrentEmployee();
        System.out.println("\n=== Employee Menu ===");
        System.out.println("Welcome, " + current.getName());
        System.out.println("1. View Order");
        System.out.println("2. Search Order");
        if (current.isManager()) {
            System.out.println("3. Access Admin Panel");
            System.out.println("4. Logout");
        } else {
            System.out.println("3. Logout");
        }
        
        System.out.print("Choose: ");

        int choice = getIntInput();
        if (choice == -1) return;

        if (current.isManager()) {
            switch (choice) {
                case 1:
                    current.handleOrder(orderManager);
                    break;
                case 2:
                    current.searchOrder(orderManager);
                    break;
                case 3:
                    if(current.accessAdminPanel()){
                        showAdminMenu();
                    }
                    break;
                case 4:
                    employeeManager.logout();
                    System.out.println("Employee logged out.");
                    break;
                default:
                    System.out.println("Invalid choice! Please enter 1-3.");
            }
        } else {
            switch (choice) {
                case 1:
                    current.handleOrder(orderManager);
                    break;
                case 2:
                    current.searchOrder(orderManager);
                    break;
                case 3:
                    employeeManager.logout();
                    System.out.println("Employee logged out.");
                    break;
                default:
                    System.out.println("Invalid choice! Please enter 1-2.");
            }
        }
    }

    private static void showAdminMenu(){
        Scanner sc = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("\n=== Admin Menu (-1 to Return) ===");
            System.out.println("1. Create Staff");
            System.out.println("2. Manage Pizzas & Toppings");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    try{
                        registerEmployee();
                    }catch (IOException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case "2":

                    break;
                case "-1":
                    running = false;
                    break;
                default:    
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n--- Menu ---");
        List<String> pizzaNames = menuLoader.getPizzaNames();
        List<Double> pizzaPrices = menuLoader.getPizzaPrices();
        List<Integer> pizzaPoints = menuLoader.getPizzaPoints();

        for (int i = 0; i < pizzaNames.size(); i++) {
            System.out.printf("%d. %s - $%.2f (%d points)%n",
                    i + 1, pizzaNames.get(i), pizzaPrices.get(i), pizzaPoints.get(i));
        }
        System.out.println("\nNote: You can add extra toppings after selecting your pizza.");
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static void displayCart(List<OrderItem> items) {
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

    private static void removePizza(List<OrderItem> items) {
        if (items.isEmpty()) {
            System.out.println("No pizzas to remove.");
            return;
        }

        displayCart(items);
        System.out.print("To remove, enter pizza number: ");
        int index = getIntInput() - 1;

        if (index < 0 || index >= items.size()) {
            System.out.println("Invalid pizza number!");
            return;
        }

        OrderItem removed = items.remove(index);
        System.out.printf("Removed: %s%n", removed.getDescription());

        if (!items.isEmpty()) {
            displayCart(items);
        } else {
            System.out.println("Cart is now empty.");
        }
    }

    // for member use
    private static void viewMyOrders() {
        Member member = memberManager.getCurrentMember();

        // Get orders from file
        List<Order> orders = orderManager.getOrdersByMemberIdFromFile(member.getId());

        if (orders.isEmpty()) {
            System.out.println("You have no orders yet.");
            return;
        }

        // Show only the 9 most recent orders
        int maxDisplay = Math.min(orders.size(), 9);
        List<Order> recentOrders = orders.subList(0, maxDisplay);

        System.out.println("\n=== Your Recent Orders (Latest order on top, max 9 shown) ===\n");
        for (int i = 0; i < recentOrders.size(); i++) {
            Order order = recentOrders.get(i);
            String timestamp = order.getTimestamp();
            if (timestamp != null && timestamp.length() > 19) {
                timestamp = timestamp.substring(0, 19);
            }

            System.out.printf("%d. %s | Total: $%.2f | %s%n", i + 1, order.getOrderId(), order.getFinalTotal(),
                    timestamp);
            System.out.println("    Item(s):");

            List<OrderItem> items = order.getItems();
            for (OrderItem item : items) {
                System.out.printf("\t%s - $%.2f each = $%.2f%n",
                        item.getDescription(),
                        item.getPizzaPrice(),
                        item.getItemTotal());
            }
            if (order.getDiscountApplied() > 0) {
                System.out.printf("\n\tOriginal Total: $%.2f, Discount: -$%.2f%n", order.getOriginalTotal(),
                        order.getDiscountApplied());
            } else {
                System.out.printf("\n\tTotal: $%.2f%n", order.getOriginalTotal());
            }
            System.out.println();
        }

        System.out.println("[m] Back to main menu");
        System.out.println("[r] Reorder a previous order");
        System.out.print("Choose: ");

        String input = scanner.nextLine().toLowerCase();

        if (input.equals("m")) {
            return;
        } else if (input.equals("r")) {
            reorderPreviousOrder(recentOrders, true);
        } else {
            System.out.println("Invalid option!");
        }
    }

    private static void reorderPreviousOrder(List<Order> orders, boolean isMember) {
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
            System.out.printf("%d. %s | $%.2f | %s%n", i + 1, order.getOrderId(), order.getFinalTotal(), timestamp);
        }

        System.out.print("\nEnter number to reorder "
                + (orders.size() == 1 ? "(e.g. 1) " : "(1-" + orders.size() + "), ") + "or input 0 to cancel: ");
        int choice = getIntInput();

        if (choice == 0) {
            System.out.println("Reorder cancelled.");
            return;
        }

        if (choice < 1 || choice > orders.size()) {
            System.out.println("Invalid choice!");
            return;
        }

        Order selectedOrder = orders.get(choice - 1);
        reorderOrder(selectedOrder, isMember);
    }

    private static void reorderOrder(Order originalOrder, boolean isMember) {
        System.out.println("\n=== Reorder Previous Order ===");

        List<OrderItem> newItems = new ArrayList<>();

        // Copy the original items directly
        for (OrderItem originalItem : originalOrder.getItems()) {
            OrderItem newItem = new OrderItem(
                    originalItem.getPizzaDescription(),
                    originalItem.getPizzaPrice(),
                    originalItem.getPizzaPoints(),
                    originalItem.getSizeName(),
                    originalItem.getSizeMultiplier(),
                    originalItem.getQuantity());
            newItems.add(newItem);
            System.out.println("\nCurrent Item(s): " + newItem.getDescription());

            if (isMember) {
                Member member = memberManager.getCurrentMember();
                if (member.getState().getDiscount() > 0) {
                    System.out.printf("Total: $%.2f%n", newItem.getItemTotal());
                    Double discountAmount = newItem.getItemTotal() * member.getState().getDiscount();
                    System.out.printf("Discount: -$%.2f%n", discountAmount);
                    System.out.printf("Final Total: $%.2f%n", newItem.getItemTotal() - discountAmount);
                } else {
                    System.out.printf("Total: $%.2f%n", newItem.getItemTotal());
                }
                System.out.println("Points: " + newItem.getItemPoints());
            } else {
                // for guest users
                System.out.printf("Total: $%.2f%n", newItem.getItemTotal());
            }
        }
        // Use existing continueOrderFlow to handle checkout
        try {
            continueOrderFlow(newItems, isMember);
        } catch (IOException e) {
            System.out.println("Error processing order: " + e.getMessage());
        }
    }

    private static void searchOrderById(boolean isMember) {
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine();

        Order order = orderManager.getOrderById(orderId);

        if (order == null) {
            System.out.println("Order not found!");
            return;
        }
        String notAuthorizedMsg = "You are not authorized to view this order.";

        if (isMember) {
            Member member = memberManager.getCurrentMember();
            // Check if the order belongs to the current member
            if (order.getMemberId() != null && !order.getMemberId().equals(member.getId())) {
                System.out.println(notAuthorizedMsg);
                return;
            }
            // Check if the order is a guest order
            else if (order.getMemberId() == null) {
                System.out.println(notAuthorizedMsg);
                return;
            }
        } else {
            // Guest user cannot check members' orders
            if (order.getMemberId() != null) {
                System.out.println(notAuthorizedMsg);
                return;
            }
        }
        orderManager.displayOrder(order, isMember);
    }

    private static String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }

    // for member get recommendation on pizza
    private static void getRecommendation() {
        Member current = memberManager.getCurrentMember();
        RecommendationService recommendationService = new RecommendationService(orderManager, menuLoader, scanner);
        recommendationService.setCallback(new RecommendationService.MainCallback() {
            @Override
            public void startOrderWithRecommendedPizza(String pizzaName, boolean isMember) {
                try {
                    placeOrderWithBuilder(isMember, pizzaName);
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        });
        recommendationService.getRecommendation(current, true);
    }

    @Override
    public void startOrderWithRecommendedPizza(String pizzaName, boolean isMember) {
        try {
            placeOrderWithBuilder(isMember, pizzaName);
        } catch (IOException e) {
            System.out.println("Error starting order: " + e.getMessage());
        }
    }

    // continue order flow (from cart to checkout)
private static void continueOrderFlow(List<OrderItem> items, boolean isMember) throws IOException {

        Order order;
        if (isMember) {
            Member member = memberManager.getCurrentMember();
            order = new Order(member.getId(), member.getName(), member.getPhone(), items);
        } else {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            String phone;
            while (true) {
                System.out.print("Enter your phone number: ");
                phone = scanner.nextLine();
                if (phone.length() != 8) {
                    System.out.println("Phone number must be exactly 8 digits.");
                } else if (!phone.matches("\\d+")) {
                    System.out.println("Phone number can only contain numbers.");
                } else {
                    break;
                }
            }
            order = new Order(null, name, phone, items);
        }
        checkOutOrder(order, isMember);
    }

    public static void displayPizzaInfo(OrderItemBuilder itemBuilder, boolean isMember) {
        
        System.out.println("\nPizza: " + itemBuilder.getPizzaDescription() + " (" + itemBuilder.getSizeName() + ") ");
        System.out.printf("Price: $%.2f%n", itemBuilder.getTotalPrice());
        if (isMember) {
            System.out.printf("Points: %d%n", itemBuilder.getTotalPoints());
        }
    }

    private static boolean addNewPizza(List<OrderItem> items, boolean isMember, String recommendedPizzaName)
            throws IOException {

        Pizza pizza = null;
        Size selectedSize = null;

        while (true) {
            if (recommendedPizzaName != null) {
                // Use the recommended pizza if not null
                List<String> pizzaNames = PizzaFactory.getPizzaNames();
                int index = -1;
                for (int i = 0; i < pizzaNames.size(); i++) {
                    if (pizzaNames.get(i).equalsIgnoreCase(recommendedPizzaName)) {
                        index = i + 1;
                        break;
                    }
                }

                if (index == -1) {
                    System.out.println("Error: Recommended pizza not found!");
                    return false;
                }
                // Create pizza using PizzaFactory
                pizza = PizzaFactory.createPizza(index);
                System.out.println("\nOrdering recommended pizza: " + recommendedPizzaName);

            } else {
                // For creating pizza without recommendation
                System.out.println("\n--- Add New Pizza ---");

                // Show pizza menu using PizzaFactory
                PizzaFactory.displayPizzaMenu(items.isEmpty(),isMember);
                int pizzaIndex;
                while (true) {
                    pizzaIndex = getIntInput("Choose pizza number (-1 to back to main menu): ");
                    if (pizzaIndex == -1) {
                        return false;
                    }

                    if (pizzaIndex == 0) {
                        if (items.isEmpty()) {
                            System.out.println("No pizza in the cart! Please add a pizza first.");
                            continue;
                        } else {
                            return false;
                        }
                    }

                    if (pizzaIndex >= 1 && pizzaIndex <= PizzaFactory.getPizzaCount()) {
                        break;
                    }
                    System.out.println("Invalid pizza choice! Please try again.");
                }

                // Create pizza using PizzaFactory
                pizza = PizzaFactory.createPizza(pizzaIndex);
            }

            // Choose size
            SizeFactory.displaySizeOptions(pizza.getPrice());
            boolean goBack = false;
            while (true) {
                int sizeChoice = getIntInput("Choose size (-1 to go back to pizza menu): ");
                if (sizeChoice == -1) {
                    goBack = true;
                    recommendedPizzaName = null; // Clear recommendation if going back
                    break;
                }
                try {
                    selectedSize = SizeFactory.getSize(sizeChoice);
                    break; // Valid size selected
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid size choice! Please try again.");
                }
            }

            if (!goBack) {
                break;
            }
        }

        // Create OrderItemBuilder before the topping loop
        OrderItemBuilder itemBuilder = new OrderItemBuilder()
                .setPizza(pizza)
                .setSize(selectedSize.getName(), selectedSize.getMultiplier());

        // Initialize command history for this pizza
        CommandHistory history = new CommandHistory();

        // Add toppings with Undo/Redo support
        boolean addingToppings = true;
        while (addingToppings) {
            PizzaFactory.displayToppingMenu(isMember);
            displayPizzaInfo(itemBuilder, isMember);

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
                    displayPizzaInfo(itemBuilder, isMember);

                } else {
                    System.out.println("Nothing to undo!");
                }
                continue;
            }

            if (input.equalsIgnoreCase("r") || input.equalsIgnoreCase("redo")) {
                if (history.redo()) {
                    System.out.println("Redo successful!");
                    displayPizzaInfo(itemBuilder, isMember);

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

            // Create and execute command, add topping by command
            // Perform undo / redo , add / remove topping action in this command
            AddToppingCommand command = new AddToppingCommand(toppingName, itemBuilder);
            history.executeCommand(command);

            System.out.println("Added: " + toppingName);
        }

        // Display final pizza
        System.out.println("\n--- Pizza Summary ---");
        displayPizzaInfo(itemBuilder, isMember);

        // Choose quantity
        int quantity = inputQuantity(false);

        // Set quantity and add to list
        itemBuilder.setQuantity(quantity);
        OrderItem orderitem = itemBuilder.build();
        items.add(orderitem);

        System.out.println("Pizza added to cart!");
        return true;
    }

    private static void modifyPizzaQuantity(List<OrderItem> items) {
        if (items.isEmpty()) {
            System.out.println("No pizzas to modify.");
            return;
        }

        displayCart(items);
        System.out.print("To modify quantity, enter pizza number: ");
        int index = getIntInput() - 1;

        if (index < 0 || index >= items.size()) {
            System.out.println("Invalid pizza number!");
            return;
        }

        OrderItem item = items.get(index);
        System.out.printf("Current quantity: %d%n", item.getQuantity());

        int newQuantity = inputQuantity(true);

        if (newQuantity <= 0) {
            items.remove(index);
            System.out.println("Pizza removed from cart.");
        } else {
            item.setQuantity(newQuantity);
            System.out.println("Quantity updated!");
        }
        displayCart(items);
    }

    public static int inputQuantity(boolean isModify) {
        int quantity;
        // For modifying, allow 0 (remove item), for adding, start from 1
        int minimum = isModify ? 0 : 1;
        int maximum = 50;
        while (true) {
            quantity = getIntInput(
                    isModify ? "Enter new quantity (0 to remove): "
                            : "Enter quantity (" + minimum + "-" + maximum + "): ");

            if (quantity >= minimum && quantity <= maximum) {
                break;
            } else {
                System.out.println(
                        "Invalid quantity! Please enter a number between " + minimum + " and " + maximum + ".");
            }
        }
        return quantity;
    }

    private static void placeOrderWithBuilder(boolean isMember, String recommendedPizzaName) throws IOException {
        OrderBuilder orderBuilder = new OrderBuilder();

        List<OrderItem> items = new ArrayList<>();
        boolean addedFirstPizza = addNewPizza(items, isMember, recommendedPizzaName);
        if (!addedFirstPizza && items.isEmpty()) {
            return;
        }

        recommendedPizzaName = null; // null after using recommendation
        boolean ordering = true;

        while (ordering) {
            System.out.println("\n=== Build Your Pizza ===");

            if (!items.isEmpty()) {
                displayCart(items);
                System.out.println("\nOptions (0. Back to Main Menu) :");
                System.out.println("1. Add new pizza");
                System.out.println("2. Modify existing pizza");
                System.out.println("3. Remove a pizza");
                System.out.println("4. Proceed to checkout");
                System.out.print("Choose: ");

                int option = getIntInput();
                switch (option) {
                    case 0:
                    if (!items.isEmpty()) {
                    System.out.print("\nPizza is still in the cart. Confirm leave? (y/n): ");
                    String confirm = scanner.nextLine().toLowerCase();
                    if (confirm.equals("y")) {
                        return;
                    } else if (confirm.equals("n")){
                        continue;
                    }else{
                        System.out.println("Invalid option!");
                        continue;
                    }
                    }
                    else{
                        return; // leave directly if no item
                    }
                    case 1:
                        addNewPizza(items, isMember, recommendedPizzaName);
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
                boolean addedPizza = addNewPizza(items, isMember, recommendedPizzaName);
                if (!addedPizza && items.isEmpty()) {
                    return;
                }
            }
        }

        if (isMember) {
            Member member = memberManager.getCurrentMember();
            orderBuilder.setMemberId(member.getId())
                    .setCustName(member.getName())
                    .setPhone(member.getPhone());
        } else { // for guests
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            String phone;
            while (true) {
                System.out.print("Enter your phone number: ");
                phone = scanner.nextLine();
                if (phone.length() != 8) {
                    System.out.println("Phone number must be exactly 8 digits.");
                } else if (!phone.matches("\\d+")) {
                    System.out.println("Phone number can only contain numbers.");
                } else {
                    break;
                }
            }
            orderBuilder.setCustName(name)
                    .setPhone(phone);
        }
        orderBuilder.addItem(items); // add all items to order builder

        if (items.isEmpty()) {
            System.out.println("No pizzas selected. Returning to menu.");
            return;
        }

        displayCart(items);
        checkOutOrder(orderBuilder.build(), isMember);
    }

    public static void checkOutOrder(Order order, boolean isMember) throws IOException {

        String orderId = generateOrderId();
        order.setOrderId(orderId);
        Member member = isMember ? memberManager.getCurrentMember() : null;

        if (isMember) {
            if (member.getDiscount() > 0) {
                order.applyDiscount(member.getDiscount());
            }
        } else { // for guest, no points earned
            order.setTotalPoints(0);
        }

        System.out.print("\nConfirm order? (y/n): ");
        String confirm = scanner.nextLine().toLowerCase();
        if (confirm.equals("y")) {
            orderManager.placeOrder(order);
            System.out.println("\nOrder placed successfully!");
            System.out.println("Your Order ID: " + orderId);
            // for updating member points and change member state if over the vip threshold
            if (isMember) {
                memberManager.updateMemberPoints(member.getId(), order.getTotalPoints());
            }
            order.displayOrder(isMember);
        } else {
            System.out.println("Order cancelled.");
        }
    }
}