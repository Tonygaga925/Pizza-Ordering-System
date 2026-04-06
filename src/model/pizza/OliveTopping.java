package model.pizza;

public class OliveTopping extends ToppingDecorator {
    
    public OliveTopping(Pizza pizza) {
        super(pizza);
    }
    
    @Override
    public String getToppingName() {
        return "Olive";
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
        return 1.00;
    }
    
    @Override
    public int getToppingPoints() {
        return 5;
    }
}