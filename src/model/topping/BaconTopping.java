package model.topping;

public class BaconTopping implements Topping {
    @Override
    public String getName() {
        return "Bacon";
    }
    
    @Override
    public double getPrice() {
        return 2.50;
    }
    
    @Override
    public int getPointsValue() {
        return 20;
    }
}