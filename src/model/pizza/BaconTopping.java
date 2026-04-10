package model.pizza;

public class BaconTopping extends ToppingDecorator {
    
    public BaconTopping(Pizza pizza) {
        super(pizza);
    }
    
    @Override
    public String getToppingName() {
        return "Bacon";
    }

    @Override
    public double getToppingPrice() {
        return 15;
    }
    
    @Override
    public int getToppingPoints() {
        return 20;
    }
}