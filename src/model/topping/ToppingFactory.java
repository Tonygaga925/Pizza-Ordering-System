package model.topping;

import java.util.*;

public class ToppingFactory {
    private static final Map<Integer, Topping> toppingMap = new LinkedHashMap<>();
    private static final List<Topping> allToppings = new ArrayList<>();
    private static final Map<String, Topping> nameToToppingMap = new HashMap<>();
    
    static {
        // Initialize all toppings with their menu numbers
        allToppings.add(new CheeseTopping());      // 1
        allToppings.add(new PepperoniTopping());   // 2
        allToppings.add(new MushroomTopping());    // 3
        allToppings.add(new OliveTopping());       // 4
        allToppings.add(new OnionTopping());       // 5
        allToppings.add(new BaconTopping());       // 6
        allToppings.add(new SausageTopping());     // 7
        allToppings.add(new ChickenTopping());     // 8
        
        // Populate maps
        for (int i = 0; i < allToppings.size(); i++) {
            Topping t = allToppings.get(i);
            toppingMap.put(i + 1, t);
            nameToToppingMap.put(t.getName(), t);
        }
    }

    public static Topping getTopping(int choice) {
        Topping topping = toppingMap.get(choice);
        if (topping == null) {
            throw new IllegalArgumentException("Invalid topping choice: " + choice);
        }
        return topping;
    }
    
    public static Topping createTopping(String name) {
        Topping topping = nameToToppingMap.get(name);
        if (topping == null) {
            throw new IllegalArgumentException("Unknown topping: " + name);
        }
        return topping;
    }

    public static int getToppingPoints(String toppingName) {
        Topping topping = nameToToppingMap.get(toppingName);
        if (topping != null) {
            return topping.getPointsValue();
        }
        return 5; // default value
    }
    
    public static void displayToppingMenu() {
        System.out.println("\n=== Extra Toppings Menu ===");
        System.out.println("Enter topping numbers (1-8), one at a time.");
        System.out.println("Enter 0 when you are done.\n");
        
        for (int i = 0; i < allToppings.size(); i++) {
            Topping t = allToppings.get(i);
            System.out.printf("  %d. %s - $%.2f (%d points)%n", 
                i + 1, t.getName(), t.getPrice(), t.getPointsValue());
        }
        System.out.println("  0. Done / Finish selecting toppings");
    }
    
    public static List<Topping> getAllToppings() {
        return new ArrayList<>(allToppings);
    }
}