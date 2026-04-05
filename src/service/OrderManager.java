package service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import model.Order;
import model.Member;
import model.Pizza;
import model.size.*;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OrderManager {
    private Map<String, Order> orders;
    private final String orderFilePath;
    private final Gson gson;
    private MemberManager memberManager;
    private MenuLoader menuLoader;
    
    public OrderManager(String orderFilePath) throws IOException {
        this.orderFilePath = orderFilePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.orders = new ConcurrentHashMap<>();
        loadOrders();
    }
    
    public void setMemberManager(MemberManager memberManager) {
        this.memberManager = memberManager;
    }
    
    public void setMenuLoader(MenuLoader menuLoader) {
        this.menuLoader = menuLoader;
    }
    

    private void loadOrders() throws IOException {
    File file = new File(orderFilePath);
    if (!file.exists()) {
        orders = new ConcurrentHashMap<>();
        saveOrders(); // Create empty file
        return;
    }
    
    try (Reader reader = new FileReader(file)) {
        // Check if file is empty
        if (file.length() == 0) {
            orders = new ConcurrentHashMap<>();
            saveOrders();
            return;
        }
        
        // Try to parse as JsonElement
        com.google.gson.JsonElement element = gson.fromJson(reader, com.google.gson.JsonElement.class);
        
        if (element == null) {
            orders = new ConcurrentHashMap<>();
        } else if (element.isJsonObject()) {
            // Object format: {"ORD123": {...}, "ORD456": {...}}
            Type type = new TypeToken<Map<String, Order>>(){}.getType();
            Map<String, Order> loaded = gson.fromJson(element, type);
            orders = loaded != null ? new ConcurrentHashMap<>(loaded) : new ConcurrentHashMap<>();
        } else if (element.isJsonArray()) {
            // Array format: [{...}, {...}]
            com.google.gson.JsonArray array = element.getAsJsonArray();
            orders = new ConcurrentHashMap<>();
            for (var item : array) {
                Order order = gson.fromJson(item, Order.class);
                if (order != null && order.getOrderId() != null) {
                    orders.put(order.getOrderId(), order);
                }
            }
            // Convert to object format on next save
        } else {
            orders = new ConcurrentHashMap<>();
        }
        
        System.out.println("Loaded " + orders.size() + " orders from " + orderFilePath);
        
    } catch (Exception e) {
        System.err.println("Warning: Could not load orders from " + orderFilePath);
        System.err.println("Error: " + e.getMessage());
        System.err.println("Starting with empty orders.");
        orders = new ConcurrentHashMap<>();
        // Backup corrupted file
        File backup = new File(orderFilePath + ".backup");
        if (file.exists()) {
            file.renameTo(backup);
            System.err.println("Corrupted file backed up to: " + backup.getName());
        }
    }
}
    
private void saveOrders() throws IOException {
    // Always save as object format for better performance
    try (Writer writer = new FileWriter(orderFilePath)) {
        gson.toJson(orders, writer);
    }
}
     // For member orders with Pizza object (recommended)
    public String placeOrder(String memberId, String customerName, String phone, 
                             Pizza pizza, Size size, List<String> extraToppings, 
                             double originalTotal) throws IOException {
        String orderId = generateOrderId();
        double finalTotal = originalTotal;
        double discount = 0;
        
        // Calculate points based on pizza and order details
        int pointsEarned = calculatePoints(pizza, size, extraToppings);
        
        // Apply member discount if applicable
        if (memberId != null && !memberId.isEmpty() && memberManager != null) {
            Member member = memberManager.getMemberById(memberId);
            if (member != null) {
                double discountRate = member.getDiscount();
                if (discountRate > 0) {
                    discount = originalTotal * discountRate;
                    finalTotal = originalTotal - discount;
                    System.out.println(member.getLevelDisplay() + " discount applied: -$" + String.format("%.2f", discount));
                }
                
                // Update member points after order
                memberManager.updateMemberPoints(memberId, pointsEarned);
            }
        }
        
        Order order = new Order(orderId, memberId, customerName, phone, 
                                pizza.getName(), size, extraToppings, originalTotal);
        order.setFinalTotal(finalTotal);
        order.setDiscountApplied(discount);
        order.setPointsEarned(pointsEarned);
        
        orders.put(orderId, order);
        saveOrders();
        
        // Display points earned info
        if (memberId != null && memberManager != null) {
            System.out.println("Points earned this order: " + pointsEarned);
            
            // Show updated member info
            Member member = memberManager.getMemberById(memberId);
            if (member != null) {
                System.out.println("Total points now: " + member.getPoints());
                if (member.getPointsToNextLevel() > 0) {
                    System.out.println( member.getPointsToNextLevel() + " more points to reach VIP!");
                }
            }
        }
        
        return orderId;
    }
     // Overloaded method for guest orders
    public String placeOrder(String customerName, String phone, 
                             Pizza pizza, Size size, List<String> extraToppings, 
                             double originalTotal) throws IOException {
        return placeOrder(null, customerName, phone, pizza, size, extraToppings, originalTotal);
    }
    
    // Legacy method for backward compatibility
    public String placeOrder(String memberId, String customerName, String phone, 
                             String pizzaName, Size size, List<String> extraToppings, 
                             double total) throws IOException {
        // Create a temporary Pizza object if we don't have the actual one
        Pizza tempPizza = new Pizza(pizzaName, total, extraToppings, (int)(total * 10));
        return placeOrder(memberId, customerName, phone, tempPizza, size, extraToppings, total);
    }
       private int calculatePoints(Pizza pizza, Size size, List<String> extraToppings) {
    int points = pizza.getPointsValue();
    
    // Size bonus using factory pattern
    points = (int)(points * size.getMultiplier());
    
    // Extra toppings bonus (5 points per extra topping)
    if (extraToppings != null) {
        points += extraToppings.size() * 5;
    }
    
    return points;
}
    
    private String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }
    
    public Order getOrderById(String orderId) {
        return orders.get(orderId);
    }
    public List<Order> getOrdersByMemberId(String memberId) {
    List<Order> memberOrders = new ArrayList<>();
    for (Order order : orders.values()) {
        if (memberId.equals(order.getMemberId())) {
            memberOrders.add(order);
        }
    }
    // Sort by timestamp (newest first)
    memberOrders.sort(Comparator.comparing(Order::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())));
    return memberOrders;
}
public void displayOrder(Order order) {
    if (order == null) {
        System.out.println("Order not found!");
        return;
    }
    
    System.out.println("\n=== ORDER DETAILS ===");
    System.out.println("Order ID: " + order.getOrderId());
    System.out.println("Date: " + order.getTimestamp());
    System.out.println("Status: " + order.getStatus());
    System.out.println("Customer: " + order.getCustomerName());
    System.out.println("Phone: " + order.getPhone());
    System.out.println("Pizza: " + order.getPizzaName());
    System.out.println("Size: " + order.getSize());
    if (order.getExtraToppings() != null && !order.getExtraToppings().isEmpty()) {
        System.out.println("Extra toppings: " + String.join(", ", order.getExtraToppings()));
    }
    System.out.println("Original total: $" + String.format("%.2f", order.getOriginalTotal()));
    if (order.getDiscountApplied() > 0) {
        System.out.println("Discount: -$" + String.format("%.2f", order.getDiscountApplied()));
    }
    System.out.println("Final total: $" + String.format("%.2f", order.getFinalTotal()));
    if (order.getPointsEarned() > 0) {
        System.out.println("Points earned: " + order.getPointsEarned());
    }
    System.out.println("=====================");
}

public void displayOrders(List<Order> orders) {
    if (orders.isEmpty()) {
        System.out.println("No orders found.");
        return;
    }
    
    System.out.println("\n=== ORDER HISTORY ===");
    for (Order order : orders) {
        String timestamp = order.getTimestamp();
        if (timestamp != null && timestamp.length() > 19) {
            timestamp = timestamp.substring(0, 19);
        }
        System.out.printf("%s | %s | $%.2f | %s | %s%n", 
            order.getOrderId(), 
            order.getPizzaName(), 
            order.getFinalTotal(),
            order.getStatus() != null ? order.getStatus() : "unknown",
            timestamp != null ? timestamp : "Unknown");
    }
    System.out.println("=====================");
}
}