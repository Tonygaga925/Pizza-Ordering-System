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
    private double originalTotal;
    private double finalTotal;
    private double discountApplied;
    private int pointsEarned;
    private String timestamp;
    private String status;
    
    // For Gson deserialization
    public Order() {}
    
    // Constructor for new orders
    public Order(String orderId, String memberId, String customerName, String phone, 
                 String pizzaName, String size, List<String> extraToppings, double originalTotal) {
        this.orderId = orderId;
        this.memberId = memberId;
        this.customerName = customerName;
        this.phone = phone;
        this.pizzaName = pizzaName;
        this.size = size;
        this.extraToppings = extraToppings;
        this.originalTotal = originalTotal;
        this.finalTotal = originalTotal;
        this.discountApplied = 0;
        this.pointsEarned = 0;
        this.timestamp = java.time.LocalDateTime.now().toString().replace("T", " ");
        this.status = "completed";
    }
    
    // Getters and setters...
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getPizzaName() { return pizzaName; }
    public void setPizzaName(String pizzaName) { this.pizzaName = pizzaName; }
    
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    
    public List<String> getExtraToppings() { return extraToppings; }
    public void setExtraToppings(List<String> extraToppings) { this.extraToppings = extraToppings; }
    
    public double getOriginalTotal() { return originalTotal; }
    public void setOriginalTotal(double originalTotal) { this.originalTotal = originalTotal; }
    
    public double getFinalTotal() { return finalTotal; }
    public void setFinalTotal(double finalTotal) { this.finalTotal = finalTotal; }
    
    public double getDiscountApplied() { return discountApplied; }
    public void setDiscountApplied(double discountApplied) { this.discountApplied = discountApplied; }
    
    public int getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}