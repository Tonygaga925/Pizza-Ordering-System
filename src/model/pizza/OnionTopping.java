package model.pizza;

public class OnionTopping extends ToppingDecorator {
    
    public OnionTopping(Pizza pizza) {
        super(pizza);
    }
    
    @Override
    public String getToppingName() {
        return "Onion";
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + " + " + getToppingName();
    }
    
    @Override
    public double getPrice() {
        return pizza.getPrice() + getToppingPrice();
    }
    
    @Override
    public int getPoints() {
        return pizza.getPoints() + getToppingPoints();
    }
    
    @Override
    public double getToppingPrice() {
        return 0.80;
    }
    
    @Override
    public int getToppingPoints() {
        return 5;
    }
}