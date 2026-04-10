package model.pizza;

public class SausageTopping extends ToppingDecorator {
    
    public SausageTopping(Pizza pizza) {
        super(pizza);
    }
    
    @Override
    public String getToppingName() {
        return "Sausage";
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