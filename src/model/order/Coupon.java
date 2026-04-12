package model.order;

public class Coupon {
    public enum Type {
        FIXED,
        PERCENTAGE
    }

    private String code;
    private Type type;
    private double value;
    private boolean isActive;

    public Coupon(String code, Type type, double value) {
        this.code = code;
        this.type = type;
        this.value = value;
        this.isActive = true;
    }

    public String getCode() { return code; }
    public Type getType() { return type; }
    public double getValue() { return value; }
    public boolean isActive() { return isActive; }

    public void setActive(boolean active) { this.isActive = active; }

    public double calculateDiscount(double amount) {
        if (!isActive) return 0;
        if (type == Type.FIXED) {
            return Math.min(value, amount);
        } else if (type == Type.PERCENTAGE) {
            return amount * value;
        }
        return 0;
    }
}
