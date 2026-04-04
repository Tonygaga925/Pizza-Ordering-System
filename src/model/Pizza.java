package model;

import java.util.List;

public class Pizza {
    private String name;
    private double basePrice;
    private List<String> toppings;

    public Pizza(String name, double basePrice, List<String> toppings) {
        this.name = name;
        this.basePrice = basePrice;
        this.toppings = toppings;
    }

    public String getName() { return name; }
    public double getBasePrice() { return basePrice; }
    public List<String> getToppings() { return toppings; }
}