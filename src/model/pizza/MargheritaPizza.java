package model.pizza;

public class MargheritaPizza extends Pizza {
    public MargheritaPizza() {
        super("Margherita", 12.99, 100);
    }
    
    @Override
    public String getDescription() {
        return name;
    }
}