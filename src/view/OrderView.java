package view;

import model.order.Order;
import model.order.OrderItem;

public class OrderView {
    public static void displayOrder(Order order, boolean isMember) {
        System.out.println("\n=== Order Details ===");
        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Date: " + order.getTimestamp());
        System.out.println("Customer: " + order.getCustomerName());
        System.out.println("Phone: " + order.getPhone());
        System.out.println("Status: " + order.getStatus());
        System.out.println("\nItems:");
        for (int i = 0; i < order.getItems().size(); i++) {
            OrderItem item = order.getItems().get(i);
            System.out.printf("  %d. %s - $%.2f each x%d = $%.2f%n",
                    i + 1, item.getDescription(),
                    item.getPizzaPrice(),
                    item.getQuantity(), item.getItemTotal());
        }

        System.out.printf("\nOriginal Total: $%.2f%n", order.getOriginalTotal());
        
        if (isMember && order.getDiscountApplied() > 0) {
            System.out.printf("VIP Discount: -$%.2f%n", order.getDiscountApplied());
            System.out.printf("Price after VIP discount: $%.2f%n", order.getOriginalTotal() - order.getDiscountApplied());
        }

        if (order.getCouponDiscountAmount() > 0) {
            System.out.printf("Coupon [%s]: -$%.2f%n", order.getCouponCode(), order.getCouponDiscountAmount());
        }

        System.out.printf("Final Total to pay: $%.2f%n", order.getFinalTotal());

        if (isMember) {
            System.out.printf("Points earned: %d%n", order.getTotalPoints());
        }
    }
}
