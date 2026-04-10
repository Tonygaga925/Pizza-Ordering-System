package model.command;

import model.order.OrderItemBuilder;

public class AddToppingCommand implements Command {
    private String toppingName;
    private OrderItemBuilder itemBuilder;
    
    public AddToppingCommand(String toppingName, OrderItemBuilder itemBuilder) {
        this.toppingName = toppingName;
        this.itemBuilder = itemBuilder;
    }
    
    @Override
    public void execute() {
        try {
            itemBuilder.addTopping(toppingName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add topping: " + toppingName, e);
        }
    }
    
    @Override
    public void undo() {
        itemBuilder.removeLastTopping();
    }
    
    public String getToppingName() {
        return toppingName;
    }

    @Override
    public String getDescription() {
        return String.format("Added %s", this.getToppingName());
    }
}