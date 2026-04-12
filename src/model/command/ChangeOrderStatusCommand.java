package model.command;

import model.order.Order;
import model.order.OrderStatus;
import model.order.OrderStatusFactory;
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
            OrderStatus state = OrderStatusFactory.createStatus(newStatus);
            orderManager.changeOrderStatus(order, state.getStatusName());
            if (!state.getStatusName().equalsIgnoreCase("Handling")) {
               System.out.println("Order " + order.getOrderId() + " has been marked as " + state.getStatusName() + ".");
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
