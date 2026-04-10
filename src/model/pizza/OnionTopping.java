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
    public double getToppingPrice() {
        return 6;
    }
    
    @Override
    public int getToppingPoints() {
        return 8;
    }
}