package model.pizza;

public class MutablePizzaWrapper implements Pizza {
    private Pizza pizza;
    
    public MutablePizzaWrapper(Pizza pizza) {
        this.pizza = pizza;
    }
    
    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }
    
    public Pizza getPizza() {
        return pizza;
    }
    
    @Override
    public String getDescription() {
        return pizza.getDescription();
    }
    
    @Override
    public double getPrice() {
        return pizza.getPrice();
    }
    
    @Override
    public int getPoints() {
        return pizza.getPoints();
    }
}