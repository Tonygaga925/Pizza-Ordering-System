package model;

import java.util.List;

public class Order {
    private String orderId;
    private String memberId;
    private String customerName;
    private String phone;
    private String pizzaName;
    private String size;
    private List<String> extraToppings;
    private double totalPrice;
    private String timestamp;
    private String status;         // pending, completed, cancelled
    
    public Order(String orderId, String memberId, String customerName, String phone,
                 String pizzaName, String size, List<String> extraToppings,
                 double totalPrice, String timestamp, String status) {
        this.orderId = orderId;
        this.memberId = memberId;
        this.customerName = customerName;
        this.phone = phone;
        this.pizzaName = pizzaName;
        this.size = size;
        this.extraToppings = extraToppings;
        this.totalPrice = totalPrice;
        this.timestamp = timestamp;
        this.status = status;
    }
    
    // Getters
    public String getOrderId() { return orderId; }
    public String getMemberId() { return memberId; }
    public String getCustomerName() { return customerName; }
    public String getPhone() { return phone; }
    public String getPizzaName() { return pizzaName; }
    public String getSize() { return size; }
    public List<String> getExtraToppings() { return extraToppings; }
    public double getTotalPrice() { return totalPrice; }
    public String getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
}