package model.command;

import model.order.Coupon;
import service.CouponManager;
import view.InputView;

import java.io.IOException;
import java.util.Map;

public class ManageCouponsCommand implements Command {
    private final CouponManager couponManager;

    public ManageCouponsCommand(CouponManager couponManager) {
        this.couponManager = couponManager;
    }

    @Override
    public void execute() {
        boolean managing = true;
        while (managing) {
            displayCoupons();
            System.out.println("\n--- Coupon Management ---");
            System.out.println("1. Add Fixed Discount Coupon");
            System.out.println("2. Add Percentage Discount Coupon");
            System.out.println("3. Toggle Coupon Status (Enable/Disable)");
            System.out.println("0. Back to Employee Menu");

            String choice = InputView.getStringInput("Choose: ");
            try {
                switch (choice) {
                    case "1":
                        addFixedCoupon();
                        break;
                    case "2":
                        addPercentageCoupon();
                        break;
                    case "3":
                        toggleCoupon();
                        break;
                    case "0":
                        managing = false;
                        break;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (IOException e) {
                System.out.println("Error saving changes: " + e.getMessage());
            }
        }
    }

    private void displayCoupons() {
        Map<String, Coupon> coupons = couponManager.getAllCoupons();
        System.out.println("\n=== Current Coupons ===");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-15s | %-12s | %-10s | %-8s%n", "Code", "Type", "Value", "Status");
        System.out.println("--------------------------------------------------");
        
        for (Coupon c : coupons.values()) {
            String valueStr = c.getType() == Coupon.Type.FIXED 
                ? String.format("$%.2f", c.getValue()) 
                : String.format("%.0f%%", c.getValue() * 100);
            
            String status = c.isActive() ? "ACTIVE" : "INACTIVE";
            
            System.out.printf("%-15s | %-12s | %-10s | %-8s%n", 
                c.getCode(), c.getType(), valueStr, status);
        }
        System.out.println("--------------------------------------------------");
    }

    private void addFixedCoupon() throws IOException {
        String code = InputView.getStringInput("Enter new coupon code: ").toUpperCase();
        if (couponManager.getAllCoupons().containsKey(code)) {
            System.out.println("Coupon already exists!");
            return;
        }
        double amount = InputView.getIntInput("Enter discount amount ($): ");
        if (amount <= 0) {
            System.out.println("Invalid amount!");
            return;
        }
        
        Coupon coupon = new Coupon(code, Coupon.Type.FIXED, amount);
        couponManager.addCoupon(coupon);
        System.out.println("Fixed discount coupon '" + code + "' added successfully.");
    }

    private void addPercentageCoupon() throws IOException {
        String code = InputView.getStringInput("Enter new coupon code: ").toUpperCase();
        if (couponManager.getAllCoupons().containsKey(code)) {
            System.out.println("Coupon already exists!");
            return;
        }
        int percent = InputView.getIntInput("Enter discount percentage (e.g., 20 for 20%): ");
        if (percent <= 0 || percent > 100) {
            System.out.println("Invalid percentage!");
            return;
        }
        
        Coupon coupon = new Coupon(code, Coupon.Type.PERCENTAGE, percent / 100.0);
        couponManager.addCoupon(coupon);
        System.out.println("Percentage discount coupon '" + code + "' added successfully.");
    }

    private void toggleCoupon() throws IOException {
        String code = InputView.getStringInput("Enter coupon code to toggle: ").toUpperCase();
        if (!couponManager.getAllCoupons().containsKey(code)) {
            System.out.println("Coupon not found.");
            return;
        }
        
        couponManager.toggleCouponStatus(code);
        Coupon c = couponManager.getAllCoupons().get(code);
        String status = c.isActive() ? "ENABLED" : "DISABLED";
        System.out.println("Coupon '" + code + "' is now " + status + ".");
    }

    @Override
    public void undo() {}

    @Override
    public String getDescription() {
        return "Manage Coupons";
    }
}
