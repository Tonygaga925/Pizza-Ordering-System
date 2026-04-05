package model.topping;

public class ChickenTopping implements Topping {
    @Override
    public String getName() {
        return "Chicken";
    }
    
    @Override
    public double getPrice() {
        return 2.30;
    }
    
    @Override
    public int getPointsValue() {
        return 18;
    }
}