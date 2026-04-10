package model.order;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Order {
    private String orderId;
    private String memberId;
    private String customerName;
    private String phone;
    private List<OrderItem> items = new java.util.ArrayList<>();
    private double originalTotal;
    private double finalTotal;
    private double discountApplied;
    private int totalPoints;
    private String timestamp;
    private String status = "Processing";

    public Order() {
        // Default constructor for Gson
    }
    
    public Order(String memberId, String customerName, String phone, List<OrderItem> items) {
        this.memberId = memberId;
        this.customerName = customerName;
        this.phone = phone;
        this.items = items;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.timestamp = LocalDateTime.now().format(formatter);
        this.status = "Processing";
        calculateTotals();
    }
    
    private void calculateTotals() {
        this.originalTotal = 0;
        this.totalPoints = 0;
        for (OrderItem item : items) {
            this.originalTotal += item.getItemTotal();
            this.totalPoints += item.getItemPoints();
        }
        this.finalTotal = this.originalTotal;
        this.discountApplied = 0;
    }
    
    public void applyDiscount(double discountRate) {
        this.discountApplied = originalTotal * discountRate;
        this.finalTotal = originalTotal - discountApplied;
    }
    
    // Getters and setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getMemberId() { return memberId; }
    public String getCustomerName() { return customerName; }
    public String getPhone() { return phone; }
    public List<OrderItem> getItems() { return items; }
    public double getOriginalTotal() { return originalTotal; }
    public double getFinalTotal() { return finalTotal; }
    public double getDiscountApplied() { return discountApplied; }
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int points) { this.totalPoints = points; }
    public String getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status){this.status = status;}

    public void displayOrder(boolean isMember) {
    System.out.println("\n=== Order Details ===");
    System.out.println("Order ID: " + orderId);
    System.out.println("Date: " + timestamp);
    System.out.println("Customer: " + customerName);
    System.out.println("Phone: " + phone);
    System.out.println("\nItems:");
    for (int i = 0; i < items.size(); i++) {
        OrderItem item = items.get(i);
        System.out.printf("  %d. %s - $%.2f each x%d = $%.2f%n",
            i + 1, item.getDescription(), 
            item.getPizzaPrice(),
            item.getQuantity(), item.getItemTotal());
    }

    System.out.printf("\nTotal: $%.2f%n", originalTotal);

    if(isMember){
    if (discountApplied > 0) {
        System.out.printf("Discount: -$%.2f%n", discountApplied);
        System.out.printf("Final total: $%.2f%n", finalTotal);
        }
        System.out.printf("Points earned: %d%n", totalPoints);
    }

}

}