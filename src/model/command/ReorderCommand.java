package model.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.order.Order;
import model.order.OrderItem;

public class ReorderCommand implements Command {

    private List<Order> orders;
    private boolean isMember;
    private Scanner scanner;
    private ReorderCallback callback;

    public interface ReorderCallback {

        void onReorder(List<OrderItem> items, boolean isMember) throws IOException;
    }

    public ReorderCommand(List<Order> orders, boolean isMember, Scanner scanner, ReorderCallback callback) {
        this.orders = orders;
        this.isMember = isMember;
        this.scanner = scanner;
        this.callback = callback;
    }

    @Override
    public void execute() {
        if (orders.isEmpty()) {
            System.out.println("No orders to reorder!");
            return;
        }
        int choice = 0;
        while (true) {
            System.out.print("Enter order number to reorder (1-" + orders.size() + "): ");
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= orders.size()) {
                    break;
                } else {
                    System.out.println("Invalid order number. Please enter a number between 1 and " + orders.size() + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        Order orderToReorder = orders.get(choice - 1);
        List<OrderItem> newItems = new ArrayList<>();
        for (OrderItem item : orderToReorder.getItems()) {
            OrderItem newItem = new OrderItem(
                    item.getPizzaDescription(),
                    item.getPizzaPrice(),
                    item.getPizzaPoints(),
                    item.getSizeName(),
                    item.getSizeMultiplier(),
                    item.getQuantity()
            );
            newItems.add(newItem);
        }

        try {
            callback.onReorder(newItems, isMember);
        } catch (IOException e) {
            System.out.println("Error reordering: " + e.getMessage());
        }
    }

    @Override
    public void undo() {
        // Read-only
    }

    @Override
    public String getDescription() {
        return "Reorder Previous Order";
    }
}
