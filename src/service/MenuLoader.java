package service;

import java.util.*;
import model.pizza.PizzaFactory;
import model.size.SizeFactory;

public class MenuLoader {
    
    private static MenuLoader instance;
    
    private List<String> pizzaNames;
    private List<Double> pizzaPrices;
    private List<Integer> pizzaPoints;
    private Map<String, Double> sizeMultiplier;
    
    private MenuLoader() {
        // Load pizza data from PizzaFactory
        this.pizzaNames = PizzaFactory.getPizzaNames();
        this.pizzaPrices = PizzaFactory.getPizzaPrices();
        this.pizzaPoints = PizzaFactory.getPizzaPoints();

        this.sizeMultiplier = new LinkedHashMap<>();
        List<String> sizeNames = SizeFactory.getSizeNames();
        List<Double> sizeMultipliers = SizeFactory.getSizeMultipliers();
        
        for (int i = 0; i < sizeNames.size(); i++) {
            sizeMultiplier.put(sizeNames.get(i), sizeMultipliers.get(i));
        }
    }

    public static MenuLoader getInstance() {
        if (instance == null) {
            instance = new MenuLoader();
        }
        return instance;
    }
    
    public List<String> getPizzaNames() {
        return pizzaNames;
    }
    
    public List<Double> getPizzaPrices() {
        return pizzaPrices;
    }
    
    public List<Integer> getPizzaPoints() {
        return pizzaPoints;
    }
    
    public Map<String, Double> getSizeMultiplier() {
        return sizeMultiplier;
    }
}