// model/pizza/BaconTopping.java
package model.pizza;

public class BaconTopping extends ToppingDecorator {
    public BaconTopping(Pizza pizza) {
        super(pizza);
    }
    
    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Bacon";
    }
    
    @Override
    public double getPrice() {
        return pizza.getPrice() + 2.50;
    }
    
    @Override
    public int getPoints() {
        return pizza.getPoints() + 20;
    }
}