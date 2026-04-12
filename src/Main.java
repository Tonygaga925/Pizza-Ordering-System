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
    
    public static void main(String[] args) {
        try {
            menuLoader = MenuLoader.getInstance();
            orderManager = OrderManager.getInstance();
            memberManager = MemberManager.getInstance();
            employeeManager = EmployeeManager.getInstance();

            orderManager.setMemberManager(memberManager);

            CommandFactory.initialize(memberManager, orderManager, employeeManager, menuLoader, view.InputView.getScanner());

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
        view.MainView.displayMainMenu();
        int choice = view.InputView.getIntInput();
        if (choice == -1) return;

        try {
            switch (choice) {
                case 1: CommandFactory.getInstance().createMemberLoginCommand().execute(); break;
                case 2: CommandFactory.getInstance().createMemberRegisterCommand().execute(); break;
                case 3: placeOrderWithBuilder(false, null); break;
                case 4: CommandFactory.getInstance().createSearchOrderCommand(false).execute(); break;
                case 5: CommandFactory.getInstance().createEmployeeLoginCommand().execute(); break;
                case 6: System.out.println("Goodbye!"); System.exit(0); break;
                default: System.out.println("Invalid choice! Please enter 1-6.");
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
        
        int choice = view.InputView.getIntInput("Choose: ");
        if (choice == -1) return;

        try {
            switch (choice) {
                case 1: showMenu(); break;
                case 2: placeOrderWithBuilder(true, null); break;
                case 3:
                    CommandFactory.getInstance().createViewOrdersCommand(memberManager.getCurrentMember(), new ReorderCommand.ReorderCallback() {
                        @Override
                        public void onReorder(List<OrderItem> items, boolean isMember) throws IOException {
                            continueOrderFlow(items, isMember);
                        }
                    }).execute();
                    break;
                case 4: CommandFactory.getInstance().createSearchOrderCommand(true).execute(); break;
                case 5: memberManager.displayMemberInfo(); break;
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
                default: System.out.println("Invalid choice! Please enter 1-7.");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showEmployeeMenu() {
        Employee current = employeeManager.getCurrentEmployee();
        boolean running = true;
        while (running) {
            if (current.accessAdminPanel()) {
                view.EmployeeView.displayManagerMenu(current.getName());
            } else {
                view.EmployeeView.displayStaffMenu(current.getName());
            }
            int choice = view.InputView.getIntInput();
            if (choice == -1) continue;

            int result = controller.EmployeeController.handleMenuChoice(current, choice, orderManager);
            if (result == 1) { // Logout
                employeeManager.logout();
                System.out.println("Employee logged out.");
                running = false;
            } else if (result == 2) { // Access Admin Panel
                showAdminMenu();
            }
        }
    }

    private static void showAdminMenu(){
        Employee current = employeeManager.getCurrentEmployee();
        boolean running = true;
        while (running) {
            System.out.println("\n=== Admin Menu (-1 to Return) ===");
            System.out.println("1. Create Staff");
            System.out.println("2. Edit Processing Order");
            String choice = view.InputView.getStringInput("Choose: ");
            switch (choice.trim()) {
                case "1": CommandFactory.getInstance().createRegisterEmployeeCommand().execute(); break;
                case "2": controller.EmployeeController.editProcessingOrder(current, orderManager); break;
                case "-1": running = false; break;
                default: System.out.println("Invalid option. Please try again.");
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

    private static void removePizza(List<OrderItem> items) {
        if (items.isEmpty()) {
            System.out.println("No pizzas to remove.");
            return;
        }

        view.MainView.displayCart(items);
        int index = view.InputView.getIntInput("To remove, enter pizza number: ") - 1;

        if (index < 0 || index >= items.size()) {
            System.out.println("Invalid pizza number!");
            return;
        }

        OrderItem removed = items.remove(index);
        System.out.printf("Removed: %s%n", removed.getDescription());

        if (!items.isEmpty()) {
            view.MainView.displayCart(items);
        } else {
            System.out.println("Cart is now empty.");
        }
    }

    private static void continueOrderFlow(List<OrderItem> items, boolean isMember) throws IOException {
        Order order;
        if (isMember) {
            Member member = memberManager.getCurrentMember();
            order = new Order(member.getId(), member.getName(), member.getPhone(), items);
        } else {
            String name = view.InputView.getStringInput("Enter your name: ");
            String phone;
            while (true) {
                phone = view.InputView.getStringInput("Enter your phone number: ");
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

    private static boolean addNewPizza(List<OrderItem> items, boolean isMember, String recommendedPizzaName) throws IOException {
        Pizza pizza = null;
        Size selectedSize = null;

        while (true) {
            if (recommendedPizzaName != null) {
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
                pizza = PizzaFactory.createPizza(index);
                System.out.println("\nOrdering recommended pizza: " + recommendedPizzaName);

            } else {
                System.out.println("\n--- Add New Pizza ---");
                view.MenuView.displayPizzaMenu(PizzaFactory.getPizzaNames(), PizzaFactory.getPizzaPrices(), PizzaFactory.getPizzaPoints(), items.isEmpty(), isMember);
                int pizzaIndex;
                while (true) {
                    pizzaIndex = view.InputView.getIntInput("Choose pizza number (-1 to back to main menu): ");
                    if (pizzaIndex == -1) return false;

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
                pizza = PizzaFactory.createPizza(pizzaIndex);
            }

            view.MenuView.displaySizeOptions(SizeFactory.getSizeNames(), SizeFactory.getSizeMultipliers(), pizza.getPrice());
            boolean goBack = false;
            while (true) {
                int sizeChoice = view.InputView.getIntInput("Choose size (-1 to go back to pizza menu): ");
                if (sizeChoice == -1) {
                    goBack = true;
                    recommendedPizzaName = null;
                    break;
                }
                try {
                    selectedSize = SizeFactory.getSize(sizeChoice);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid size choice! Please try again.");
                }
            }
            if (!goBack) break;
        }

        OrderItemBuilder itemBuilder = new OrderItemBuilder()
                .setPizza(pizza)
                .setSize(selectedSize.getName(), selectedSize.getMultiplier());

        CommandHistory history = new CommandHistory();
        boolean addingToppings = true;
        while (addingToppings) {
            view.MenuView.displayToppingMenu(PizzaFactory.getToppingNames(), PizzaFactory.getToppingPrices(), PizzaFactory.getToppingPoints(), isMember);
            displayPizzaInfo(itemBuilder, isMember);

            String prompt = history.canUndo() ? "\nCommands: u=Undo, r=Redo, 0=Finish, or enter topping number: " : "\nCommands: 0=Finish, or enter topping number: ";
            String input = view.InputView.getStringInput(prompt);

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

            List<String> toppingNames = PizzaFactory.getToppingNames();
            if (toppingChoice < 1 || toppingChoice > toppingNames.size()) {
                System.out.println("Invalid topping choice!");
                continue;
            }

            String toppingName = toppingNames.get(toppingChoice - 1);
            if (history.isToppingSelected(toppingName)) {
                System.out.println("You already selected " + toppingName + "! Each topping can only be added once.");
                continue;
            }

            Command command = CommandFactory.getInstance().createAddToppingCommand(toppingName, itemBuilder);
            ((CommandHistory)history).executeCommand(command);
            System.out.println("Added: " + toppingName);
        }

        System.out.println("\n--- Pizza Summary ---");
        displayPizzaInfo(itemBuilder, isMember);

        int quantity = inputQuantity(false);
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

        view.MainView.displayCart(items);
        int index = view.InputView.getIntInput("To modify quantity, enter pizza number: ") - 1;

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
        view.MainView.displayCart(items);
    }

    public static int inputQuantity(boolean isModify) {
        int quantity;
        int minimum = isModify ? 0 : 1;
        int maximum = 50;
        while (true) {
            quantity = view.InputView.getIntInput(
                    isModify ? "Enter new quantity (0 to remove): " : "Enter quantity (" + minimum + "-" + maximum + "): ");
            if (quantity >= minimum && quantity <= maximum) {
                break;
            } else {
                System.out.println("Invalid quantity! Please enter a number between " + minimum + " and " + maximum + ".");
            }
        }
        return quantity;
    }

    private static void placeOrderWithBuilder(boolean isMember, String recommendedPizzaName) throws IOException {
        OrderBuilder orderBuilder = new OrderBuilder();
        List<OrderItem> items = new ArrayList<>();
        boolean addedFirstPizza = addNewPizza(items, isMember, recommendedPizzaName);
        if (!addedFirstPizza && items.isEmpty()) return;

        recommendedPizzaName = null;
        boolean ordering = true;

        while (ordering) {
            System.out.println("\n=== Build Your Pizza ===");

            if (!items.isEmpty()) {
                view.MainView.displayCart(items);
                System.out.println("\nOptions (0. Back to Main Menu) :");
                System.out.println("1. Add new pizza");
                System.out.println("2. Modify existing pizza");
                System.out.println("3. Remove a pizza");
                System.out.println("4. Proceed to checkout");
                
                int option = view.InputView.getIntInput("Choose: ");
                switch (option) {
                    case 0:
                        if (!items.isEmpty()) {
                            String confirm = view.InputView.getStringInput("\nPizza is still in the cart. Confirm leave? (y/n): ").toLowerCase();
                            if (confirm.equals("y")) {
                                return;
                            } else if (confirm.equals("n")) {
                                continue;
                            } else {
                                System.out.println("Invalid option!");
                                continue;
                            }
                        } else {
                            return;
                        }
                    case 1: addNewPizza(items, isMember, recommendedPizzaName); break;
                    case 2: modifyPizzaQuantity(items); break;
                    case 3: removePizza(items); break;
                    case 4: ordering = false; break;
                    default: System.out.println("Invalid option!"); continue;
                }
            } else {
                System.out.println("Your cart is empty. Please add a pizza.");
                boolean addedPizza = addNewPizza(items, isMember, recommendedPizzaName);
                if (!addedPizza && items.isEmpty()) return;
            }
        }

        if (isMember) {
            Member member = memberManager.getCurrentMember();
            orderBuilder.setMemberId(member.getId()).setCustName(member.getName()).setPhone(member.getPhone());
        } else {
            String name = view.InputView.getStringInput("Enter your name: ");
            String phone;
            while (true) {
                phone = view.InputView.getStringInput("Enter your phone number: ");
                if (phone.length() != 8) {
                    System.out.println("Phone number must be exactly 8 digits.");
                } else if (!phone.matches("\\d+")) {
                    System.out.println("Phone number can only contain numbers.");
                } else {
                    break;
                }
            }
            orderBuilder.setCustName(name).setPhone(phone);
        }
        orderBuilder.addItem(items);

        if (items.isEmpty()) {
            System.out.println("No pizzas selected. Returning to menu.");
            return;
        }

        view.MainView.displayCart(items);
        CommandFactory.getInstance().createPlaceOrderCommand(orderBuilder.build(), isMember).execute();
    }
}