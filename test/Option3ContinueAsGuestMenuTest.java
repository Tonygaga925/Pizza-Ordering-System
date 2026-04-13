import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.command.CommandFactory;
import model.command.PlaceOrderCommand;
import model.order.Order;
import model.order.OrderItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.EmployeeManager;
import service.MemberManager;
import service.MenuLoader;
import service.OrderManager;
import view.InputView;
import view.MainView;

public class Option3ContinueAsGuestMenuTest {

    private MemberManager memberManager;
    private OrderManager orderManager;

    @BeforeEach
    void setUp() throws Exception {
        resetMemberManagerSingleton();
        resetOrderManagerSingleton();
        resetEmployeeManagerSingleton();
        resetCommandFactorySingleton();

        Path tempMembersFile = Files.createTempFile("members-option3-", ".json");
        Files.writeString(tempMembersFile, "{}");

        Path tempOrdersFile = Files.createTempFile("orders-option3-", ".json");
        Files.writeString(tempOrdersFile, "{}");

        Path tempStaffFile = Files.createTempFile("staff-option3-", ".json");
        String staffJson = "{\n"
                + "  \"S001\": {\n"
                + "    \"id\": \"S001\",\n"
                + "    \"username\": \"jdoe_staff\",\n"
                + "    \"password\": \"password123\",\n"
                + "    \"name\": \"John\",\n"
                + "    \"role\": \"normal\",\n"
                + "    \"isActive\": true\n"
                + "  }\n"
                + "}";
        Files.writeString(tempStaffFile, staffJson);

        memberManager = MemberManager.getInstance(tempMembersFile.toString());
        orderManager = OrderManager.getInstance(tempOrdersFile.toString());
        EmployeeManager employeeManager = EmployeeManager.getInstance(tempStaffFile.toString());

        CommandFactory.initialize(memberManager, orderManager, employeeManager, MenuLoader.getInstance(), InputView.getScanner());
    }

    @AfterEach
    void tearDown() {
        InputView.resetScanner();
    }

    @Test
    void option3_MainMenu_ShouldDisplayContinueAsGuest() {
        String printed = captureOutput(MainView::displayMainMenu);
        assertTrue(printed.contains("=== Pizza Ordering System ==="));
        assertTrue(printed.contains("1. Member Login"));
        assertTrue(printed.contains("2. Register"));
        assertTrue(printed.contains("3. Continue as Guest"));
        assertTrue(printed.contains("4. Search Order by ID"));
        assertTrue(printed.contains("5. Employee Login"));
        assertTrue(printed.contains("6. Exit"));
        assertTrue(printed.contains("Choose:"));
    }

    @Test
    void option3_GuestOrderSubOption1_AddNewPizza_ShouldBuildItem() {
        List<OrderItem> items = new ArrayList<>();
        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("1\n1\n1\n0\n2\n".getBytes())));

        String output = captureOutput(() -> invokePrivateAddNewPizza(items, false, null));

        assertEquals(1, items.size());
        assertEquals(2, items.get(0).getQuantity());
        assertTrue(output.contains("--- Add New Pizza ---"));
        assertTrue(output.contains("Pizza added to cart!"));
    }

    @Test
    void option3_GuestOrderSubOption2_ModifyExistingPizza_ShouldUpdateQuantity() {
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Margherita (Small)", 42.0, 110, "Small", 1.0, 1));
        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("1\n5\n".getBytes())));

        String output = captureOutput(() -> invokePrivateModifyPizzaQuantity(items));

        assertEquals(5, items.get(0).getQuantity());
        assertTrue(output.contains("Quantity updated!"));
    }

    @Test
    void option3_GuestOrderSubOption3_RemovePizza_ShouldRemoveItem() {
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Margherita (Small)", 42.0, 110, "Small", 1.0, 1));
        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("1\n".getBytes())));

        String output = captureOutput(() -> invokePrivateRemovePizza(items));

        assertTrue(items.isEmpty());
        assertTrue(output.contains("Removed:"));
        assertTrue(output.contains("Cart is now empty."));
    }

    @Test
    void option3_GuestOrderSubOption4_ProceedCheckoutYes_ShouldPlaceOrderAndZeroPoints() {
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Hawaiian (Medium)", 55.9, 130, "Medium", 1.3, 3));
        Order order = new Order(null, "Guest", "98765432", items);

        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("\ny\n".getBytes())));
        PlaceOrderCommand placeOrderCommand = new PlaceOrderCommand(order, false, orderManager, memberManager, new Scanner(new ByteArrayInputStream(new byte[0])));

        String output = captureOutput(placeOrderCommand::execute);

        assertTrue(output.contains("Final Confirmation - Place order now? (y/n):"));
        assertTrue(output.contains("Order placed successfully!"));
        assertEquals(0, order.getTotalPoints());
        assertNotNull(orderManager.getOrderById(order.getOrderId()));
    }

    @Test
    void option3_GuestOrderSubOption4_ProceedCheckoutNo_ShouldCancelOrder() {
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Vegetarian (Small)", 40.0, 100, "Small", 1.0, 1));
        Order order = new Order(null, "Guest", "98765432", items);

        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("\nn\n".getBytes())));
        PlaceOrderCommand placeOrderCommand = new PlaceOrderCommand(order, false, orderManager, memberManager, new Scanner(new ByteArrayInputStream(new byte[0])));

        String output = captureOutput(placeOrderCommand::execute);

        assertTrue(output.contains("Order cancelled."));
    }

    @Test
    void option3_GuestAddNewPizza_MinusOne_ShouldBackWithoutAdding() {
        List<OrderItem> items = new ArrayList<>();
        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("-1\n".getBytes())));

        boolean added = invokePrivateAddNewPizza(items, false, null);

        assertFalse(added);
        assertTrue(items.isEmpty());
    }

    @Test
    void option3_GuestContinueOrderFlow_InvalidPhone_ShouldValidateAndAllowCancelCheckout() {
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Margherita (Small)", 42.0, 110, "Small", 1.0, 1));

        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("GuestName\n12ab5678\n123\n98765432\n\nn\n".getBytes())));

        String output = captureOutput(() -> invokePrivateContinueOrderFlow(items, false));

        assertTrue(output.contains("Enter your name:"));
        assertTrue(output.contains("Enter your phone number:"));
        assertTrue(output.contains("Phone number must be exactly 8 digits."));
        assertTrue(output.contains("Phone number can only contain numbers."));
        assertTrue(output.contains("Order cancelled."));
    }

    private String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output));
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return output.toString();
    }

    private boolean invokePrivateAddNewPizza(List<OrderItem> items, boolean isMember, String recommendedPizzaName) {
        try {
            Method method = Main.class.getDeclaredMethod("addNewPizza", List.class, boolean.class, String.class);
            method.setAccessible(true);
            return (boolean) method.invoke(null, items, isMember, recommendedPizzaName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void invokePrivateModifyPizzaQuantity(List<OrderItem> items) {
        try {
            Method method = Main.class.getDeclaredMethod("modifyPizzaQuantity", List.class);
            method.setAccessible(true);
            method.invoke(null, items);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void invokePrivateRemovePizza(List<OrderItem> items) {
        try {
            Method method = Main.class.getDeclaredMethod("removePizza", List.class);
            method.setAccessible(true);
            method.invoke(null, items);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void invokePrivateContinueOrderFlow(List<OrderItem> items, boolean isMember) {
        try {
            Method method = Main.class.getDeclaredMethod("continueOrderFlow", List.class, boolean.class);
            method.setAccessible(true);
            method.invoke(null, items, isMember);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private void resetEmployeeManagerSingleton() throws Exception {
        Field instanceField = EmployeeManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    private void resetCommandFactorySingleton() throws Exception {
        Field instanceField = CommandFactory.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }
}
