package model.employee;

import java.util.List;
import java.util.ArrayList; 
import java.util.Scanner;
import model.order.Order;
import model.order.OrderItem;
import model.order.OrderItemBuilder;
import model.pizza.Pizza;
import model.pizza.PizzaFactory;
import model.size.Size;
import model.size.SizeFactory;
import model.command.CommandHistory;
import service.OrderManager;

public abstract class Role {
    public abstract void displayMenu(String employeeName);
    public abstract int handleMenuChoice(int choice, OrderManager orderManager);
    
    public void handleOrder(OrderManager orderManager) {
        List<Order> activeOrders = new ArrayList<>();
        activeOrders.addAll(orderManager.getOrdersByStatus("Handling"));
        activeOrders.addAll(orderManager.getOrdersByStatus("Processing"));
        
        if (activeOrders.isEmpty()) {
            System.out.println("\n==================================");
            System.out.println("   No processing orders found.    ");
            System.out.println("==================================");
            return;
        }

        // Sort to show earliest orders first (oldest on top)
        activeOrders.sort((o1, o2) -> {
            if (o1.getTimestamp() == null && o2.getTimestamp() == null) return 0;
            if (o1.getTimestamp() == null) return 1;
            if (o2.getTimestamp() == null) return -1;
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        });
        System.out.println();
        System.out.println("There are currently " + activeOrders.size() + " orders in processing.");
        // Limit to only the 1 earliest order
        List<Order> earliestOrderList = activeOrders.subList(0, 1);

        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║            EARLIEST PROCESSING ORDER           ║");
        
        for (int i = 0; i < earliestOrderList.size(); i++) {
            Order order = earliestOrderList.get(i);
            
            // --- NEW CONNECTION LOGIC ---
            // 1. Create the title text
            String orderTitle = String.format(" Order %d: %s ", i + 1, order.getOrderId());
            
            // 2. Calculate remaining dashes to make the box exactly 50 characters wide
            int remainingDashes = 50 - 2 - orderTitle.length() - 1; // 50 total - "╟─" - title - "╢"
            
            // 3. Print the connecting border using ╟ and ╢
            System.out.print("╟─" + orderTitle);
            for (int d = 0; d < remainingDashes; d++) {
                System.out.print("─");
            }
            System.out.println("╢");
            // -----------------------------
            
            System.out.printf("│ Customer: %-36s │%n", order.getCustomerName());
            System.out.printf("│ Date:     %-36s │%n", order.getTimestamp());
            System.out.println("├────────────────────────────────────────────────┤");
            
            for (int j = 0; j < order.getItems().size(); j++) {
                model.order.OrderItem item = order.getItems().get(j);
                String desc = item.getDescription(); 
                
                String pizzaName = desc;
                String[] toppings = new String[0];

                // 1. Extract the size at the end (e.g., "(Medium)")
                String size = "";
                int sizeStart = desc.lastIndexOf("(");
                if (sizeStart != -1) {
                    size = desc.substring(sizeStart);
                    desc = desc.substring(0, sizeStart).trim();
                }

                // 2. Split by '+' to separate the pizza name and extra toppings
                if (desc.contains("+")) {
                    String[] parts = desc.split("\\+");
                    pizzaName = parts[0].trim() + " " + size;
                    
                    toppings = new String[parts.length - 1];
                    for (int k = 1; k < parts.length; k++) {
                        toppings[k - 1] = parts[k].trim();
                    }
                } else {
                    pizzaName = desc.trim() + " " + size;
                }

                // 3. Append the Quantity format (Check to prevent duplicates)
                String nameWithQuantity = pizzaName;
                if (!pizzaName.contains("| x")) {
                    nameWithQuantity += " | x " + item.getQuantity();
                }

                // 4. Print Item Details
                System.out.printf("│ %-46s │%n", "Item " + (j + 1) + ":");
                System.out.printf("│ %-46s │%n", nameWithQuantity);
                System.out.println("│ Topping:                                       │");
                
                if (toppings.length == 0) {
                    System.out.println("│ No topping                                     │");
                } else {
                    for (int k = 0; k < toppings.length; k++) {
                        String toppingLine = String.format("%d. %s", (k + 1), toppings[k]);
                        System.out.printf("│ %-46s │%n", toppingLine);
                    }
                }
                
                // Print divider or bottom border
                if (j < order.getItems().size() - 1) {
                    System.out.println("├────────────────────────────────────────────────┤");
                } else {
                    System.out.println("└────────────────────────────────────────────────┘");
                }
            }
        }
        Order currentOrder = earliestOrderList.get(0);
        model.command.CommandFactory.getInstance().createChangeOrderStatusCommand(currentOrder, "Handling").execute();
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nType 'y' to finish this order : ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("y")) {
            model.command.CommandFactory.getInstance().createChangeOrderStatusCommand(currentOrder, "Completed").execute();
        }
    }

    public void searchOrder(OrderManager orderManager) {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter the Order ID to search: ");
        String orderID = sc.nextLine().trim();
        Order order = orderManager.getOrderById(orderID);
        
        if (order == null) {
            System.out.println("\n==================================");
            System.out.println("         Order not found.         ");
            System.out.println("==================================");
            return;
        }

        model.command.Command searchCmd = model.command.CommandFactory.getInstance().createSearchOrderCommand(false);
        ((model.command.SearchOrderCommand)searchCmd).setOrderId(order.getOrderId());
        ((model.command.SearchOrderCommand)searchCmd).setStaffInvoke(true);
        searchCmd.execute();
    }

    public boolean cancelOrder(OrderManager orderManager) {
        java.util.List<Order> processingOrders = new java.util.ArrayList<>(orderManager.getOrdersByStatus("Processing"));
        if (processingOrders.isEmpty()) {
            System.out.println("No processing orders available to cancel.");
            return false;
        }

        // Sort by timestamp
        processingOrders.sort((o1, o2) -> {
            String t1 = o1.getTimestamp();
            String t2 = o2.getTimestamp();
            if (t1 == null) return (t2 == null) ? 0 : -1;
            return t1.compareTo(t2);
        });

        System.out.println("\n=== Cancel Processing Order ===");
        for (int i = 0; i < processingOrders.size(); i++) {
            Order order = processingOrders.get(i);
            System.out.printf("%d. ID: %s | Customer: %s | Time: %s%n", 
                i + 1, order.getOrderId(), order.getCustomerName(), order.getTimestamp());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("\nChoose order number to cancel (0 to go back): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number.");
            return false;
        }

        if (choice == 0) return false;
        if (choice < 1 || choice > processingOrders.size()) {
            System.out.println("Invalid order number!");
            return false;
        }

        Order order = processingOrders.get(choice - 1);
        
        System.out.print("Are you sure you want to cancel order " + order.getOrderId() + "? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y")) {
            model.command.CommandFactory.getInstance().createChangeOrderStatusCommand(order, "Cancelled").execute();
            return true;
        } else {
            System.out.println("Cancellation aborted.");
            return false;
        }
    }

    public void editProcessingOrder(OrderManager orderManager) {
        if (!canEditOrder()) {
            System.out.println("Access Denied: You do not have permission to edit orders.");
            return;
        }

        java.util.List<Order> processingOrders = orderManager.getOrdersByStatus("Processing");
        
        if (processingOrders.isEmpty()) {
            System.out.println("\n==================================");
            System.out.println("   No processing orders found.    ");
            System.out.println("==================================");
            return;
        }

        processingOrders.sort((o1, o2) -> {
            if (o1.getTimestamp() == null && o2.getTimestamp() == null) return 0;
            if (o1.getTimestamp() == null) return 1;
            if (o2.getTimestamp() == null) return -1;
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        });

        System.out.println("\n=== Edit Processing Order ===");
        for (int i = 0; i < processingOrders.size(); i++) {
            Order order = processingOrders.get(i);
            String timestamp = order.getTimestamp();
            if (timestamp != null && timestamp.length() > 19) {
                timestamp = timestamp.substring(0, 19);
            }
            System.out.printf("%d. Order ID: %s | Customer: %s | Date: %s | Total: $%.2f%n", 
                i + 1, order.getOrderId(), order.getCustomerName(), timestamp, order.getFinalTotal());
        }

        System.out.print("\nEnter the number of the order to edit (or 0 to cancel): ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        
        int choice;
        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input!");
            return;
        }

        if (choice == 0) {
            return;
        }

        if (choice < 1 || choice > processingOrders.size()) {
            System.out.println("Invalid order number!");
            return;
        }

        Order selectedOrder = processingOrders.get(choice - 1);
        boolean editing = true;
        while (editing) {
            System.out.println("\n=== Editing Order: " + selectedOrder.getOrderId() + " ===");
            selectedOrder.displayOrder(true);
            System.out.println("\n1. Edit Ordered Item (Remove/Update Quantity)");
            System.out.println("2. Add New Item");
            System.out.println("3. Save and Exit");
            System.out.println("0. Cancel / Exit without saving");
            System.out.print("Choose: ");
            
            String action = scanner.nextLine().trim();
            switch (action) {
                case "1":
                    editOrderedItem(selectedOrder, scanner);
                    break;
                case "2":
                    addNewItemToOrder(selectedOrder, scanner);
                    break;
                case "3":
                    try {
                        orderManager.placeOrder(selectedOrder);
                        System.out.println("Order updated successfully!");
                    } catch (java.io.IOException e) {
                        System.out.println("Error saving order: " + e.getMessage());
                    }
                    editing = false;
                    break;
                case "0":
                    System.out.println("Changes discarded.");
                    editing = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private void editOrderedItem(Order order, Scanner scanner) {
        List<OrderItem> items = order.getItems();
        System.out.println("\n--- Select Item to Edit ---");
        for (int i = 0; i < items.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, items.get(i).getDescription());
        }
        System.out.print("Enter item number (0 to cancel): ");
        int itemIdx = getIntInput(scanner) - 1;
        
        if (itemIdx == -1) return;
        if (itemIdx < 0 || itemIdx >= items.size()) {
            System.out.println("Invalid item number!");
            return;
        }

        OrderItem selectedItem = items.get(itemIdx);
        System.out.println("\nSelected Item: " + selectedItem.getDescription());
        System.out.println("1. Update Quantity");
        System.out.println("2. Remove Item");
        System.out.print("Choose: ");
        
        String action = scanner.nextLine().trim();
        if (action.equals("1")) {
            int newQty = getValidQuantity(scanner, "Enter new quantity (1-50): ");
            model.command.CommandFactory.getInstance().createUpdateQuantityCommand(order, selectedItem, newQty).execute();
        } else if (action.equals("2")) {
            model.command.CommandFactory.getInstance().createRemoveItemCommand(order, items, itemIdx).execute();
        } else {
            System.out.println("Invalid action.");
        }
    }

    private void addNewItemToOrder(Order order, Scanner scanner) {
        // Pizza selection
        PizzaFactory.displayPizzaMenu(false, true); // Assuming manager view (shows points)
        System.out.print("Choose pizza number (0 to cancel): ");
        int pizzaChoice = getIntInput(scanner);
        if (pizzaChoice == 0) return;
        if (pizzaChoice < 1 || pizzaChoice > PizzaFactory.getPizzaCount()) {
            System.out.println("Invalid pizza choice!");
            return;
        }
        Pizza pizza = PizzaFactory.createPizza(pizzaChoice);

        // Size selection
        SizeFactory.displaySizeOptions(pizza.getPrice());
        System.out.print("Choose size: ");
        int sizeChoice = getIntInput(scanner);
        Size selectedSize;
        try {
            selectedSize = SizeFactory.getSize(sizeChoice);
        } catch (Exception e) {
            System.out.println("Invalid size choice!");
            return;
        }

        OrderItemBuilder itemBuilder = new OrderItemBuilder()
                .setPizza(pizza)
                .setSize(selectedSize.getName(), selectedSize.getMultiplier());

        // Toppings selection
        CommandHistory history = new CommandHistory();
        boolean addingToppings = true;
        while (addingToppings) {
            PizzaFactory.displayToppingMenu(true);
            System.out.println("\nCurrent Pizza: " + itemBuilder.getPizzaDescription());
            System.out.printf("Current Price: $%.2f%n", itemBuilder.getTotalPrice());
            
            String prompt = "\nCommands: 0=Done, enter topping number";
            if (history.canUndo()) prompt += ", u=Undo";
            if (history.canRedo()) prompt += ", r=Redo";
            System.out.print(prompt + ": ");
            
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("0")) {
                addingToppings = false;
            } else if (input.equals("u")) {
                if (!history.undo()) System.out.println("Nothing to undo!");
            } else if (input.equals("r")) {
                if (!history.redo()) System.out.println("Nothing to redo!");
            } else {
                try {
                    int toppingChoice = Integer.parseInt(input);
                    if (toppingChoice >= 1 && toppingChoice <= PizzaFactory.getToppingCount()) {
                        String toppingName = PizzaFactory.getToppingNames().get(toppingChoice - 1);
                        if (itemBuilder.hasTopping(toppingName)) {
                            System.out.println("Topping already added!");
                        } else {
                        model.command.Command command = model.command.CommandFactory.getInstance().createAddToppingCommand(toppingName, itemBuilder);
                        history.executeCommand(command);
                        }
                    } else {
                        System.out.println("Invalid topping choice!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input!");
                }
            }
        }

        int quantity = getValidQuantity(scanner, "Enter quantity (1-50): ");

        itemBuilder.setQuantity(quantity);
        model.command.CommandFactory.getInstance().createAddItemCommand(order, order.getItems(), itemBuilder.build()).execute();
        System.out.println("Item added to order.");
    }

    private int getIntInput(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int getValidQuantity(Scanner scanner, String prompt) {
        int quantity;
        while (true) {
            System.out.print(prompt);
            quantity = getIntInput(scanner);
            if (quantity >= 1 && quantity <= 50) {
                return quantity;
            }
            System.out.println("Invalid quantity! Please enter a number between 1 and 50.");
        }
    }

    public abstract boolean accessAdminPanel();

    public abstract boolean canEditOrder();
}