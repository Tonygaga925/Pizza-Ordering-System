package model.order;

import java.util.ArrayList;
import java.util.List;

public class OrderItem {

    private String pizzaDescription;  // pizza description (includes all toppings)
    private double pizzaPrice;         // total price for one pizza
    private int pizzaPoints;           // total points for one pizza
    private String sizeName;           // size name (e.g., "Large")
    private double sizeMultiplier;     // size multiplier (e.g., 1.6)
    private int quantity;

    // No-arg constructor for Gson
    public OrderItem() {
    }

    public OrderItem(String pizzaDescription, double pizzaPrice, int pizzaPoints,
            String sizeName, int quantity) {
        this.pizzaDescription = pizzaDescription;
        this.pizzaPrice = pizzaPrice;
        this.pizzaPoints = pizzaPoints;
        this.sizeName = sizeName;
        this.quantity = quantity;
    }

    // Constructor for creating new order items
    public OrderItem(String pizzaDescription, double pizzaPrice, int pizzaPoints,
            String sizeName, double sizeMultiplier, int quantity) {
        this.pizzaDescription = pizzaDescription;
        this.pizzaPrice = pizzaPrice;
        this.pizzaPoints = pizzaPoints;
        this.sizeName = sizeName;
        this.sizeMultiplier = sizeMultiplier;
        this.quantity = quantity;
    }

    // Getters
    public String getPizzaDescription() {
        return pizzaDescription;
    }

    public double getPizzaPrice() {
        return pizzaPrice;
    }

    public int getPizzaPoints() {
        return pizzaPoints;
    }

    public String getSizeName() {
        return sizeName;
    }

    public double getSizeMultiplier() {
        return sizeMultiplier;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters for Gson
    public void setPizzaDescription(String pizzaDescription) {
        this.pizzaDescription = pizzaDescription;
    }

    public void setPizzaPrice(double pizzaPrice) {
        this.pizzaPrice = pizzaPrice;
    }

    public void setPizzaPoints(int pizzaPoints) {
        this.pizzaPoints = pizzaPoints;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public void setSizeMultiplier(double sizeMultiplier) {
        this.sizeMultiplier = sizeMultiplier;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    // Get base pizza name to display most ordered pizza on recommendation
    // (first part before " ( ") , pizza name before the size of pizza
    public String getBasePizzaName() {
        if (pizzaDescription == null || pizzaDescription.isEmpty()) {
            return "";
        }
        int parenIndex = pizzaDescription.indexOf(" (");
        if (parenIndex > 0) {
            String beforeSize = pizzaDescription.substring(0, parenIndex);

            int plusIndex = beforeSize.indexOf(" + ");
            if (plusIndex > 0) {
                return beforeSize.substring(0, plusIndex);
            } else {
                return beforeSize;
            }
        }
        return pizzaDescription;
    }

    // Get list of toppings
    public List<String> getToppings() {
        List<String> toppings = new ArrayList<>();
        String[] parts = pizzaDescription.split(" \\+ ");
        for (int i = 1; i < parts.length; i++) {
            toppings.add(parts[i]);
        }
        return toppings;
    }

    // Calculate total price for this item (price * quantity)
    public double getItemTotal() {
        return pizzaPrice * quantity;
    }

    // Calculate total points for this item (points * quantity)
    public int getItemPoints() {
        return pizzaPoints * quantity;
    }

    // Get single item total (for display)
    public double getSingleItemTotal() {
        return pizzaPrice;
    }

    // Get description for display
    public String getDescription() {
        return String.format("%s | x %d ", pizzaDescription, quantity);
    }
}
