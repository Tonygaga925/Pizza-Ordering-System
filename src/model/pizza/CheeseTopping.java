// model/pizza/CheeseTopping.java
package model.pizza;

public class CheeseTopping extends ToppingDecorator {
    public CheeseTopping(Pizza pizza) {
        super(pizza);
    }
    
    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Cheese";
    }
    
    @Override
    public double getPrice() {
        return pizza.getPrice() + 1.50;
    }
    
    @Override
    public int getPoints() {
        return pizza.getPoints() + 10;
    }
}