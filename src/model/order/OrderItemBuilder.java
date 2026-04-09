package model.order;

import model.pizza.*;
import java.util.*;

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
        Pizza p = pizza;
        while(p instanceof ToppingDecorator) {
            p = ((ToppingDecorator)p).getPizza();
        }
        return p.getDescription();
    }

    public double getTotalPrice() {
        double basePrice = getBasePizzaPrice(pizza);
        double totalToppingPrice = pizza.getPrice() - basePrice;
        return (basePrice * sizeMultiplier) + totalToppingPrice;
    }

    public int getTotalPoints() {
        return pizza.getPoints();
    }

    public String getSizeName() {
        return sizeName;
    }

    private double getBasePizzaPrice(Pizza p) {
        while(p instanceof ToppingDecorator) {
            p = ((ToppingDecorator) p).getPizza();
        }
        return p.getPrice();
    }

    public OrderItemBuilder addTopping(String toppingName) {
        this.pizza = PizzaFactory.addToppingByName(this.pizza, toppingName);
        return this;
    }

    public OrderItemBuilder addToppings(List<String> toppingNames) {
        for(String t : toppingNames) {
            addTopping(t);
        }
        return this;
    }

    public OrderItemBuilder removeTopping(String toppingName) {
        return removeLastTopping();
    }

    public OrderItemBuilder removeLastTopping() {
        if (this.pizza instanceof ToppingDecorator) {
            this.pizza = ((ToppingDecorator) this.pizza).getPizza();
        }
        return this;
    }

    public String getAllSelectedToppingNames(){
        String s = "";
        Pizza current = pizza;
        List<String> toppings = new ArrayList<>();
        while(current instanceof ToppingDecorator) {
            ToppingDecorator decorator = (ToppingDecorator) current;
            toppings.add(0, decorator.getToppingName());
            current = decorator.getPizza();
        }
        if(!toppings.isEmpty()){
            for (int i=0; i<toppings.size(); i++) {
                s += (i==0? " | Topping: " : " + " ) + toppings.get(i);
            }
        }
        return s;
    }


    public OrderItem build(){
        return new OrderItem(
                getPizzaDescription() + " (" + sizeName + ") " + getAllSelectedToppingNames(),
                getTotalPrice(),
                getTotalPoints(),
                sizeName,
                sizeMultiplier,
                quantity);
    }
}