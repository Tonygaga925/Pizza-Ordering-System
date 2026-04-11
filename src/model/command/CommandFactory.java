package model.command;

import service.MemberManager;
import service.OrderManager;
import service.EmployeeManager;
import service.MenuLoader;
import service.RecommendationService;
import model.order.Order;
import model.order.OrderItem;
import model.order.OrderItemBuilder;
import model.Member;
import java.util.List;
import java.util.Scanner;

public class CommandFactory {
    private static CommandFactory instance;
    private MemberManager memberManager;
    private OrderManager orderManager;
    private EmployeeManager employeeManager;
    private MenuLoader menuLoader;
    private Scanner scanner;

    private CommandFactory(MemberManager memberManager, OrderManager orderManager, EmployeeManager employeeManager, MenuLoader menuLoader, Scanner scanner) {
        this.memberManager = memberManager;
        this.orderManager = orderManager;
        this.employeeManager = employeeManager;
        this.menuLoader = menuLoader;
        this.scanner = scanner;
    }

    public static void initialize(MemberManager memberManager, OrderManager orderManager, EmployeeManager employeeManager, MenuLoader menuLoader, Scanner scanner) {
        instance = new CommandFactory(memberManager, orderManager, employeeManager, menuLoader, scanner);
    }

    public static CommandFactory getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CommandFactory is not initialized. Call initialize() first.");
        }
        return instance;
    }

    // Login and Registration Commands
    public Command createMemberLoginCommand() {
        return new MemberLoginCommand(memberManager, scanner);
    }

    public Command createMemberRegisterCommand() {
        return new MemberRegisterCommand(memberManager, scanner);
    }

    public Command createEmployeeLoginCommand() {
        return new EmployeeLoginCommand(employeeManager, scanner);
    }

    public Command createRegisterEmployeeCommand() {
        return new RegisterEmployeeCommand(employeeManager, scanner);
    }

    // Order Modification Commands
    public Command createAddItemCommand(Order order, List<OrderItem> items, OrderItem item) {
        return new AddItemCommand(order, items, item);
    }

    public Command createRemoveItemCommand(Order order, List<OrderItem> items, int itemIndex) {
        return new RemoveItemCommand(order, items, itemIndex);
    }

    public Command createUpdateQuantityCommand(Order order, OrderItem item, int newQty) {
        return new UpdateQuantityCommand(order, item, newQty);
    }

    // Order Processing Commands
    public Command createPlaceOrderCommand(Order order, boolean isMember) {
        return new PlaceOrderCommand(order, isMember, orderManager, memberManager, scanner);
    }

    public Command createChangeOrderStatusCommand(Order order, String newStatus) {
        return new ChangeOrderStatusCommand(order, newStatus, orderManager);
    }

    // Search and View Commands
    public Command createSearchOrderCommand(boolean isMemberInvoke) {
        return new SearchOrderCommand(isMemberInvoke, orderManager, memberManager, scanner);
    }

    public Command createViewOrdersCommand(Member member, ReorderCommand.ReorderCallback reorderCallback) {
        return new ViewOrdersCommand(member, orderManager, scanner, reorderCallback);
    }

    public Command createReorderCommand(List<Order> orders, boolean isMember, ReorderCommand.ReorderCallback callback) {
        return new ReorderCommand(orders, isMember, scanner, callback);
    }

    public Command createGetRecommendationCommand(Member member, RecommendationService.MainCallback callback) {
        return new GetRecommendationCommand(member, orderManager, menuLoader, scanner, callback);
    }
    
    // Topping Command
    public Command createAddToppingCommand(String toppingName, OrderItemBuilder itemBuilder) {
        return new AddToppingCommand(toppingName, itemBuilder);
    }
}
