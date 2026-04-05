package model;

import java.util.List;
import model.topping.*;

public class Pizza {
    private String name;
    private double basePrice;
    private int pointsValue;
    
    // Constructor
    public Pizza(String name, double basePrice, int pointsValue) {
        this.name = name;
        this.basePrice = basePrice;
        this.pointsValue = pointsValue;
    }
    
    // Getters
    public String getName() { return name; }
    public double getBasePrice() { return basePrice; }
    public int getPointsValue() { return pointsValue; }
    
    @Override
    public String toString() {
        return String.format("%s - $%.2f (%d points)", name, basePrice, pointsValue);
    }
}