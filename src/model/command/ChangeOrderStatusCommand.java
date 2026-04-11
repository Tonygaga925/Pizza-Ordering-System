package model.command;

import model.order.Order;
import service.OrderManager;
import java.io.IOException;

public class ChangeOrderStatusCommand implements Command {
    private Order order;
    private String newStatus;
    private OrderManager orderManager;

    public ChangeOrderStatusCommand(Order order, String newStatus, OrderManager orderManager) {
        this.order = order;
        this.newStatus = newStatus;
        this.orderManager = orderManager;
    }

    @Override
    public void execute() {
        try {
            orderManager.changeOrderStatus(order, newStatus);
            if (!newStatus.equalsIgnoreCase("Handling")) {
               System.out.println("Order " + order.getOrderId() + " has been marked as " + newStatus + ".");
            }
        } catch (IOException e) {
            System.err.println("Error changing order status: " + e.getMessage());
        }
    }

    @Override
    public void undo() {
        // Not implemented for this use case
    }

    @Override
    public String getDescription() {
        return "Change Order Status to " + newStatus;
    }
}
