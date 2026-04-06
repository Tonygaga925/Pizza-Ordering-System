package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.order.Order;
import model.order.OrderItem;
import model.pizza.Pizza;
import model.size.Size;
import model.Member;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OrderManager {
    private Map<String, Order> orders;
    private final String orderFilePath;
    private final Gson gson;
    private MemberManager memberManager;

    public OrderManager(String orderFilePath) throws IOException {
        this.orderFilePath = orderFilePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.orders = new ConcurrentHashMap<>();
        loadOrders();
    }

    public void setMemberManager(MemberManager memberManager) {
        this.memberManager = memberManager;
    }

    private void loadOrders() throws IOException {
        File file = new File(orderFilePath);
        if (!file.exists()) {
            orders = new ConcurrentHashMap<>();
            saveOrders();
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Order>>() {
            }.getType();
            Map<String, Order> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                orders = new ConcurrentHashMap<>(loaded);
            } else {
                orders = new ConcurrentHashMap<>();
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load orders, starting with empty orders.");
            orders = new ConcurrentHashMap<>();
        }
    }

    private void saveOrders() throws IOException {
        try (Writer writer = new FileWriter(orderFilePath)) {
            gson.toJson(orders, writer);
        }
    }

    public void refreshOrders() {
        try {
            loadOrders();
            System.out.println("Orders refreshed. Total orders: " + orders.size());
        } catch (IOException e) {
            System.err.println("Warning: Could not refresh orders: " + e.getMessage());
        }
    }

    public List<Order> getOrdersByMemberIdFromFile(String memberId) {
        List<Order> memberOrders = new ArrayList<>();
        File file = new File(orderFilePath);
        
        if (!file.exists()) {
            return memberOrders;
        }
        
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Order>>() {}.getType();
            Map<String, Order> loadedOrders = gson.fromJson(reader, type);
            
            if (loadedOrders != null) {
                for (Order order : loadedOrders.values()) {
                    if (memberId != null && memberId.equals(order.getMemberId())) {
                        memberOrders.add(order);
                    }
                }
            }
            
            memberOrders.sort(Comparator.comparing(Order::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())));
            
        } catch (IOException e) {
            System.err.println("Error reading orders file: " + e.getMessage());
        }
        
        return memberOrders;
    }

    public String placeOrder(String memberId, String customerName, String phone,
            Pizza pizza, Size size, int quantity) throws IOException {
        OrderItem item = new OrderItem(
            pizza.getDescription(),
            pizza.getPrice(),
            pizza.getPoints(),
            size.getName(),
            size.getMultiplier(),
            quantity
        );
        List<OrderItem> items = new ArrayList<>();
        items.add(item);

        Order order = new Order(memberId, customerName, phone, items);

        if (memberId != null && memberManager != null) {
            Member member = memberManager.getMemberById(memberId);
            if (member != null && member.getDiscount() > 0) {
                order.applyDiscount(member.getDiscount());
            }
        }

        return placeOrder(order);
    }

    public String placeOrder(String memberId, String customerName, String phone,
            List<OrderItem> items) throws IOException {
        Order order = new Order(memberId, customerName, phone, items);

        if (memberId != null && memberManager != null) {
            Member member = memberManager.getMemberById(memberId);
            if (member != null && member.getDiscount() > 0) {
                order.applyDiscount(member.getDiscount());
            }
        }

        return placeOrder(order);
    }

    public String placeOrder(Order order) throws IOException {
        String orderId = order.getOrderId();
        if (orderId == null || orderId.isEmpty()) {
            orderId = generateOrderId();
            order.setOrderId(orderId);
        }
        orders.put(orderId, order);
        saveOrders();
        return orderId;
    }

    public Order getOrderById(String orderId) {
        return orders.get(orderId);
    }

    public List<Order> getOrdersByMemberId(String memberId) {
        List<Order> memberOrders = new ArrayList<>();
        for (Order order : orders.values()) {
            if (memberId != null && memberId.equals(order.getMemberId())) {
                memberOrders.add(order);
            }
        }
        memberOrders.sort(Comparator.comparing(Order::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())));
        return memberOrders;
    }

    public void displayOrder(Order order, boolean isMember) {
        if (order == null) {
            System.out.println("Order not found!");
            return;
        }
        order.displayOrder(isMember);
    }

    public void displayOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }

        System.out.println("\n=== Order History ===");
        for (Order order : orders) {
            String timestamp = order.getTimestamp();
            if (timestamp != null && timestamp.length() > 19) {
                timestamp = timestamp.substring(0, 19);
            }
            System.out.printf("%s | %d item(s) | $%.2f | %s%n",
                    order.getOrderId(),
                    order.getItems().size(),
                    order.getFinalTotal(),
                    timestamp != null ? timestamp : "Unknown");
        }
        System.out.println("=====================");
    }

    private String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }
}