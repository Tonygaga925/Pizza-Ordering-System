package model.topping;

public class OnionTopping implements Topping {
    @Override
    public String getName() {
        return "Onion";
    }
    
    @Override
    public double getPrice() {
        return 0.80;
    }
    
    @Override
    public int getPointsValue() {
        return 5;
    }
}