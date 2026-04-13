import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Scanner;

import model.command.SearchOrderCommand;
import model.order.Order;
import model.order.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.MemberManager;
import service.OrderManager;

public class Option4SearchOrderCommandTest {

    private MemberManager memberManager;
    private OrderManager orderManager;
    private String memberOrderId;
    private String otherMemberOrderId;
    private String guestOrderId;

    @BeforeEach
    void setUp() throws Exception {
        resetMemberManagerSingleton();
        resetOrderManagerSingleton();

        Path tempMembersFile = Files.createTempFile("members-option4-", ".json");
        String membersJson = "{\n"
                + "  \"M001\": {\n"
                + "    \"id\": \"M001\",\n"
                + "    \"username\": \"alice123\",\n"
                + "    \"password\": \"password123\",\n"
                + "    \"name\": \"Alice\",\n"
                + "    \"phone\": \"91234567\",\n"
                + "    \"points\": 0,\n"
                + "    \"level\": \"Normal\",\n"
                + "    \"registerDate\": \"2026-01-01 10:00:00\"\n"
            + "  },\n"
            + "  \"M002\": {\n"
            + "    \"id\": \"M002\",\n"
            + "    \"username\": \"bob123\",\n"
            + "    \"password\": \"password123\",\n"
            + "    \"name\": \"Bob\",\n"
            + "    \"phone\": \"93456789\",\n"
            + "    \"points\": 0,\n"
            + "    \"level\": \"Normal\",\n"
            + "    \"registerDate\": \"2026-01-02 10:00:00\"\n"
                + "  }\n"
                + "}";
        Files.writeString(tempMembersFile, membersJson);

        Path tempOrdersFile = Files.createTempFile("orders-option4-", ".json");
        Files.writeString(tempOrdersFile, "{}");

        memberManager = MemberManager.getInstance(tempMembersFile.toString());
        orderManager = OrderManager.getInstance(tempOrdersFile.toString());

        Order memberOrder = new Order(
                "M001",
                "Alice",
                "91234567",
            Collections.singletonList(new OrderItem("Margherita (Small)", 42.0, 110, "Small", 1.0, 1)));
        memberOrder.setOrderId("ORD-MEMBER-001");
        memberOrderId = orderManager.placeOrder(memberOrder);

        Order otherMemberOrder = new Order(
            "M002",
            "Bob",
            "93456789",
            Collections.singletonList(new OrderItem("Pepperoni (Small)", 44.0, 120, "Small", 1.0, 1)));
        otherMemberOrder.setOrderId("ORD-MEMBER-002");
        otherMemberOrderId = orderManager.placeOrder(otherMemberOrder);

        Order guestOrder = new Order(
                null,
                "Guest",
                "92345678",
            Collections.singletonList(new OrderItem("Hawaiian (Small)", 48.0, 100, "Small", 1.0, 1)));
        guestOrder.setOrderId("ORD-GUEST-001");
        guestOrderId = orderManager.placeOrder(guestOrder);
    }

    @Test
    void option4_MemberSearchOwnOrder_ShouldDisplayOrderDetails() throws Exception {
        memberManager.login("alice123", "password123");

        SearchOrderCommand command = new SearchOrderCommand(
                true,
                orderManager,
                memberManager,
                new Scanner(new ByteArrayInputStream(new byte[0])));
        command.setOrderId(memberOrderId);

        String output = executeAndCaptureOutput(command);

        assertFalse(output.contains("You are not authorized to view this order."));
        assertTrue(output.contains("=== Order Details ==="));
        assertTrue(output.contains("Order ID: " + memberOrderId));
        assertTrue(output.contains("Customer: Alice"));
        assertTrue(output.contains("Status: Processing"));
        assertTrue(output.contains("Points earned:"));
    }

    @Test
    void option4_GuestSearchMemberOrder_ShouldBeUnauthorized() {
        SearchOrderCommand command = new SearchOrderCommand(
                false,
                orderManager,
                memberManager,
                new Scanner(new ByteArrayInputStream(new byte[0])));
        command.setOrderId(memberOrderId);

        String output = executeAndCaptureOutput(command);

        assertTrue(output.contains("You are not authorized to view this order."));
        assertFalse(output.contains("=== Order Details ==="));
    }

    @Test
    void option4_GuestSearchGuestOrder_ShouldDisplayOrderDetails() {
        SearchOrderCommand command = new SearchOrderCommand(
                false,
                orderManager,
                memberManager,
                new Scanner(new ByteArrayInputStream(new byte[0])));
        command.setOrderId(guestOrderId);

        String output = executeAndCaptureOutput(command);

        assertFalse(output.contains("You are not authorized to view this order."));
        assertTrue(output.contains("=== Order Details ==="));
        assertTrue(output.contains("Order ID: " + guestOrderId));
        assertTrue(output.contains("Customer: Guest"));
        assertTrue(output.contains("Status: Processing"));
        assertFalse(output.contains("Points earned:"));
    }

    @Test
    void option4_SearchOrderByIdInputSuccess_GuestOrder_ShouldDisplayOrderDetails() {
        SearchOrderCommand command = new SearchOrderCommand(
                false,
                orderManager,
                memberManager,
                new Scanner(new ByteArrayInputStream("ORD-GUEST-001\n".getBytes())));

        String output = executeAndCaptureOutput(command);

        assertTrue(output.contains("Enter Order ID:"));
        assertTrue(output.contains("=== Order Details ==="));
        assertTrue(output.contains("Order ID: ORD-GUEST-001"));
        assertTrue(output.contains("Customer: Guest"));
        assertTrue(output.contains("Status: Processing"));
    }

    @Test
    void option4_GuestSearchMinusOneOrderId_ShouldShowNotFound() {
        SearchOrderCommand command = new SearchOrderCommand(
                false,
                orderManager,
                memberManager,
                new Scanner(new ByteArrayInputStream("-1\n".getBytes())));

        String output = executeAndCaptureOutput(command);

        assertTrue(output.contains("Enter Order ID:"));
        assertTrue(output.contains("Order not found!"));
    }

    @Test
    void option4_MemberSearchAnotherMembersOrder_ShouldBeUnauthorized() throws Exception {
        memberManager.login("alice123", "password123");

        SearchOrderCommand command = new SearchOrderCommand(
                true,
                orderManager,
                memberManager,
                new Scanner(new ByteArrayInputStream(new byte[0])));
        command.setOrderId(otherMemberOrderId);

        String output = executeAndCaptureOutput(command);

        assertTrue(output.contains("You are not authorized to view this order."));
        assertFalse(output.contains("=== Order Details ==="));
    }

    private String executeAndCaptureOutput(SearchOrderCommand command) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output));
            command.execute();
        } finally {
            System.setOut(originalOut);
        }
        return output.toString();
    }

    private void resetMemberManagerSingleton() throws Exception {
        Field instanceField = MemberManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    private void resetOrderManagerSingleton() throws Exception {
        Field instanceField = OrderManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }
}
