package model.pizza;

public class ChickenTopping extends ToppingDecorator {
    
    public ChickenTopping(Pizza pizza) {
        super(pizza);
    }
    
    @Override
    public String getToppingName() {
        return "Chicken";
    }

    @Override
    public double getToppingPrice() {
        return 15;
    }
    
    @Override
    public int getToppingPoints() {
        return 22;
    }
}