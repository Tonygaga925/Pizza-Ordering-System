package model.command;

import model.order.Order;
import model.Member;
import service.OrderManager;
import service.MemberManager;
import java.util.Scanner;

public class SearchOrderCommand implements Command {
    private boolean isMemberInvoke;
    private boolean isStaffInvoke;
    private OrderManager orderManager;
    private MemberManager memberManager;
    private Scanner scanner;
    private String orderId;

    public SearchOrderCommand(boolean isMemberInvoke, OrderManager orderManager, MemberManager memberManager, Scanner scanner) {
        this.isMemberInvoke = isMemberInvoke;
        this.orderManager = orderManager;
        this.memberManager = memberManager;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        if (orderId == null) {
            System.out.print("Enter Order ID: ");
            orderId = scanner.nextLine().trim();
        }

        Order order = orderManager.getOrderById(orderId);

        if (order == null) {
            System.out.println("Order not found!");
            return;
        }

        // Staff bypasses all authorization checks
        if (isStaffInvoke) {
            orderManager.displayOrder(order, false);
            return;
        }

        String notAuthorizedMsg = "You are not authorized to view this order.";

        if (isMemberInvoke) {
            Member member = memberManager.getCurrentMember();
            // Check if the order belongs to the current member
            if (order.getMemberId() != null && !order.getMemberId().equals(member.getId())) {
                System.out.println(notAuthorizedMsg);
                return;
            }
            // Check if the order is a guest order
            else if (order.getMemberId() == null) {
                System.out.println(notAuthorizedMsg);
                return;
            }
        } else {
            // Guest user cannot check members' orders
            if (order.getMemberId() != null) {
                System.out.println(notAuthorizedMsg);
                return;
            }
        }
        orderManager.displayOrder(order, isMemberInvoke);
    }
    
    // For direct use (Employee side)
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setStaffInvoke(boolean staffInvoke) {
        this.isStaffInvoke = staffInvoke;
    }

    @Override
    public void undo() {
        // Read-only
    }

    @Override
    public String getDescription() {
        return "Search Order by ID";
    }
}
