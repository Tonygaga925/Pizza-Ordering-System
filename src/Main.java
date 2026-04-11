import model.pizza.*;
import model.Member;
import model.command.CommandHistory;
import model.size.*;
import model.order.*;
import service.MenuLoader;
import service.OrderManager;
import service.RecommendationService;
import service.MemberManager;
import service.EmployeeManager;
import model.employee.Employee;
import model.command.*;
import java.io.IOException;
import java.util.*;

public class Main {
    private static MenuLoader menuLoader;
    private static OrderManager orderManager;
    private static MemberManager memberManager;
    private static EmployeeManager employeeManager;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        try {
            menuLoader = MenuLoader.getInstance();
            orderManager = OrderManager.getInstance();
            memberManager = MemberManager.getInstance();
            employeeManager = EmployeeManager.getInstance();

            // Set references
            orderManager.setMemberManager(memberManager);

            scanner = new Scanner(System.in);
            CommandFactory.initialize(memberManager, orderManager, employeeManager, menuLoader, scanner);

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
        System.out.println("2. Register");
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
                    CommandFactory.getInstance().createMemberLoginCommand().execute();
                    break;
                case 2:
                    CommandFactory.getInstance().createMemberRegisterCommand().execute();
                    break;
                case 3:
                    placeOrderWithBuilder(false, null);
                    break;
                case 4:
                    CommandFactory.getInstance().createSearchOrderCommand(false).execute();
                    break;
                case 5:
                    CommandFactory.getInstance().createEmployeeLoginCommand().execute();
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
                    CommandFactory.getInstance().createViewOrdersCommand(memberManager.getCurrentMember(), new ReorderCommand.ReorderCallback() {
                        @Override
                        public void onReorder(List<OrderItem> items, boolean isMember) throws IOException {
                            continueOrderFlow(items, isMember);
                        }
                    }).execute();
                    break;
                case 4:
                    CommandFactory.getInstance().createSearchOrderCommand(true).execute();
                    break;
                case 5:
                    memberManager.displayMemberInfo();
                    break;
                case 6:
                    CommandFactory.getInstance().createGetRecommendationCommand(memberManager.getCurrentMember(), new RecommendationService.MainCallback() {
                        @Override
                        public void startOrderWithRecommendedPizza(String pizzaName, boolean isMember) {
                            try {
                                placeOrderWithBuilder(isMember, pizzaName);
                            } catch (IOException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                        }
                    }).execute();
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



    private static void showEmployeeMenu() {
        Employee current = employeeManager.getCurrentEmployee();
        boolean running = true;
        while (running) {
            current.displayMenu();
            int choice = getIntInput();
            if (choice == -1) continue;

            int result = current.handleMenuChoice(choice, orderManager);
            if (result == 1) { // Logout
                employeeManager.logout();
                System.out.println("Employee logged out.");
                running = false;
            } else if (result == 2) { // Access Admin Panel
                showAdminMenu();
            }
            // result == 0 continues the loop
        }
    }

    private static void showAdminMenu(){
        Employee current = employeeManager.getCurrentEmployee();
        Scanner sc = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("\n=== Admin Menu (-1 to Return) ===");
            System.out.println("1. Create Staff");
            System.out.println("2. Edit Processing Order");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    CommandFactory.getInstance().createRegisterEmployeeCommand().execute();
                    break;
                case "2":
                    current.editProcessingOrder(orderManager);
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
        CommandFactory.getInstance().createPlaceOrderCommand(order, isMember).execute();
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
            Command command = CommandFactory.getInstance().createAddToppingCommand(toppingName, itemBuilder);
            ((CommandHistory)history).executeCommand(command);

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
        CommandFactory.getInstance().createPlaceOrderCommand(orderBuilder.build(), isMember).execute();
    }


}