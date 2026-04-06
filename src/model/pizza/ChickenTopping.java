package model.pizza;

public class ChickenTopping extends ToppingDecorator {

    public ChickenTopping(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getToppingName() {
        return "Chicken";
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + " + " + getToppingName();
    }

    @Override
    public double getPrice() {
        return pizza.getPrice() + getToppingPrice();
    }

    @Override
    public int getPoints() {
        return pizza.getPoints() + getToppingPoints();
    }

    @Override
    public double getToppingPrice() {
        return 2.30;
    }

    @Override
    public int getToppingPoints() {
        return 18;
    }
}