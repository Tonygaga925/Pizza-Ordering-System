// model/pizza/OliveTopping.java
package model.pizza;

public class OliveTopping extends ToppingDecorator {
    public OliveTopping(Pizza pizza) {
        super(pizza);
    }
    
    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Olive";
    }
    
    @Override
    public double getPrice() {
        return pizza.getPrice() + 1.00;
    }
    
    @Override
    public int getPoints() {
        return pizza.getPoints() + 5;
    }
}