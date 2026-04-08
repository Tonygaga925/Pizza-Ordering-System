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
        return 9;
    }
    
    @Override
    public int getToppingPoints() {
        return 10;
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