package model.pizza;

public abstract class Pizza {
    protected String name;
    protected double basePrice;
    protected int basePoints;
    
    public Pizza(String name, double basePrice, int basePoints) {
        this.name = name;
        this.basePrice = basePrice;
        this.basePoints = basePoints;
    }
    
    public abstract String getDescription();
    
    public double getPrice() {
        return basePrice;
    }
    
    public int getPoints() {
        return basePoints;
    }
    
    public String getName() {
        return name;
    }
    
    public double getBasePrice() {
        return basePrice;
    }
    
    public int getBasePoints() {
        return basePoints;
    }
}