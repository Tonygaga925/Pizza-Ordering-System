package model.command;

import model.order.Order;
import model.Member;
import service.OrderManager;
import service.MemberManager;
import java.io.IOException;
import java.util.Scanner;

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

            System.out.print("\nConfirm order? (y/n): ");
            String confirm = scanner.nextLine().toLowerCase();
            if (confirm.equals("y")) {
                orderManager.placeOrder(order);
                System.out.println("\nOrder placed successfully!");
                System.out.println("Your Order ID: " + orderId);
                
                // for updating member points and change member state if over the vip threshold
                if (isMember && member != null) {
                    memberManager.updateMemberPoints(member.getId(), order.getTotalPoints());
                }
                order.displayOrder(isMember);
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
