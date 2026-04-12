package controller;

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
import model.employee.Employee;

public class EmployeeController {

    public static int handleMenuChoice(Employee employee, int choice, OrderManager orderManager) {
        boolean isManager = employee.accessAdminPanel();
        
        switch (choice) {
            case 1:
                handleOrder(employee, orderManager);
                return 0;
            case 2:
                searchOrder(employee, orderManager);
                return 0;
            case 3:
                cancelOrder(employee, orderManager);
                return 0;
            case 4:
                if (isManager) {
                    handleCouponManagement();
                    return 0;
                }
                return 1; // Staff Logout
            case 5:
                if (isManager) return 2; // Manager Access Admin Panel
                return 0;
            case 6:
                if (isManager) return 1; // Manager Logout
                return 0;
            default:
                view.MainView.displayMessage("Invalid choice!");
                return 0;
        }
    }

    private static void handleCouponManagement() {
        try {
            model.command.CommandFactory.getInstance().createManageCouponsCommand().execute();
        } catch (Exception e) {
            System.out.println("Error accessing coupon management: " + e.getMessage());
        }
    }

    public static void handleOrder(Employee employee, OrderManager orderManager) {
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

            String orderTitle = String.format(" Order %d: %s ", i + 1, order.getOrderId());
            int remainingDashes = 50 - 2 - orderTitle.length() - 1; 

            System.out.print("╟─" + orderTitle);
            for (int d = 0; d < remainingDashes; d++) {
                System.out.print("─");
            }
            System.out.println("╢");

            System.out.printf("│ Customer: %-36s │%n", order.getCustomerName());
            System.out.printf("│ Date:     %-36s │%n", order.getTimestamp());
            System.out.println("├────────────────────────────────────────────────┤");

            for (int j = 0; j < order.getItems().size(); j++) {
                model.order.OrderItem item = order.getItems().get(j);
                String desc = item.getDescription();

                String pizzaName = desc;
                String[] toppings = new String[0];

                String size = "";
                int sizeStart = desc.lastIndexOf("(");
                if (sizeStart != -1) {
                    size = desc.substring(sizeStart);
                    desc = desc.substring(0, sizeStart).trim();
                }

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

                String nameWithQuantity = pizzaName;
                if (!pizzaName.contains("| x")) {
                    nameWithQuantity += " | x " + item.getQuantity();
                }

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

                if (j < order.getItems().size() - 1) {
                    System.out.println("├────────────────────────────────────────────────┤");
                } else {
                    System.out.println("└────────────────────────────────────────────────┘");
                }
            }
        }
        Order currentOrder = earliestOrderList.get(0);
        
        try {
            model.command.CommandFactory.getInstance().createChangeOrderStatusCommand(currentOrder, "Handling").execute();
            String confirm = view.InputView.getStringInput("\nType 'y' to finish this order : ");
            if (confirm.equalsIgnoreCase("y")) {
                model.command.CommandFactory.getInstance().createChangeOrderStatusCommand(currentOrder, "Completed").execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void searchOrder(Employee employee, OrderManager orderManager) {
        String orderID = view.InputView.getStringInput("\nEnter the Order ID to search: ");
        Order order = orderManager.getOrderById(orderID);

        if (order == null) {
            System.out.println("\n==================================");
            System.out.println("         Order not found.         ");
            System.out.println("==================================");
            return;
        }

        try {
            model.command.Command searchCmd = model.command.CommandFactory.getInstance().createSearchOrderCommand(false);
            ((model.command.SearchOrderCommand) searchCmd).setOrderId(order.getOrderId());
            ((model.command.SearchOrderCommand) searchCmd).setStaffInvoke(true);
            searchCmd.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean cancelOrder(Employee employee, OrderManager orderManager) {
        java.util.List<Order> processingOrders = new java.util.ArrayList<>(orderManager.getOrdersByStatus("Processing"));
        if (processingOrders.isEmpty()) {
            System.out.println("No processing orders available to cancel.");
            return false;
        }

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

        int choice = view.InputView.getIntInput("\nChoose order number to cancel (0 to go back): ");
        if (choice == 0) return false;
        if (choice < 1 || choice > processingOrders.size()) {
            System.out.println("Invalid order number!");
            return false;
        }

        Order order = processingOrders.get(choice - 1);
        String confirm = view.InputView.getStringInput("Are you sure you want to cancel order " + order.getOrderId() + "? (y/n): ");

        if (confirm.equalsIgnoreCase("y")) {
            try {
                model.command.CommandFactory.getInstance().createChangeOrderStatusCommand(order, "Cancelled").execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else {
            System.out.println("Cancellation aborted.");
            return false;
        }
    }

    public static void editProcessingOrder(Employee employee, OrderManager orderManager) {
        if (!employee.getRole().canEditOrder()) {
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

        int choice = view.InputView.getIntInput("\nEnter the number of the order to edit (or 0 to cancel): ");
        if (choice == 0) return;
        if (choice < 1 || choice > processingOrders.size()) {
            System.out.println("Invalid order number!");
            return;
        }

        Order selectedOrder = processingOrders.get(choice - 1);
        boolean editing = true;
        while (editing) {
            System.out.println("\n=== Editing Order: " + selectedOrder.getOrderId() + " ===");
            view.OrderView.displayOrder(selectedOrder, true);
            System.out.println("\n1. Edit Ordered Item (Remove/Update Quantity)");
            System.out.println("2. Add New Item");
            System.out.println("3. Save and Exit");
            System.out.println("0. Cancel / Exit without saving");
            
            String action = view.InputView.getStringInput("Choose: ");
            switch (action) {
                case "1":
                    editOrderedItem(selectedOrder, orderManager);
                    break;
                case "2":
                    addNewItemToOrder(selectedOrder, orderManager);
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

    private static void editOrderedItem(Order order, OrderManager orderManager) {
        List<OrderItem> items = order.getItems();
        System.out.println("\n--- Select Item to Edit ---");
        for (int i = 0; i < items.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, items.get(i).getDescription());
        }
        int itemIdx = view.InputView.getIntInput("Enter item number (0 to cancel): ") - 1;

        if (itemIdx == -1) return;
        if (itemIdx < 0 || itemIdx >= items.size()) {
            System.out.println("Invalid item number!");
            return;
        }

        OrderItem selectedItem = items.get(itemIdx);
        System.out.println("\nSelected Item: " + selectedItem.getDescription());
        System.out.println("1. Update Quantity");
        System.out.println("2. Remove Item");
        
        String action = view.InputView.getStringInput("Choose: ");
        try {
            if (action.equals("1")) {
                int newQty = getValidQuantity("Enter new quantity (1-50): ");
                model.command.CommandFactory.getInstance().createUpdateQuantityCommand(order, selectedItem, newQty).execute();
            } else if (action.equals("2")) {
                model.command.CommandFactory.getInstance().createRemoveItemCommand(order, items, itemIdx).execute();
            } else {
                System.out.println("Invalid action.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addNewItemToOrder(Order order, OrderManager orderManager) {
        view.MenuView.displayPizzaMenu(PizzaFactory.getPizzaNames(), PizzaFactory.getPizzaPrices(), PizzaFactory.getPizzaPoints(), false, true);
        int pizzaChoice = view.InputView.getIntInput("Choose pizza number (0 to cancel): ");
        if (pizzaChoice == 0) return;
        if (pizzaChoice < 1 || pizzaChoice > PizzaFactory.getPizzaCount()) {
            System.out.println("Invalid pizza choice!");
            return;
        }
        Pizza pizza = PizzaFactory.createPizza(pizzaChoice);

        view.MenuView.displaySizeOptions(SizeFactory.getSizeNames(), SizeFactory.getSizeMultipliers(), pizza.getPrice());
        int sizeChoice = view.InputView.getIntInput("Choose size: ");
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

        CommandHistory history = new CommandHistory();
        boolean addingToppings = true;
        while (addingToppings) {
            view.MenuView.displayToppingMenu(PizzaFactory.getToppingNames(), PizzaFactory.getToppingPrices(), PizzaFactory.getToppingPoints(), true);
            System.out.println("\nCurrent Pizza: " + itemBuilder.getPizzaDescription());
            System.out.printf("Current Price: $%.2f%n", itemBuilder.getTotalPrice());

            String prompt = "\nCommands: 0=Done, enter topping number";
            if (history.canUndo()) prompt += ", u=Undo";
            if (history.canRedo()) prompt += ", r=Redo";
            
            String input = view.InputView.getStringInput(prompt + ": ").toLowerCase();
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
                } catch (Exception e) {
                    System.out.println("Invalid input!");
                }
            }
        }

        int quantity = getValidQuantity("Enter quantity (1-50): ");
        itemBuilder.setQuantity(quantity);
        
        try {
            model.command.CommandFactory.getInstance().createAddItemCommand(order, order.getItems(), itemBuilder.build()).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Item added to order.");
    }

    private static int getValidQuantity(String prompt) {
        int quantity;
        while (true) {
            quantity = view.InputView.getIntInput(prompt);
            if (quantity >= 1 && quantity <= 50) {
                return quantity;
            }
            System.out.println("Invalid quantity! Please enter a number between 1 and 50.");
        }
    }
}
