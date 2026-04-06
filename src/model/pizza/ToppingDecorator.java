package model.pizza;

public abstract class ToppingDecorator extends Pizza {
    protected Pizza pizza;
    
    public ToppingDecorator(Pizza pizza) {
        super(pizza.getName(), pizza.getBasePrice(), pizza.getBasePoints());
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

    public abstract String getToppingName();
    public abstract double getToppingPrice();
    public abstract int getToppingPoints();

    public Pizza getPizza() {
        return pizza;
    }
}