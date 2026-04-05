// model/pizza/OnionTopping.java
package model.pizza;

public class OnionTopping extends ToppingDecorator {
    public OnionTopping(Pizza pizza) {
        super(pizza);
    }
    
    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Onion";
    }
    
    @Override
    public double getPrice() {
        return pizza.getPrice() + 0.80;
    }
    
    @Override
    public int getPoints() {
        return pizza.getPoints() + 5;
    }
}