package model.order;

import model.pizza.Pizza;
import model.size.Size;
import java.util.*;

public class OrderBuilder {
    private String memberId;
    private String customerName;
    private String phone;
    private List<OrderItem> items;
    
    public OrderBuilder() {
        this.items = new ArrayList<>();
    }
    
    public OrderBuilder setMember(String memberId, String customerName, String phone) {
        this.memberId = memberId;
        this.customerName = customerName;
        this.phone = phone;
        return this;
    }
    
    public OrderBuilder setGuest(String customerName, String phone) {
        this.memberId = null;
        this.customerName = customerName;
        this.phone = phone;
        return this;
    }
    
    public OrderBuilder addPizza(Pizza pizza, Size size, int quantity) {
        OrderItem item = new OrderItem(pizza, size, quantity);
        items.add(item);
        return this;
    }
    
    public OrderBuilder addPizza(Pizza pizza, Size size) {
        return addPizza(pizza, size, 1);
    }
    
    public Order build() {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalStateException("Customer name is required");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalStateException("Phone number is required");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Order must have at least one pizza");
        }
        
        return new Order(memberId, customerName, phone, items);
    }
}