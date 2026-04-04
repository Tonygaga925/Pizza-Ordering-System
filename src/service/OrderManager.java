package service;

import com.google.gson.reflect.TypeToken;
import model.Order;
import util.JsonUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private String orderFilePath;
    private List<Order> orders;

    public OrderManager(String orderFilePath) throws IOException {
        this.orderFilePath = orderFilePath;
        loadOrders();
    }

    private void loadOrders() throws IOException {
        Type orderListType = new TypeToken<ArrayList<Order>>() {}.getType();
        orders = JsonUtil.readFromFile(orderFilePath, orderListType);
        if (orders == null) orders = new ArrayList<>();
    }

    public void saveOrder(Order order) throws IOException {
        orders.add(order);
        JsonUtil.writeToFile(orderFilePath, orders);
    }

    public void showAllOrders() {
        if (orders.isEmpty()) {
            System.out.println("No orders yet.");
            return;
        }
        for (Order o : orders) {
            System.out.printf("Customer: %s | Pizza: %s | Size: %s | Extra: %s | Total: $%.2f | Time: %s%n",
                    o.getCustomerName(), o.getPizzaName(), o.getSize(),
                    String.join(",", o.getExtraToppings()), o.getTotalPrice(), o.getTimestamp());
        }
    }
}