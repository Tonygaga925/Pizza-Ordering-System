package model.pizza;

public class BasePizza extends Pizza {
    
    public BasePizza(String name, double basePrice, int basePoints) {
        super(name, basePrice, basePoints);
    }
    
    @Override
    public String getDescription() {
        return name;
    }
}