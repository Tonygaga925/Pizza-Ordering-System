package model.topping;

public class CheeseTopping implements Topping {
    @Override
    public String getName() {
        return "Cheese";
    }
    
    @Override
    public double getPrice() {
        return 1.50;
    }
    
    @Override
    public int getPointsValue() {
        return 10;
    }
}