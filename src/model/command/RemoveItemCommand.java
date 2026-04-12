package model.command;

import model.order.Order;
import java.util.List;
import model.order.OrderItem;

public class RemoveItemCommand implements Command {
    private Order order;
    private List<OrderItem> items;
    private int itemIdx;

    public RemoveItemCommand(Order order, List<OrderItem> items, int itemIdx) {
        this.order = order;
        this.items = items;
        this.itemIdx = itemIdx;
    }

    @Override
    public void execute() {
        if (items.size() <= 1) {
            System.out.println("Cannot remove the only item in the order!");
        } else {
            items.remove(itemIdx);
            if (order != null) {
                order.calculateTotals();
            }
            System.out.println("Item removed.");
        }
    }

    @Override
    public void undo() {
        // No undo needed
    }

    @Override
    public String getDescription() { 
        return "Remove Item"; 
    }
}
