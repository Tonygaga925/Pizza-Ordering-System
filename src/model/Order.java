package model;

import java.util.List;

public class Order {
    private String customerName;
    private String pizzaName;
    private String size;
    private List<String> extraToppings;
    private double totalPrice;
    private String timestamp;

    public Order(String customerName, String pizzaName, String size,
                 List<String> extraToppings, double totalPrice, String timestamp) {
        this.customerName = customerName;
        this.pizzaName = pizzaName;
        this.size = size;
        this.extraToppings = extraToppings;
        this.totalPrice = totalPrice;
        this.timestamp = timestamp;
    }

    // Getters (for JSON serialization)
    public String getCustomerName() { return customerName; }
    public String getPizzaName() { return pizzaName; }
    public String getSize() { return size; }
    public List<String> getExtraToppings() { return extraToppings; }
    public double getTotalPrice() { return totalPrice; }
    public String getTimestamp() { return timestamp; }
}