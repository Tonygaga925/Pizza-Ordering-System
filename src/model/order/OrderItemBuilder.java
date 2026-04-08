package model.order;

import model.pizza.*;
import java.util.*;

public class OrderItemBuilder {
    private String pizzaName;
    private String sizeName = "Small";
    private double sizeMultiplier = 1.0;
    private double price = 0.0;
    private int points = 0;
    private int quantity = 1;
    private List<String> toppingNames = new ArrayList<>();
    private double totalToppingPrice = 0.0;
    private int totalToppingPoints = 0;

    public OrderItemBuilder setPizza(String pizzaName) {
        this.pizzaName = pizzaName;
        return this;
    }

    public OrderItemBuilder setSize(String sizeName, double sizeMultiplier) {
        this.sizeName = sizeName;
        this.sizeMultiplier = sizeMultiplier;
        return this;
    }

    public OrderItemBuilder setPrice(double price) {
        this.price = price;
        return this;
    }

    public OrderItemBuilder setPoints(int points) {
        this.points = points;
        return this;
    }

    public OrderItemBuilder setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public String getPizzaDescription() {
        return pizzaName;
    }

    public double getTotalPrice() {
        return (price * sizeMultiplier) + totalToppingPrice;
    }

    public int getTotalPoints() {
        return points + totalToppingPoints;
    }

    public String getSizeName() {
        return sizeName;
    }

    public OrderItemBuilder addTopping(String toppingName) {
        this.toppingNames.add(toppingName);
        this.totalToppingPrice += PizzaFactory.getToppingPriceByName(toppingName);
        this.totalToppingPoints += PizzaFactory.getToppingPointsByName(toppingName);
        return this;
    }

    public OrderItemBuilder addToppings(List<String> toppingNames) {
        this.toppingNames.addAll(toppingNames);
        return this;
    }

    public OrderItemBuilder removeTopping(String toppingName) {
        this.toppingNames.remove(toppingName);
        this.totalToppingPrice -= PizzaFactory.getToppingPriceByName(toppingName);
        this.totalToppingPoints -= PizzaFactory.getToppingPointsByName(toppingName);
        return this;
    }

    public OrderItemBuilder removeLastTopping() {
        if (!this.toppingNames.isEmpty()) {
            this.toppingNames.remove(this.toppingNames.size() - 1);
        }
        return this;
    }

    public String getAllSelectedToppingNames(){
        String s = "";
        if(!toppingNames.isEmpty()){
            for (int i=0; i<toppingNames.size(); i++) {
                s+= (i==0? "| Topping: " : " + " ) + toppingNames.get(i);
            }
        }
        return s;
    }

    public double getTotalToppingPrice() {
        return this.totalToppingPrice;
    }

    public int getTotalToppingPoints() {
        return this.totalToppingPoints;
    }

    public OrderItem build(){
        return new OrderItem(
                pizzaName + " (" + sizeName + ") " + getAllSelectedToppingNames(),
                getTotalPrice(),
                getTotalPoints(),
                sizeName,
                quantity);
    }


}