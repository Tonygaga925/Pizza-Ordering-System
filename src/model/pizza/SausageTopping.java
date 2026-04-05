// model/pizza/SausageTopping.java
package model.pizza;

public class SausageTopping extends ToppingDecorator {
    public SausageTopping(Pizza pizza) {
        super(pizza);
    }
    
    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Sausage";
    }
    
    @Override
    public double getPrice() {
        return pizza.getPrice() + 2.20;
    }
    
    @Override
    public int getPoints() {
        return pizza.getPoints() + 18;
    }
}