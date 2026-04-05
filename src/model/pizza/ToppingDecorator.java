package model.pizza;

public abstract class ToppingDecorator implements Pizza {
    protected Pizza pizza;
    
    public ToppingDecorator(Pizza pizza) {
        this.pizza = pizza;
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

    public Pizza getPizza() {
        return pizza;
    }
}