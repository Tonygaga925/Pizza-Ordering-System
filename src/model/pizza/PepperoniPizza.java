package model.pizza;

public class PepperoniPizza extends Pizza {
    public PepperoniPizza() {
        super("Pepperoni", 14.99, 120);
    }
    
    @Override
    public String getDescription() {
        return name;
    }
}