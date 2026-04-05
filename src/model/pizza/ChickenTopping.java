// model/pizza/ChickenTopping.java
package model.pizza;

public class ChickenTopping extends ToppingDecorator {
    public ChickenTopping(Pizza pizza) {
        super(pizza);
    }
    
    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Chicken";
    }
    
    @Override
    public double getPrice() {
        return pizza.getPrice() + 2.30;
    }
    
    @Override
    public int getPoints() {
        return pizza.getPoints() + 18;
    }
}