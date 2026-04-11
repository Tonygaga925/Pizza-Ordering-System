package model.command;

import model.order.Order;
import model.order.OrderItem;
import java.util.List;

public class AddItemCommand implements Command {
    private Order order;
    private List<OrderItem> items;
    private OrderItem item;

    public AddItemCommand(Order order, List<OrderItem> items, OrderItem item) {
        this.order = order;
        this.items = items;
        this.item = item;
    }

    @Override
    public void execute() {
        items.add(item);
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
        return "Add Item"; 
    }
}
