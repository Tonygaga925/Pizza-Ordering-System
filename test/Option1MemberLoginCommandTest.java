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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import model.Member;
import model.command.CommandFactory;
import model.command.MemberLoginCommand;
import model.command.SearchOrderCommand;
import model.command.ViewOrdersCommand;
import model.order.Order;
import model.order.OrderItem;
import model.command.PlaceOrderCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.EmployeeManager;
import service.MemberManager;
import service.MenuLoader;
import service.OrderManager;
import service.RecommendationService;
import view.InputView;

public class Option1MemberLoginCommandTest {

    private MemberManager memberManager;
    private OrderManager orderManager;

    @BeforeEach
    void setUp() throws Exception {
        resetMemberManagerSingleton();
        resetOrderManagerSingleton();
        resetEmployeeManagerSingleton();
        resetCommandFactorySingleton();

        Path tempMembersFile = Files.createTempFile("members-option1-", ".json");
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
                + "  }\n"
                + "}";
        Files.writeString(tempMembersFile, membersJson);

        Path tempOrdersFile = Files.createTempFile("orders-option1-", ".json");
        Files.writeString(tempOrdersFile, "{}");

        Path tempStaffFile = Files.createTempFile("staff-option1-", ".json");
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
    void option1_LoginSuccessful_ShouldShowSuccessOutput() {
        MemberLoginCommand command = new MemberLoginCommand(memberManager, new Scanner(new ByteArrayInputStream("alice123\npassword123\n".getBytes())));

        String output = captureOutput(command::execute);

        assertTrue(memberManager.isLoggedIn());
        assertTrue(output.contains("--- Member Login (-1 to go back to previous step) ---"));
        assertTrue(output.contains("Username:"));
        assertTrue(output.contains("Password:"));
        assertTrue(output.contains("Login successful"));
    }

    @Test
    void option1_LoginFailed_ShouldShowInvalidCredentialOutput() {
        MemberLoginCommand command = new MemberLoginCommand(memberManager, new Scanner(new ByteArrayInputStream("alice123\nwrong-pass\n".getBytes())));

        String output = captureOutput(command::execute);

        assertFalse(memberManager.isLoggedIn());
        assertTrue(output.contains("Invalid username or password!"));
    }

    @Test
    void option1_LoginMinusOneAtUsername_ShouldGoBack() {
        MemberLoginCommand command = new MemberLoginCommand(memberManager, new Scanner(new ByteArrayInputStream("-1\n".getBytes())));

        String output = captureOutput(command::execute);

        assertFalse(memberManager.isLoggedIn());
        assertTrue(output.contains("Username:"));
        assertFalse(output.contains("Password:"));
    }

    @Test
    void option1_LoginMinusOneAtPassword_ShouldGoBackToUsername() {
        MemberLoginCommand command = new MemberLoginCommand(memberManager, new Scanner(new ByteArrayInputStream("alice123\n-1\nalice123\npassword123\n".getBytes())));

        String output = captureOutput(command::execute);

        assertTrue(memberManager.isLoggedIn());
        assertTrue(countOccurrences(output, "Username:") >= 2);
        assertTrue(countOccurrences(output, "Password:") >= 2);
    }

    @Test
    void option1_LoginSuccessful_ShouldContainMemberMenuOptionsInMainFlow() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));

        String source = readMainSource();
        assertTrue(source.contains("System.out.println(\"\\n1. Show Menu\");"));
        assertTrue(source.contains("System.out.println(\"2. Place Order\");"));
        assertTrue(source.contains("System.out.println(\"3. View My Orders\");"));
        assertTrue(source.contains("System.out.println(\"4. Search Order by ID\");"));
        assertTrue(source.contains("System.out.println(\"5. View Member Info\");"));
        assertTrue(source.contains("System.out.println(\"6. Get Pizza Recommendations\");"));
        assertTrue(source.contains("System.out.println(\"7. Logout\");"));
    }

    @Test
    void option1_LoginSuccessful_Option1ShowMenu_ShouldDisplayPizzaMenuOutput() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));

        String output = captureOutput(this::invokePrivateShowMenu);

        assertTrue(output.contains("--- Menu ---"));
        assertTrue(output.contains("1. Margherita"));
        assertTrue(output.contains("2. Pepperoni"));
        assertTrue(output.contains("Note: You can add extra toppings after selecting your pizza."));
    }

    @Test
    void option1_LoginSuccessful_Option2PlaceOrderTestCase1_AddNewPizzaDifferentPizzaSizeToppingQty() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));

        List<OrderItem> items = new ArrayList<>();
        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("2\n3\n1\n2\n0\n4\n".getBytes())));

        String output = captureOutput(() -> invokePrivateAddNewPizza(items, true, null));

        assertEquals(1, items.size());
        OrderItem item = items.get(0);
        assertEquals(4, item.getQuantity());
        assertTrue(item.getSizeName().equalsIgnoreCase("Large"));
        assertTrue(item.getPizzaDescription().contains("("));
        assertTrue(item.getPizzaDescription().contains("+"));
        assertTrue(output.contains("--- Add New Pizza ---"));
        assertTrue(output.contains("Pizza added to cart!"));
    }

    @Test
    void option1_LoginSuccessful_Option2PlaceOrderTestCase2_ModifyExistingPizzaQuantity() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Margherita (Small)", 42.0, 110, "Small", 1.0, 1));
        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("1\n3\n".getBytes())));

        String output = captureOutput(() -> invokePrivateModifyPizzaQuantity(items));

        assertEquals(3, items.get(0).getQuantity());
        assertTrue(output.contains("Quantity updated!"));
    }

    @Test
    void option1_LoginSuccessful_Option2PlaceOrderTestCase3_RemovePizza() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Margherita (Small)", 42.0, 110, "Small", 1.0, 1));
        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("1\n".getBytes())));

        String output = captureOutput(() -> invokePrivateRemovePizza(items));

        assertTrue(items.isEmpty());
        assertTrue(output.contains("Removed:"));
        assertTrue(output.contains("Cart is now empty."));
    }

    @Test
    void option1_LoginSuccessful_Option2PlaceOrderTestCase4_ProceedToCheckoutConfirmYes() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));
        Member member = memberManager.getCurrentMember();

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Margherita (Small)", 42.0, 110, "Small", 1.0, 2));
        Order order = new Order(member.getId(), member.getName(), member.getPhone(), items);

        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("\ny\n".getBytes())));
        PlaceOrderCommand placeOrderCommand = new PlaceOrderCommand(order, true, orderManager, memberManager, new Scanner(new ByteArrayInputStream(new byte[0])));

        String output = captureOutput(placeOrderCommand::execute);

        assertTrue(output.contains("Final Confirmation - Place order now? (y/n):"));
        assertTrue(output.contains("Order placed successfully!"));
        assertNotNull(order.getOrderId());
        assertNotNull(orderManager.getOrderById(order.getOrderId()));
    }

    @Test
    void option1_LoginSuccessful_Option2PlaceOrderTestCase5_ProceedToCheckoutEnterNShouldCancel() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));
        Member member = memberManager.getCurrentMember();

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Hawaiian (Medium)", 62.4, 130, "Medium", 1.3, 1));
        Order order = new Order(member.getId(), member.getName(), member.getPhone(), items);

        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("\nn\n".getBytes())));
        PlaceOrderCommand placeOrderCommand = new PlaceOrderCommand(order, true, orderManager, memberManager, new Scanner(new ByteArrayInputStream(new byte[0])));

        String output = captureOutput(placeOrderCommand::execute);

        assertTrue(output.contains("Final Confirmation - Place order now? (y/n):"));
        assertTrue(output.contains("Order cancelled."));
    }

    @Test
    void option1_LoginSuccessful_Option3ViewMyOrders_ShouldShowNoOrdersMessage() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));
        Member member = memberManager.getCurrentMember();

        ViewOrdersCommand command = new ViewOrdersCommand(
                member,
                orderManager,
                new Scanner(new ByteArrayInputStream("-1\n".getBytes())),
                null);

        String output = captureOutput(command::execute);

        assertTrue(output.contains("You have no orders yet."));
    }

    @Test
    void option1_LoginSuccessful_Option3ViewMyOrders_ReorderPreviousOrder_ShouldTriggerReorderCallback() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));
        Member member = memberManager.getCurrentMember();

        List<OrderItem> historicalItems = new ArrayList<>();
        historicalItems.add(new OrderItem("Margherita + Cheese (Small)", 48.0, 118, "Small", 1.0, 2));
        Order oldOrder = new Order(member.getId(), member.getName(), member.getPhone(), historicalItems);
        oldOrder.setOrderId("ORD-HISTORY-001");
        orderManager.placeOrder(oldOrder);

        AtomicBoolean callbackInvoked = new AtomicBoolean(false);
        AtomicReference<List<OrderItem>> reorderedItems = new AtomicReference<>(new ArrayList<>());
        AtomicBoolean reorderedAsMember = new AtomicBoolean(false);

        ViewOrdersCommand command = new ViewOrdersCommand(
                member,
                orderManager,
                new Scanner(new ByteArrayInputStream("0\n1\n".getBytes())),
                (items, isMember) -> {
                    callbackInvoked.set(true);
                    reorderedItems.set(items);
                    reorderedAsMember.set(isMember);
                }
        );

        String output = captureOutput(command::execute);

        assertTrue(output.contains("=== Your Recent Orders"));
        assertTrue(output.contains("[0] Reorder a previous order"));
        assertTrue(output.contains("Enter order number to reorder"));
        assertTrue(callbackInvoked.get());
        assertTrue(reorderedAsMember.get());
        assertEquals(1, reorderedItems.get().size());
        assertEquals("Margherita + Cheese (Small)", reorderedItems.get().get(0).getPizzaDescription());
        assertEquals(2, reorderedItems.get().get(0).getQuantity());
    }

    @Test
    void option1_LoginSuccessful_Option3ViewMyOrders_NotReorder_ShouldReturnWithoutTriggeringCallback() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));
        Member member = memberManager.getCurrentMember();

        List<OrderItem> historicalItems = new ArrayList<>();
        historicalItems.add(new OrderItem("Pepperoni (Small)", 44.0, 120, "Small", 1.0, 1));
        Order oldOrder = new Order(member.getId(), member.getName(), member.getPhone(), historicalItems);
        oldOrder.setOrderId("ORD-HISTORY-002");
        orderManager.placeOrder(oldOrder);

        AtomicBoolean callbackInvoked = new AtomicBoolean(false);
        ViewOrdersCommand command = new ViewOrdersCommand(
                member,
                orderManager,
                new Scanner(new ByteArrayInputStream("-1\n".getBytes())),
                (items, isMember) -> callbackInvoked.set(true)
        );

        String output = captureOutput(command::execute);

        assertTrue(output.contains("=== Your Recent Orders"));
        assertTrue(output.contains("[-1] Back to main menu"));
        assertFalse(callbackInvoked.get());
    }

    @Test
    void option1_LoginSuccessful_Option4SearchOrderById_ShouldShowPromptAndNotFound() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));

        SearchOrderCommand command = new SearchOrderCommand(
                true,
                orderManager,
                memberManager,
                new Scanner(new ByteArrayInputStream("ORD404\n".getBytes())));

        String output = captureOutput(command::execute);

        assertTrue(output.contains("Enter Order ID:"));
        assertTrue(output.contains("Order not found!"));
    }

    @Test
    void option1_LoginSuccessful_Option4SearchOrderById_ShouldShowOrderDetailsWhenSearchSuccessful() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));
        Member member = memberManager.getCurrentMember();

        List<OrderItem> historicalItems = new ArrayList<>();
        historicalItems.add(new OrderItem("Margherita (Small)", 42.0, 110, "Small", 1.0, 1));
        Order oldOrder = new Order(member.getId(), member.getName(), member.getPhone(), historicalItems);
        oldOrder.setOrderId("ORD-SEARCH-SUCCESS-001");
        orderManager.placeOrder(oldOrder);

        SearchOrderCommand command = new SearchOrderCommand(
                true,
                orderManager,
                memberManager,
                new Scanner(new ByteArrayInputStream("ORD-SEARCH-SUCCESS-001\n".getBytes())));

        String output = captureOutput(command::execute);

        assertTrue(output.contains("Enter Order ID:"));
        assertTrue(output.contains("=== Order Details ==="));
        assertTrue(output.contains("Order ID: ORD-SEARCH-SUCCESS-001"));
        assertTrue(output.contains("Customer: Alice"));
    }

    @Test
    void option1_LoginSuccessful_Option5ViewMemberInfo_ShouldDisplayMemberInformation() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));

        String output = captureOutput(memberManager::displayMemberInfo);

        assertTrue(output.contains("=== Member Information ==="));
        assertTrue(output.contains("ID: M001"));
        assertTrue(output.contains("Username: alice123"));
        assertTrue(output.contains("Phone: 91234567"));
    }

    @Test
    void option1_LoginSuccessful_Option6GetPizzaRecommendations_ShouldShowRecommendationFlow() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));
        Member member = memberManager.getCurrentMember();

        RecommendationService recommendationService = new RecommendationService(
                orderManager,
                MenuLoader.getInstance(),
                new Scanner(new ByteArrayInputStream("n\ny\nn\n".getBytes())));

        String output = captureOutput(() -> recommendationService.getRecommendation(member, true));

        assertTrue(output.contains("=== Pizza Recommendation ==="));
        assertTrue(output.contains("Q1. Are you vegetarian? (y/n):"));
        assertTrue(output.contains("Q2. Are you on a diet / health-conscious? (y/n):"));
        assertTrue(output.contains("=== Healthy Options ==="));
    }

    @Test
    void option1_LoginSuccessful_Option6GetPizzaRecommendations_ShouldAllowNotOrderingRecommendedPizza() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));
        Member member = memberManager.getCurrentMember();

        List<OrderItem> historicalItems = new ArrayList<>();
        historicalItems.add(new OrderItem("Margherita (Small)", 42.0, 110, "Small", 1.0, 2));
        Order oldOrder = new Order(member.getId(), member.getName(), member.getPhone(), historicalItems);
        oldOrder.setOrderId("ORD-RECO-HISTORY-001");
        orderManager.placeOrder(oldOrder);

        RecommendationService recommendationService = new RecommendationService(
                orderManager,
                MenuLoader.getInstance(),
                new Scanner(new ByteArrayInputStream("y\n1\nn\n".getBytes())));

        String output = captureOutput(() -> recommendationService.getRecommendation(member, true));

        assertTrue(output.contains("Would you like to try something similar but different? (y/n):"));
        assertTrue(output.contains("Would you like to order this pizza now? (y/n):"));
        assertTrue(output.contains("No problem! Returning to main menu."));
    }

    @Test
    void option1_LoginSuccessful_Option6GetPizzaRecommendations_ShouldAllowOrderingRecommendedPizza() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));
        Member member = memberManager.getCurrentMember();

        List<OrderItem> historicalItems = new ArrayList<>();
        historicalItems.add(new OrderItem("Margherita (Small)", 42.0, 110, "Small", 1.0, 2));
        Order oldOrder = new Order(member.getId(), member.getName(), member.getPhone(), historicalItems);
        oldOrder.setOrderId("ORD-RECO-HISTORY-002");
        orderManager.placeOrder(oldOrder);

        AtomicBoolean callbackInvoked = new AtomicBoolean(false);
        RecommendationService recommendationService = new RecommendationService(
                orderManager,
                MenuLoader.getInstance(),
                new Scanner(new ByteArrayInputStream("y\n1\ny\n".getBytes())));
        recommendationService.setCallback((pizzaName, isMember) -> callbackInvoked.set(true));

        String output = captureOutput(() -> recommendationService.getRecommendation(member, true));

        assertTrue(output.contains("Would you like to order this pizza now? (y/n):"));
        assertTrue(output.contains("Great! Let's customize your"));
        assertTrue(callbackInvoked.get());
    }

    @Test
    void option1_LoginSuccessful_Option2PlaceOrder_MinusOneAtAddNewPizza_ShouldBackToMenu() throws Exception {
        assertTrue(memberManager.login("alice123", "password123"));

        List<OrderItem> items = new ArrayList<>();
        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("-1\n".getBytes())));

        boolean added = invokePrivateAddNewPizza(items, true, null);

        assertFalse(added);
        assertTrue(items.isEmpty());
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

    private int countOccurrences(String text, String token) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(token, idx)) != -1) {
            count++;
            idx += token.length();
        }
        return count;
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

    private void invokePrivateShowMenu() {
        try {
            Field menuLoaderField = Main.class.getDeclaredField("menuLoader");
            menuLoaderField.setAccessible(true);
            menuLoaderField.set(null, MenuLoader.getInstance());

            Method method = Main.class.getDeclaredMethod("showMenu");
            method.setAccessible(true);
            method.invoke(null);
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

    private String readMainSource() {
        try {
            return Files.readString(Path.of("src/Main.java"));
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
