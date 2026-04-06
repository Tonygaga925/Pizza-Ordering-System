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
        return 1.50;
    }
    
    @Override
    public int getToppingPoints() {
        return 10;
    }
    

}