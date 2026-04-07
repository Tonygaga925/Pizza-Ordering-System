package model.pizza;

import java.util.*;
import java.lang.reflect.Constructor;

public class PizzaFactory {
    // Pizza registration
    private static final List<Class<? extends Pizza>> pizzaClasses = new ArrayList<>();
    private static final List<String> pizzaNames = new ArrayList<>();
    private static final List<Double> pizzaPrices = new ArrayList<>();
    private static final List<Integer> pizzaPoints = new ArrayList<>();
    
    // Topping registration
    private static final Map<Integer, Class<? extends ToppingDecorator>> toppingMap = new LinkedHashMap<>();
    private static final List<String> toppingNames = new ArrayList<>();
    private static final List<Double> toppingPrices = new ArrayList<>();
    private static final List<Integer> toppingPoints = new ArrayList<>();
    
    static {
        // Register all pizza classes
        registerPizza(MargheritaPizza.class);
        registerPizza(PepperoniPizza.class);
        registerPizza(HawaiianPizza.class);
        registerPizza(VegetarianPizza.class);
        registerPizza(MeatLoversPizza.class);
        registerPizza(BBQChickenPizza.class);
        
        // Register all topping classes
        registerTopping(1, CheeseTopping.class);
        registerTopping(2, OliveTopping.class);
        registerTopping(3, OnionTopping.class);
        registerTopping(4, BaconTopping.class);
        registerTopping(5, SausageTopping.class);
        registerTopping(6, ChickenTopping.class);
    }
    
    private static void registerPizza(Class<? extends Pizza> pizzaClass) {
        try {
            Constructor<? extends Pizza> constructor = pizzaClass.getConstructor();
            Pizza instance = constructor.newInstance();
            pizzaClasses.add(pizzaClass);
            pizzaNames.add(instance.getDescription());
            pizzaPrices.add(instance.getPrice());
            pizzaPoints.add(instance.getPoints());
        } catch (Exception e) {
            System.err.println("Failed to register pizza: " + pizzaClass.getSimpleName());
        }
    }
    
    private static void registerTopping(int id, Class<? extends ToppingDecorator> toppingClass) {
        try {
            Pizza dummyPizza = new Pizza() {
            @Override public String getDescription() { return ""; }
            @Override public double getPrice() { return 0; }
            @Override public int getPoints() { return 0; }
        };
            ToppingDecorator tempTopping = toppingClass.getConstructor(Pizza.class).newInstance(dummyPizza);
        
            toppingMap.put(id, toppingClass);
            toppingNames.add(tempTopping.getToppingName());
            toppingPrices.add(tempTopping.getToppingPrice());
            toppingPoints.add(tempTopping.getToppingPoints());
        } catch (Exception e) {
            System.err.println("Failed to register topping: " + toppingClass.getSimpleName());
        }
    }
    
    public static Pizza createPizza(int choice) {
        if (choice < 1 || choice > pizzaClasses.size()) {
            throw new IllegalArgumentException("Invalid pizza choice: " + choice);
        }
        try {
            return pizzaClasses.get(choice - 1).getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create pizza", e);
        }
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
    
    // Getters for pizza data
    public static List<String> getPizzaNames() {
        return new ArrayList<>(pizzaNames);
    }
    
    public static List<Double> getPizzaPrices() {
        return new ArrayList<>(pizzaPrices);
    }
    
    public static List<Integer> getPizzaPoints() {
        return new ArrayList<>(pizzaPoints);
    }
    
    // Getters for topping data
    public static List<String> getToppingNames() {
        return new ArrayList<>(toppingNames);
    }
    
    public static List<Double> getToppingPrices() {
        return new ArrayList<>(toppingPrices);
    }
    
    public static List<Integer> getToppingPoints() {
        return new ArrayList<>(toppingPoints);
    }
    
    // Display methods
    public static void displayPizzaMenu() {
        System.out.println("\n--- Pizza Menu ---");
        for (int i = 0; i < pizzaNames.size(); i++) {
            System.out.printf("%d. %s - $%.2f (%d points)%n", 
                i + 1, pizzaNames.get(i), pizzaPrices.get(i), pizzaPoints.get(i));
        }
        System.out.println("0. Finish / Checkout");
    }
    
    public static void displayToppingMenu() {
        System.out.println("\n=== Extra Toppings Menu ===");
        System.out.println("Enter topping numbers (1-" + toppingNames.size() + "), one at a time.");
        System.out.println("Enter 0 when you are done.\n");
        
        for (int i = 0; i < toppingNames.size(); i++) {
            System.out.printf("  %d. %s - $%.2f (%d points)%n", 
                i + 1, toppingNames.get(i), toppingPrices.get(i), toppingPoints.get(i));
        }
        System.out.println("  0. Done / Finish selecting toppings");
    }
    
    public static int getPizzaCount() {
        return pizzaClasses.size();
    }
    
    public static int getToppingCount() {
        return toppingNames.size();
    }
    
    public static double getToppingPrice(int choice) {
        if (choice < 1 || choice > toppingPrices.size()) {
            return 0;
        }
        return toppingPrices.get(choice - 1);
    }
    
    public static int getToppingPoints(int choice) {
        if (choice < 1 || choice > toppingPoints.size()) {
            return 0;
        }
        return toppingPoints.get(choice - 1);
    }
}