import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import controller.EmployeeController;
import model.command.CommandFactory;
import model.command.EmployeeLoginCommand;
import model.command.RegisterEmployeeCommand;
import model.employee.Employee;
import model.order.Coupon;
import model.order.Order;
import model.order.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import service.CouponManager;
import service.EmployeeManager;
import service.MemberManager;
import service.MenuLoader;
import service.OrderManager;
import view.EmployeeView;
import view.InputView;

public class Option5EmployeeLoginCommandTest {

    private EmployeeManager employeeManager;
    private OrderManager orderManager;
    private String couponsBackup;

    @BeforeEach
    void setUp() throws Exception {
        resetEmployeeManagerSingleton();
        resetMemberManagerSingleton();
        resetCommandFactorySingleton();
        resetCouponManagerSingleton();

        Path tempStaffFile = Files.createTempFile("staff-option5-", ".json");
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

        Path tempOrdersFile = Files.createTempFile("orders-option5-", ".json");
        Files.writeString(tempOrdersFile, "{}");

        Path tempMembersFile = Files.createTempFile("members-option5-", ".json");
        Files.writeString(tempMembersFile, "{}");

        employeeManager = EmployeeManager.getInstance(tempStaffFile.toString());
        resetOrderManagerSingleton();
        orderManager = OrderManager.getInstance(tempOrdersFile.toString());

        MemberManager memberManager = MemberManager.getInstance(tempMembersFile.toString());
        CommandFactory.initialize(memberManager, orderManager, employeeManager, MenuLoader.getInstance(), InputView.getScanner());

        Path couponsPath = Path.of("data/coupons.json");
        couponsBackup = Files.readString(couponsPath);
    }

    @AfterEach
    void tearDown() throws Exception {
        InputView.resetScanner();
        Path couponsPath = Path.of("data/coupons.json");
        Files.writeString(couponsPath, couponsBackup);
        resetCouponManagerSingleton();
    }

    @Test
    void option5_EmployeeLoginSuccessful_ShouldLoginWithValidCredentials() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("jdoe_staff\npassword123\n".getBytes()));
        EmployeeLoginCommand command = new EmployeeLoginCommand(employeeManager, scanner);

        String output = executeAndCaptureOutput(command);

        assertTrue(employeeManager.isLoggedIn());
        assertEquals("jdoe_staff", employeeManager.getCurrentEmployee().getUsername());
        assertTrue(output.contains("--- Employee Login (-1 to go back to previous step) ---"));
        assertTrue(output.contains("Username:"));
        assertTrue(output.contains("Password:"));
        assertTrue(output.contains("Employee login successful! Welcome, John"));
    }

    @Test
    void option5_EmployeeLoginFailed_ShouldRejectInvalidCredentials() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("jdoe_staff\nwrong-pass\n".getBytes()));
        EmployeeLoginCommand command = new EmployeeLoginCommand(employeeManager, scanner);

        String output = executeAndCaptureOutput(command);

        assertFalse(employeeManager.isLoggedIn());
        assertTrue(output.contains("--- Employee Login (-1 to go back to previous step) ---"));
        assertTrue(output.contains("Username:"));
        assertTrue(output.contains("Password:"));
        assertTrue(output.contains("Invalid employee username or password!"));
    }

    @Test
    void option5_EmployeeLoginMinusOneAtUsername_ShouldGoBack() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("-1\n".getBytes()));
        EmployeeLoginCommand command = new EmployeeLoginCommand(employeeManager, scanner);

        String output = executeAndCaptureOutput(command);

        assertFalse(employeeManager.isLoggedIn());
        assertTrue(output.contains("Username:"));
        assertFalse(output.contains("Password:"));
    }

    @Test
    void option5_EmployeeLoginMinusOneAtPassword_ShouldGoBackToUsername() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("jdoe_staff\n-1\njdoe_staff\npassword123\n".getBytes()));
        EmployeeLoginCommand command = new EmployeeLoginCommand(employeeManager, scanner);

        String output = executeAndCaptureOutput(command);

        assertTrue(employeeManager.isLoggedIn());
        assertTrue(countOccurrences(output, "Username:") >= 2);
        assertTrue(countOccurrences(output, "Password:") >= 2);
    }

    @Test
    void option5_StaffMenu_ShouldDisplayAllSubOptions() {
        String output = executeAndCaptureOutput(() -> EmployeeView.displayStaffMenu("John"));

        assertTrue(output.contains("=== Employee Menu (Staff) ==="));
        assertTrue(output.contains("1. View Order"));
        assertTrue(output.contains("2. Search Order"));
        assertTrue(output.contains("3. Cancel Processing Order"));
        assertTrue(output.contains("4. Logout"));
    }

    @Test
    void option5_ManagerMenu_ShouldDisplayAllSubOptions() {
        String output = executeAndCaptureOutput(() -> EmployeeView.displayManagerMenu("Mary"));

        assertTrue(output.contains("=== Employee Menu (Manager) ==="));
        assertTrue(output.contains("1. View Order"));
        assertTrue(output.contains("2. Search Order"));
        assertTrue(output.contains("3. Cancel Processing Order"));
        assertTrue(output.contains("4. Manage Coupons"));
        assertTrue(output.contains("5. Access Admin Panel"));
        assertTrue(output.contains("6. Logout"));
    }

    @Test
    void option5_StaffMenu_LogoutSubOption_ShouldReturnOne() {
        Employee staff = new Employee("S001", "s", "p", "Staff", true, "normal");
        int result = EmployeeController.handleMenuChoice(staff, 4, orderManager);
        assertEquals(1, result);
    }

    @Test
    void option5_ManagerMenu_AccessAdminSubOption_ShouldReturnTwo() {
        Employee manager = new Employee("S002", "m", "p", "Manager", true, "manager");
        int result = EmployeeController.handleMenuChoice(manager, 5, orderManager);
        assertEquals(2, result);
    }

    @Test
    void option5_ManagerMenu_LogoutSubOption_ShouldReturnOne() {
        Employee manager = new Employee("S002", "m", "p", "Manager", true, "manager");
        int result = EmployeeController.handleMenuChoice(manager, 6, orderManager);
        assertEquals(1, result);
    }

    @Test
    void option5_EmployeeMenuInvalidChoice_ShouldShowInvalidMessage() {
        Employee staff = new Employee("S001", "s", "p", "Staff", true, "normal");
        String output = executeAndCaptureOutput(() -> EmployeeController.handleMenuChoice(staff, 99, orderManager));
        assertTrue(output.contains("Invalid choice!"));
    }

    @Test
    void option5_EmployeeMenuViewOrderSubOption_ShouldHandleNoProcessingOrders() {
        Employee staff = new Employee("S001", "s", "p", "Staff", true, "normal");
        String output = executeAndCaptureOutput(() -> EmployeeController.handleMenuChoice(staff, 1, orderManager));
        assertTrue(output.contains("No processing orders found."));
    }

    @Test
    void option5_EmployeeMenuViewOrderSubOption_ShouldFinishOrderSuccessfully() throws Exception {
        Employee staff = new Employee("S001", "s", "p", "Staff", true, "normal");

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Margherita (Small)", 42.0, 110, "Small", 1.0, 1));
        Order order = new Order(null, "Guest", "98765432", items);
        order.setOrderId("ORD-HANDLE-001");
        orderManager.placeOrder(order);

        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("y\n".getBytes())));
        String output = executeAndCaptureOutput(() -> EmployeeController.handleMenuChoice(staff, 1, orderManager));

        Order updated = orderManager.getOrderById("ORD-HANDLE-001");
        assertEquals("Completed", updated.getStatus());
        assertTrue(output.contains("There are currently 1 orders in processing."));
        assertTrue(output.contains("Type 'y' to finish this order"));
        assertTrue(output.contains("has been marked as Completed."));
    }

    @Test
    void option5_EmployeeMenuViewOrderSubOption_ShouldNotFinishOrderWhenChooseN() throws Exception {
        Employee staff = new Employee("S001", "s", "p", "Staff", true, "normal");

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Pepperoni (Small)", 44.0, 120, "Small", 1.0, 1));
        Order order = new Order(null, "Guest", "98765432", items);
        order.setOrderId("ORD-HANDLE-002");
        orderManager.placeOrder(order);

        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("n\n".getBytes())));
        String output = executeAndCaptureOutput(() -> EmployeeController.handleMenuChoice(staff, 1, orderManager));

        Order updated = orderManager.getOrderById("ORD-HANDLE-002");
        assertEquals("Handling", updated.getStatus());
        assertTrue(output.contains("Type 'y' to finish this order"));
        assertTrue(output.contains("Order status remains no change."));
    }

    @Test
    void option5_EmployeeMenuSearchOrderSubOption_ShouldShowOrderNotFound() {
        Employee staff = new Employee("S001", "s", "p", "Staff", true, "normal");
        view.InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("ORD404\n".getBytes())));

        String output = executeAndCaptureOutput(() -> EmployeeController.handleMenuChoice(staff, 2, orderManager));

        assertTrue(output.contains("Enter the Order ID to search:"));
        assertTrue(output.contains("Order not found."));
        view.InputView.resetScanner();
    }

    @Test
    void option5_EmployeeMenuSearchOrderSubOption_ShouldShowOrderDetailsWhenSearchSuccessful() throws Exception {
        Employee staff = new Employee("S001", "s", "p", "Staff", true, "normal");

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Hawaiian (Small)", 43.0, 110, "Small", 1.0, 1));
        Order order = new Order(null, "Guest", "92345678", items);
        order.setOrderId("ORD-SEARCH-OK-001");
        orderManager.placeOrder(order);

        view.InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("ORD-SEARCH-OK-001\n".getBytes())));

        String output = executeAndCaptureOutput(() -> EmployeeController.handleMenuChoice(staff, 2, orderManager));

        assertTrue(output.contains("Enter the Order ID to search:"));
        assertTrue(output.contains("=== Order Details ==="));
        assertTrue(output.contains("Order ID: ORD-SEARCH-OK-001"));
        assertTrue(output.contains("Customer: Guest"));
        view.InputView.resetScanner();
    }

    @Test
    void option5_EmployeeMenuCancelOrderSubOption_ShouldHandleNoProcessingOrders() {
        Employee staff = new Employee("S001", "s", "p", "Staff", true, "normal");
        String output = executeAndCaptureOutput(() -> EmployeeController.handleMenuChoice(staff, 3, orderManager));
        assertTrue(output.contains("No processing orders available to cancel."));
    }

    @Test
    void option5_ManagerMenu_CancelProcessingOrder_ShouldCancelSuccessfully() throws Exception {
        Employee manager = new Employee("S002", "m", "p", "Manager", true, "manager");

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Margherita (Small)", 42.0, 110, "Small", 1.0, 1));
        Order order = new Order(null, "Guest", "98765432", items);
        order.setOrderId("ORD-CANCEL-OK-001");
        orderManager.placeOrder(order);

        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("1\ny\n".getBytes())));
        String output = executeAndCaptureOutput(() -> EmployeeController.cancelOrder(manager, orderManager));

        Order updated = orderManager.getOrderById("ORD-CANCEL-OK-001");
        assertEquals("Cancelled", updated.getStatus());
        assertTrue(output.contains("=== Cancel Processing Order ==="));
        assertTrue(output.contains("has been marked as Cancelled."));
    }

    @Test
    void option5_ManagerMenu_CancelProcessingOrder_ShouldNotCancelWhenUserChooseN() throws Exception {
        Employee manager = new Employee("S002", "m", "p", "Manager", true, "manager");

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Hawaiian (Small)", 43.0, 110, "Small", 1.0, 1));
        Order order = new Order(null, "Guest", "98765432", items);
        order.setOrderId("ORD-CANCEL-NO-001");
        orderManager.placeOrder(order);

        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("1\nn\n".getBytes())));
        String output = executeAndCaptureOutput(() -> EmployeeController.cancelOrder(manager, orderManager));

        Order updated = orderManager.getOrderById("ORD-CANCEL-NO-001");
        assertEquals("Processing", updated.getStatus());
        assertTrue(output.contains("Cancellation aborted."));
    }

    @Test
    void option5_ManagerOption4_ManageCouponsCase1_AddFixedDiscountCoupon_ShouldAddSuccessfully() {
        Employee manager = new Employee("S002", "m", "p", "Manager", true, "manager");
        String code = "FIX" + System.currentTimeMillis() % 100000;

        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream(("1\n" + code + "\n50\n-1\n").getBytes())));
        String output = executeAndCaptureOutput(() -> EmployeeController.handleMenuChoice(manager, 4, orderManager));

        Coupon c = CouponManager.getInstance().getAllCoupons().get(code);
        assertNotNull(c);
        assertEquals(Coupon.Type.FIXED, c.getType());
        assertEquals(50.0, c.getValue());
        assertTrue(output.contains("Add Fixed Discount Coupon"));
        assertTrue(output.contains("added successfully."));
    }

    @Test
    void option5_ManagerOption4_ManageCouponsCase2_AddPercentageDiscountCoupon_ShouldAddSuccessfully() {
        Employee manager = new Employee("S002", "m", "p", "Manager", true, "manager");
        String code = "PCT" + System.currentTimeMillis() % 100000;

        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream(("2\n" + code + "\n20\n-1\n").getBytes())));
        String output = executeAndCaptureOutput(() -> EmployeeController.handleMenuChoice(manager, 4, orderManager));

        Coupon c = CouponManager.getInstance().getAllCoupons().get(code);
        assertNotNull(c);
        assertEquals(Coupon.Type.PERCENTAGE, c.getType());
        assertEquals(0.2, c.getValue());
        assertTrue(output.contains("Add Percentage Discount Coupon"));
        assertTrue(output.contains("added successfully."));
    }

    @Test
    void option5_ManagerOption4_ManageCouponsCase3_ToggleCouponStatus_ShouldToggleSuccessfully() throws Exception {
        Employee manager = new Employee("S002", "m", "p", "Manager", true, "manager");
        String code = "TGL" + System.currentTimeMillis() % 100000;

        CouponManager.getInstance().addCoupon(new Coupon(code, Coupon.Type.FIXED, 30));
        boolean oldStatus = CouponManager.getInstance().getAllCoupons().get(code).isActive();

        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream(("3\n" + code + "\n-1\n").getBytes())));
        String output = executeAndCaptureOutput(() -> EmployeeController.handleMenuChoice(manager, 4, orderManager));

        boolean newStatus = CouponManager.getInstance().getAllCoupons().get(code).isActive();
        assertNotEquals(oldStatus, newStatus);
        assertTrue(output.contains("Toggle Coupon Status (Enable/Disable)"));
        assertTrue(output.contains("is now"));
    }

    @Test
    void option5_ManagerOption5_AccessAdminPanelCase1_CreateStaff_ShouldCreateSuccessfully() {
        Employee manager = new Employee("S002", "m", "p", "Manager", true, "manager");
        assertEquals(2, EmployeeController.handleMenuChoice(manager, 5, orderManager));

        String username = "staff" + System.currentTimeMillis() % 100000;
        RegisterEmployeeCommand cmd = new RegisterEmployeeCommand(employeeManager, new Scanner(new ByteArrayInputStream((username + "\npass123\nNew Staff\n").getBytes())));

        String output = executeAndCaptureOutput(cmd::execute);
        assertTrue(output.contains("Create Staff Account"));
        assertTrue(output.contains("created successfully."));
        assertTrue(employeeManager.login(username, "pass123"));
    }

    @Test
    void option5_ManagerOption5_AccessAdminPanelCase2_EditProcessingOrder_ShouldSaveAndExit() throws Exception {
        Employee manager = new Employee("S002", "m", "p", "Manager", true, "manager");
        assertEquals(2, EmployeeController.handleMenuChoice(manager, 5, orderManager));

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("BBQ Chicken (Small)", 55.0, 140, "Small", 1.0, 1));
        Order order = new Order(null, "Guest", "98765432", items);
        order.setOrderId("ORD-EDIT-001");
        orderManager.placeOrder(order);

        InputView.setScannerForTesting(new Scanner(new ByteArrayInputStream("1\n3\n".getBytes())));
        String output = executeAndCaptureOutput(() -> EmployeeController.editProcessingOrder(manager, orderManager));

        assertTrue(output.contains("=== Edit Processing Order ==="));
        assertTrue(output.contains("=== Editing Order: ORD-EDIT-001 ==="));
        assertTrue(output.contains("Order updated successfully!"));
    }

    private int countOccurrences(String text, String token) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(token, index)) != -1) {
            count++;
            index += token.length();
        }
        return count;
    }

    private String executeAndCaptureOutput(EmployeeLoginCommand command) {
        return executeAndCaptureOutput((Runnable) command::execute);
    }

    private String executeAndCaptureOutput(Runnable runnable) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output));
            runnable.run();
        } finally {
            System.setOut(originalOut);
        }
        return output.toString();
    }

    private void resetEmployeeManagerSingleton() throws Exception {
        Field instanceField = EmployeeManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    private void resetMemberManagerSingleton() throws Exception {
        Field instanceField = MemberManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    private void resetCouponManagerSingleton() throws Exception {
        Field instanceField = CouponManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    private void resetCommandFactorySingleton() throws Exception {
        Field instanceField = CommandFactory.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    private void resetOrderManagerSingleton() throws Exception {
        Field instanceField = OrderManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }
}
