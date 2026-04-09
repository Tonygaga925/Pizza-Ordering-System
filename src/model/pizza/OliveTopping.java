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
    public double getToppingPrice() {
        return 10;
    }
    
    @Override
    public int getToppingPoints() {
        return 15;
    }
}