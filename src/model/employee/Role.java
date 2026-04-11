package model.employee;

import service.OrderManager;
import model.order.Order;
import java.util.List;
import java.util.Scanner;

public abstract class Role {
    public void handleOrder(OrderManager orderManager) {
        List<Order> processingOrders = orderManager.getOrdersByStatus("Processing");
        
        if (processingOrders.isEmpty()) {
            System.out.println("\n==================================");
            System.out.println("   No processing orders found.    ");
            System.out.println("==================================");
            return;
        }

        // Sort to show earliest orders first (oldest on top)
        processingOrders.sort((o1, o2) -> {
            if (o1.getTimestamp() == null && o2.getTimestamp() == null) return 0;
            if (o1.getTimestamp() == null) return 1;
            if (o2.getTimestamp() == null) return -1;
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        });
        System.out.println();
        System.out.println("There are currently " + processingOrders.size() + " orders in processing.");
        // Limit to only the 1 earliest order
        List<Order> earliestOrderList = processingOrders.subList(0, 1);

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
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nType 'y' to finish this order : ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("y")) {
            try {
                orderManager.changeOrderStatus(currentOrder, "Completed");
                System.out.println("Order " + currentOrder.getOrderId() + " has been marked as Completed.");
            } catch (java.io.IOException e) {
                System.out.println("An unexpected error occurred. Please try again later.");
            }
        }
    }

    public void searchOrder(OrderManager orderManager) {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter the Order ID to search: ");
        String orderID = sc.nextLine().trim();
        Order order = orderManager.getOrderById(orderID);
        
        // Handle case where order is not found
        if (order == null) {
            System.out.println("\n==================================");
            System.out.println("         Order not found.         ");
            System.out.println("==================================");
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║                  ORDER DETAILS                 ║");
        
        // --- CONNECTION LOGIC ---
        // 1. Create the title text (without the index 'i' since it's a single search)
        String orderTitle = String.format(" Order: %s ", order.getOrderId());
        
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
        System.out.printf("│ Status:   %-36s │%n", order.getStatus()); // Helpful for search results
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

    public abstract boolean accessAdminPanel();
    
}