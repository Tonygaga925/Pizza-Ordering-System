package model.order;

import model.pizza.*;

public class OrderItemBuilder {
    private Pizza pizza;
    private String sizeName = "Small";
    private double sizeMultiplier = 1.0;
    private int quantity = 1;

    public OrderItemBuilder setPizza(Pizza pizza) {
        this.pizza = pizza;
        return this;
    }

    public OrderItemBuilder setSize(String sizeName, double sizeMultiplier) {
        this.sizeName = sizeName;
        this.sizeMultiplier = sizeMultiplier;
        return this;
    }

    public OrderItemBuilder setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public Pizza getPizza() {
        return pizza;
    }

    public String getPizzaDescription() {
        return pizza.getDescription();
    }

    public double getTotalPrice() {
        return pizza.getPrice() * sizeMultiplier;
    }

    public int getTotalPoints() {
        return pizza.getPoints();
    }

    public String getSizeName() {
        return sizeName;
    }

    public void addTopping(String toppingName) {
        this.pizza = PizzaFactory.addToppingByName(this.pizza, toppingName);
    }
    
    public void removeLastTopping() {
        if (this.pizza instanceof ToppingDecorator) {
            this.pizza = ((ToppingDecorator) this.pizza).getPizza();
        }
    }
    
    public boolean hasTopping(String toppingName) {
        Pizza current = pizza;
        while (current instanceof ToppingDecorator) {
            ToppingDecorator decorator = (ToppingDecorator) current;
            if (decorator.getToppingName().equalsIgnoreCase(toppingName)) {
                return true;
            }
            current = decorator.getPizza();
        }
        return false;
    }

    public OrderItem build() {

        return new OrderItem(
                getPizzaDescription() + " (" + sizeName + ")",
                getTotalPrice(),
                getTotalPoints(),
                sizeName,
                sizeMultiplier,
                quantity);
 
    }
}