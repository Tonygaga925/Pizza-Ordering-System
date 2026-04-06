package model.pizza;

public class VegetarianPizza extends Pizza {
    public VegetarianPizza() {
        super("Vegetarian", 13.49, 105);
    }
    
    @Override
    public String getDescription() {
        return name;
    }
}