package model.order;

import java.util.*;

public class OrderBuilder {
    private String memberId = null; // null for guest
    private String customerName;
    private String phone;
    private List<OrderItem> items = new ArrayList<>();
 
    public OrderBuilder setCustName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public OrderBuilder setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public OrderBuilder setMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }


    public OrderBuilder addItem(List<OrderItem> orderItems) {
        this.items = orderItems;
        return this;
    }
    
    public Order build() {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalStateException("Customer name is required");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalStateException("Phone number is required");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Order must have at least one item");
        }
        
        return new Order(memberId, customerName, phone, items);
    }
}