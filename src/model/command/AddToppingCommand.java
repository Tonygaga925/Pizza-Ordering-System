package model.command;

import model.pizza.*;

public class AddToppingCommand implements Command {
    private MutablePizzaWrapper pizzaWrapper;
    private int toppingChoice;
    private String toppingName;
    private double addedPrice;
    private int addedPoints;
    private Pizza previousPizza;
    
    public AddToppingCommand(MutablePizzaWrapper pizzaWrapper, int toppingChoice, String toppingName) {
        this.pizzaWrapper = pizzaWrapper;
        this.toppingChoice = toppingChoice;
        this.toppingName = toppingName;
        this.previousPizza = pizzaWrapper.getPizza();
    }
    
    @Override
    public void execute() {
        try {
            double oldPrice = pizzaWrapper.getPrice();
            int oldPoints = pizzaWrapper.getPoints();
            
            Pizza newPizza = PizzaFactory.addTopping(pizzaWrapper.getPizza(), toppingChoice);
            pizzaWrapper.setPizza(newPizza);
            
            this.addedPrice = pizzaWrapper.getPrice() - oldPrice;
            this.addedPoints = pizzaWrapper.getPoints() - oldPoints;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to add topping: " + toppingName, e);
        }
    }
    
    @Override
    public void undo() {
        if (previousPizza != null) {
            pizzaWrapper.setPizza(previousPizza);
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Added %s (+$%.2f, +%d points)", toppingName, addedPrice, addedPoints);
    }
    
    public String getToppingName() {
        return toppingName;
    }
}