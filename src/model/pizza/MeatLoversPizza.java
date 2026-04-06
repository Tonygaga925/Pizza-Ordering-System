package model.pizza;

public class MeatLoversPizza extends Pizza {
    public MeatLoversPizza() {
        super("Meat Lovers", 16.99, 150);
    }
    
    @Override
    public String getDescription() {
        return name;
    }
}