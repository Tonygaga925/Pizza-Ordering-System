package model.topping;

public class PepperoniTopping implements Topping {
    @Override
    public String getName() {
        return "Pepperoni";
    }
    
    @Override
    public double getPrice() {
        return 2.00;
    }
    
    @Override
    public int getPointsValue() {
        return 15;
    }
}