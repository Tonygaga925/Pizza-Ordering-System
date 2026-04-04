package service;

import com.google.gson.reflect.TypeToken;
import model.Order;
import util.JsonUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class OrderManager {
    private String orderFilePath;
    private List<Order> orders;
    private int orderCounter;
    
    public OrderManager(String orderFilePath) throws IOException {
        this.orderFilePath = orderFilePath;
        loadOrders();
    }
    
    private void loadOrders() throws IOException {
        Type orderListType = new TypeToken<ArrayList<Order>>() {}.getType();
        orders = JsonUtil.readFromFile(orderFilePath, orderListType);
        if (orders == null) {
            orders = new ArrayList<>();
            orderCounter = 1;
        } else {
            orderCounter = orders.size() + 1;
        }
    }
    
    private void saveOrders() throws IOException {
        JsonUtil.writeToFile(orderFilePath, orders);
    }
    
    private String generateOrderId() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("ORD%s%03d", date, orderCounter++);
    }
    
    public String placeOrder(String memberId, String customerName, String phone, String pizzaName,
                             String size, List<String> extraToppings, double totalPrice) throws IOException {
        String orderId = generateOrderId();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        Order order = new Order(orderId, memberId, customerName, phone, pizzaName, 
                                size, extraToppings, totalPrice, timestamp, "completed");
        orders.add(order);
        saveOrders();
        return orderId;
    }
    
    public String placeOrder(String customerName, String phone, String pizzaName,
                             String size, List<String> extraToppings, double totalPrice) throws IOException {
        return placeOrder(null, customerName, phone, pizzaName, size, extraToppings, totalPrice);
    }
    
    public Order getOrderById(String orderId) {
        for (Order order : orders) {
            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }
    
    public List<Order> getOrdersByMemberId(String memberId) {
        return orders.stream()
                .filter(order -> memberId.equals(order.getMemberId()))
                .collect(Collectors.toList());
    }
    
    public List<Order> getOrdersByPhone(String phone) {
        return orders.stream()
                .filter(order -> phone.equals(order.getPhone()))
                .collect(Collectors.toList());
    }
    
    public void displayOrder(Order order) {
        if (order == null) {
            System.out.println("Order not found!");
            return;
        }
        
        System.out.println("\n========== Order Details ==========");
        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Customer: " + order.getCustomerName());
        System.out.println("Phone: " + order.getPhone());
        System.out.println("Pizza: " + order.getPizzaName());
        System.out.println("Size: " + order.getSize());
        System.out.println("Extra Toppings: " + String.join(", ", order.getExtraToppings()));
        System.out.printf("Total: $%.2f%n", order.getTotalPrice());
        System.out.println("Time: " + order.getTimestamp());
        System.out.println("Status: " + order.getStatus());
        System.out.println("====================================\n");
    }
    
    public void displayOrders(List<Order> orderList) {
        if (orderList.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        
        System.out.println("\n========== Order List ==========");
        for (Order order : orderList) {
            System.out.printf("%s | %s | %s | $%.2f | %s%n",
                    order.getOrderId(), order.getPizzaName(), 
                    order.getSize(), order.getTotalPrice(), order.getTimestamp());
        }
        System.out.println("================================\n");
    }
}