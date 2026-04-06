package model.pizza;

public class HawaiianPizza extends Pizza {
    public HawaiianPizza() {
        super("Hawaiian", 13.99, 110);
    }
    
    @Override
    public String getDescription() {
        return name;
    }
}