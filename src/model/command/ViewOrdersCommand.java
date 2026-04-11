package model.command;

import java.util.List;
import java.util.Scanner;
import model.Member;
import model.order.Order;
import model.order.OrderItem;
import service.OrderManager;

public class ViewOrdersCommand implements Command {
    private Member member;
    private OrderManager orderManager;
    private Scanner scanner;
    private ReorderCommand.ReorderCallback reorderCallback;

    public ViewOrdersCommand(Member member, OrderManager orderManager, Scanner scanner, ReorderCommand.ReorderCallback reorderCallback) {
        this.member = member;
        this.orderManager = orderManager;
        this.scanner = scanner;
        this.reorderCallback = reorderCallback;
    }

    @Override
    public void execute() {
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
        while(true){
        System.out.println("[-1] Back to main menu");
        System.out.println("[0] Reorder a previous order");
        System.out.print("Choose: ");

        String input = scanner.nextLine().toLowerCase();

        if (input.equals("-1")) {
            return;
        } else if (input.equals("0")) {
            new model.command.ReorderCommand(recentOrders, true, scanner, reorderCallback).execute();
            break;
        } else {
            System.out.println("Invalid option!\n");
        }
        }
    }

    @Override
    public void undo() {
        // Read-only
    }

    @Override
    public String getDescription() {
        return "View Member Orders";
    }
}
