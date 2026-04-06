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
        return 2.20;
    }
    
    @Override
    public int getToppingPoints() {
        return 18;
    }
}