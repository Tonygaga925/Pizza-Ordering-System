package model.topping;

public class OliveTopping implements Topping {
    @Override
    public String getName() {
        return "Olive";
    }
    
    @Override
    public double getPrice() {
        return 1.00;
    }
    
    @Override
    public int getPointsValue() {
        return 5;
    }
}