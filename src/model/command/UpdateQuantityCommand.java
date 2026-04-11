package model.command;

import model.order.Order;
import model.order.OrderItem;

public class UpdateQuantityCommand implements Command {
    private Order order;
    private OrderItem item;
    private int newQty;

    public UpdateQuantityCommand(Order order, OrderItem item, int newQty) {
        this.order = order;
        this.item = item;
        this.newQty = newQty;
    }

    @Override
    public void execute() {
        item.setQuantity(newQty);
        if (order != null) {
            order.calculateTotals();
        }
    }

    @Override
    public void undo() {
        // No undo needed
    }

    @Override
    public String getDescription() { 
        return "Update Quantity"; 
    }
}
