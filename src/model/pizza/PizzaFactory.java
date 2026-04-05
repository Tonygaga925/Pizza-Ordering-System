package model.pizza;

import java.util.*;

public class PizzaFactory {
    private static final Map<Integer, Class<? extends ToppingDecorator>> toppingMap = new LinkedHashMap<>();
    private static final List<String> toppingNames = new ArrayList<>();
    
    static {
        toppingMap.put(1, CheeseTopping.class);
        toppingMap.put(2, OliveTopping.class);
        toppingMap.put(3, OnionTopping.class);
        toppingMap.put(4, BaconTopping.class);
        toppingMap.put(5, SausageTopping.class);
        toppingMap.put(6, ChickenTopping.class);
        
        toppingNames.add("Cheese");
        toppingNames.add("Olive");
        toppingNames.add("Onion");
        toppingNames.add("Bacon");
        toppingNames.add("Sausage");
        toppingNames.add("Chicken");
    }
    
    public static Pizza addTopping(Pizza pizza, int choice) {
        try {
            Class<? extends ToppingDecorator> toppingClass = toppingMap.get(choice);
            if (toppingClass == null) {
                throw new IllegalArgumentException("Invalid topping choice: " + choice);
            }
            return toppingClass.getConstructor(Pizza.class).newInstance(pizza);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add topping", e);
        }
    }
    
    public static void displayToppingMenu() {
        System.out.println("\n=== Extra Toppings Menu ===");
        System.out.println("Enter topping numbers, one at a time.");
        System.out.println("Enter 0 when you are done.\n");
        
        for (int i = 0; i < toppingNames.size(); i++) {
            Pizza temp = new BasePizza("Temp", 0, 0);
            temp = addTopping(temp, i + 1);
            double price = temp.getPrice();
            int points = temp.getPoints();
            System.out.printf("  %d. %s - $%.2f (%d points)%n", 
                i + 1, toppingNames.get(i), price, points);
        }
        System.out.println("  0. Done / Finish selecting toppings");
    }
    
    public static List<String> getToppingNames() {
        return new ArrayList<>(toppingNames);
    }
    
    public static int getToppingCount() {
        return toppingNames.size();
    }
}