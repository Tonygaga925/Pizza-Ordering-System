package model.pizza;

public class BasePizza implements Pizza {
    private String name;
    private double basePrice;
    private int basePoints;
    
    public BasePizza(String name, double basePrice, int basePoints) {
        this.name = name;
        this.basePrice = basePrice;
        this.basePoints = basePoints;
    }
    
    @Override
    public String getDescription() {
        return name;
    }
    
    @Override
    public double getPrice() {
        return basePrice;
    }
    
    @Override
    public int getPoints() {
        return basePoints;
    }
    
    public String getName() {
        return name;
    }
    
    public double getBasePrice() {
        return basePrice;
    }
    
    public int getPointsValue() {
        return basePoints;
    }
}