package model;

import java.util.List;

public class Pizza {
    private String name;
    private double basePrice;
    private List<String> toppings;
    private int pointsValue;
    
    // Constructor
    public Pizza(String name, double basePrice, List<String> toppings, int pointsValue) {
        this.name = name;
        this.basePrice = basePrice;
        this.toppings = toppings;
        this.pointsValue = pointsValue;
    }
    
    // Getters
    public String getName() { return name; }
    public double getBasePrice() { return basePrice; }
    public List<String> getToppings() { return toppings; }
    public int getPointsValue() { return pointsValue; }
    
    @Override
    public String toString() {
        return String.format("%s - $%.2f (%d points)", name, basePrice, pointsValue);
    }
}