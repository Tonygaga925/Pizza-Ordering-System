package model.pizza;

public class BBQChickenPizza extends Pizza {
    public BBQChickenPizza() {
        super("BBQ Chicken", 15.99, 140);
    }
    
    @Override
    public String getDescription() {
        return name;
    }
}