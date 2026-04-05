package model.topping;

public class MushroomTopping implements Topping {
    @Override
    public String getName() {
        return "Mushroom";
    }
    
    @Override
    public double getPrice() {
        return 1.20;
    }
    
    @Override
    public int getPointsValue() {
        return 8;
    }
}