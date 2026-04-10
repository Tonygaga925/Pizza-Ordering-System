package model.pizza;

public class CheeseTopping extends ToppingDecorator {
    
    public CheeseTopping(Pizza pizza) {
        super(pizza);
    }
    
    @Override
    public String getToppingName() {
        return "Cheese";
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