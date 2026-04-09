package model.pizza;

public abstract class BasePizza implements Pizza {
    protected String name;
    protected double price;
    protected int points;
    
    public BasePizza(String name, double price, int points) {
        this.name = name;
        this.price = price;
        this.points = points;
    }
    
    @Override
    public String getDescription() {
        return name;
    }
    
    @Override
    public double getPrice() {
        return price;
    }
    
    @Override
    public int getPoints() {
        return points;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    public void setPoints(int points) {
        this.points = points;
    }
    
}