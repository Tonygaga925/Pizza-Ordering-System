package model.topping;

public class SausageTopping implements Topping {
    @Override
    public String getName() {
        return "Sausage";
    }
    
    @Override
    public double getPrice() {
        return 2.20;
    }
    
    @Override
    public int getPointsValue() {
        return 18;
    }
}