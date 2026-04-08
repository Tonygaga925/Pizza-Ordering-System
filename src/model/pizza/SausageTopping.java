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
        return 9;
    }
    
    @Override
    public int getToppingPoints() {
        return 18;
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
    
}