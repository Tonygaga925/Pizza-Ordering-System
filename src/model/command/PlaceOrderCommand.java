package model.command;

import model.order.Order;
import model.Member;
import service.OrderManager;
import service.MemberManager;
import java.io.IOException;
import java.util.Scanner;
import view.InputView;

public class PlaceOrderCommand implements Command {
    private Order order;
    private boolean isMember;
    private OrderManager orderManager;
    private MemberManager memberManager;
    private Scanner scanner;

    public PlaceOrderCommand(Order order, boolean isMember, OrderManager orderManager, MemberManager memberManager, Scanner scanner) {
        this.order = order;
        this.isMember = isMember;
        this.orderManager = orderManager;
        this.memberManager = memberManager;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        try {
            String orderId = "ORD" + System.currentTimeMillis();
            order.setOrderId(orderId);
            Member member = isMember ? memberManager.getCurrentMember() : null;

            if (isMember && member != null) {
                if (member.getDiscount() > 0) {
                    order.applyDiscount(member.getDiscount());
                }
            } else { // for guest, no points earned
                order.setTotalPoints(0);
            }

            while (true) {
                String couponCode = InputView.getStringInput("\nEnter coupon code (or press Enter to skip): ");
                if (couponCode.isEmpty()) break;

                model.order.Coupon coupon = service.CouponManager.getInstance().validateCoupon(couponCode);
                if (coupon != null) {
                    order.applyCoupon(coupon);
                    System.out.println("Coupon applied: " + coupon.getCode());
                    break;
                } else {
                    System.out.println("Invalid coupon code. Please try again or press Enter to skip.");
                }
            }

            String confirm = InputView.getStringInput("\nFinal Confirmation - Place order now? (y/n): ").toLowerCase();
            if (confirm.equals("y")) {
                orderManager.placeOrder(order);
                System.out.println("\nOrder placed successfully!");
                System.out.println("Your Order ID: " + orderId);
                
                // for updating member points and change member state if over the vip threshold
                if (isMember && member != null) {
                    memberManager.updateMemberPoints(member.getId(), order.getTotalPoints());
                }
                view.OrderView.displayOrder(order, isMember);
            } else {
                System.out.println("Order cancelled.");
            }
        } catch (IOException e) {
            System.err.println("Error placing order: " + e.getMessage());
        }
    }

    @Override
    public void undo() {
        // No undo for checkout
    }

    @Override
    public String getDescription() {
        return "Place Order";
    }
}
