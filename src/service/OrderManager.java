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

    private static OrderManager instance;
    private static final String DEFAULT_FILE_PATH = "data/orders.json";
    
    private Map<String, Order> orders;
    private final String orderFilePath;
    private final Gson gson;
    private MemberManager memberManager;

    private OrderManager(String orderFilePath) throws IOException {
        this.orderFilePath = orderFilePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.orders = new ConcurrentHashMap<>();
        loadOrders();
    }

    public static OrderManager getInstance() throws IOException {
        if (instance == null) {
            instance = new OrderManager(DEFAULT_FILE_PATH);
        }
        return instance;
    }
    
    public static OrderManager getInstance(String filePath) throws IOException {
        if (instance == null) {
            instance = new OrderManager(filePath);
        }
        return instance;
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
                System.out.println("System: Loaded " + orders.size() + " orders from file.");
            } else {
                orders = new ConcurrentHashMap<>();
                System.out.println("System: Starting with empty orders list.");
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
    
    public List<Order> getOrdersByStatus(String status) {
        List<Order> result = new ArrayList<>();
        if (orders == null) return result;
        
        for (Order order : orders.values()) {
            if (order != null && order.getStatus() != null && status.equalsIgnoreCase(order.getStatus())) {
                result.add(order);
            }
        }
        return result;
    }
    
    public Order getOrderById(String orderId) {
        return orders.get(orderId);
    }
    
    public void displayOrder(Order order, boolean isMember) {
        if (order == null) {
            System.out.println("Order not found!");
            return;
        }
        order.displayOrder(isMember);
    }
    
    public void changeOrderStatus(Order order, String status) throws IOException {
        if (order == null || status == null || status.trim().isEmpty()) {
            System.out.println("Error: Invalid order or status.");
            return;
        }
        order.setStatus(status);
        orders.put(order.getOrderId(), order);
        saveOrders();
    }
    
    private String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }
}