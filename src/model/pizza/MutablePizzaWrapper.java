package model.pizza;

public class MutablePizzaWrapper implements Pizza {
    private Pizza pizza;
    // add pizza price with size multiplier
    public MutablePizzaWrapper(Pizza pizza, double multiplier) {
        this.pizza = new BasePizza(pizza.getDescription(), pizza.getPrice() * multiplier,
                (int) (pizza.getPoints() * multiplier));
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