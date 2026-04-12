package model.order;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Order {
    private String orderId;
    private String memberId;
    private String customerName;
    private String phone;
    private List<OrderItem> items = new java.util.ArrayList<>();
    private double originalTotal;
    private double finalTotal;
    private double discountApplied;
    private int totalPoints;
    private String timestamp;
    private String status = "Processing";
    private transient OrderStatus orderStatus;
    private double discountRate = 0;
    private String couponCode = "";
    private double couponDiscountAmount = 0;

    public Order() {
        // Default constructor for Gson
    }

    public void initStatus() {
        this.orderStatus = OrderStatusFactory.createStatus(this.status);
    }
    
    public Order(String memberId, String customerName, String phone, List<OrderItem> items) {
        this.memberId = memberId;
        this.customerName = customerName;
        this.phone = phone;
        this.items = items;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.timestamp = LocalDateTime.now().format(formatter);
        this.status = "Processing";
        this.orderStatus = new ProcessingState();
        this.discountRate = 0;
        calculateTotals();
    }
    
    public void calculateTotals() {
        this.originalTotal = 0;
        this.totalPoints = 0;
        for (OrderItem item : items) {
            this.originalTotal += item.getItemTotal();
            this.totalPoints += item.getItemPoints();
        }
        this.discountApplied = this.originalTotal * this.discountRate; // VIP Discount
        double priceAfterVIP = this.originalTotal - this.discountApplied;

        if (this.couponCode != null && !this.couponCode.isEmpty()) {
            try {
                java.util.Map<String, Coupon> coupons = service.CouponManager.getInstance().getAllCoupons();
                Coupon c = coupons.get(this.couponCode.toUpperCase());
                if (c != null) {
                    // Bypass isActive() check for historical scale
                    if (c.getType() == Coupon.Type.PERCENTAGE) {
                        this.couponDiscountAmount = priceAfterVIP * c.getValue();
                    } else if (c.getType() == Coupon.Type.FIXED) {
                        this.couponDiscountAmount = Math.min(c.getValue(), priceAfterVIP);
                    }
                }
            } catch (Exception e) {
                // Fallback to original hardcoded amount if Manager loading fails
            }
        }

        this.finalTotal = priceAfterVIP - this.couponDiscountAmount; 
        if (this.finalTotal < 0) {
            this.finalTotal = 0;
        }
    }

    public void applyCoupon(Coupon coupon) {
        if (coupon != null) {
            this.couponCode = coupon.getCode();
            calculateTotals();
        }
    }
    
    public void applyDiscount(double discountRate) {
        this.discountRate = discountRate;
        calculateTotals();
    }
    
    // Getters and setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getMemberId() { return memberId; }
    public String getCustomerName() { return customerName; }
    public String getPhone() { return phone; }
    public List<OrderItem> getItems() { return items; }
    public double getOriginalTotal() { return originalTotal; }
    public double getFinalTotal() { return finalTotal; }
    public double getDiscountApplied() { return discountApplied; }
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int points) { this.totalPoints = points; }
    public String getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status){
        this.status = status;
        this.orderStatus = OrderStatusFactory.createStatus(status);
    }

    public OrderStatus getOrderStatus() {
        if (orderStatus == null) {
            initStatus();
        }
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        this.status = orderStatus.getStatusName();
    }
    public String getCouponCode() { return couponCode; }
    public double getCouponDiscountAmount() { return couponDiscountAmount; }


}