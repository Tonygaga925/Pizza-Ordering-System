package model.order;

import model.pizza.Pizza;
import model.size.Size;

public class OrderItem {
    private Pizza pizza;
    private Size size;
    private int quantity;
    
    public OrderItem(Pizza pizza, Size size, int quantity) {
        this.pizza = pizza;
        this.size = size;
        this.quantity = quantity;
    }
    
    public Pizza getPizza() { return pizza; }
    public Size getSize() { return size; }
    public int getQuantity() { return quantity; }
    
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getItemTotal() {
        return pizza.getPrice() * quantity;
    }
    
    public int getItemPoints() {
        return (int)(pizza.getPoints() * quantity);
    }
    
    public String getDescription() {
        return String.format("%dx %s (%s)", quantity, pizza.getDescription(), size.getName());
    }

     public double getSingleItemTotal() {
        return pizza.getPrice();
    }
    
}