package model.pizza;

public class MutablePizzaWrapper implements Pizza {
    private Pizza pizza;
    
    // add pizza price with size multiplier
    public MutablePizzaWrapper(Pizza p, double multiplier) {
        this.pizza = new BasePizza(p.getDescription(), p.getPrice() * multiplier, (int) (p.getPoints() * multiplier));
    }

    @Override
    public String getDescription() {
        return pizza.getDescription();
    }

    @Override
    public double getPrice() {
        return pizza.getPrice();
    }

    @Override
    public int getPoints() {
        return pizza.getPoints();
    }

    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }

    public Pizza getPizza() {
        return pizza;
    }

}