package service;

import java.util.*;
import model.pizza.PizzaFactory;
import model.size.SizeFactory;

public class MenuLoader {
    private List<String> pizzaNames;
    private List<Double> pizzaPrices;
    private List<Integer> pizzaPoints;
    private Map<String, Double> sizeMultiplier;
    
    public MenuLoader() {
        // Load pizza data from PizzaFactory
        this.pizzaNames = PizzaFactory.getPizzaNames();
        this.pizzaPrices = PizzaFactory.getPizzaPrices();
        this.pizzaPoints = PizzaFactory.getPizzaPoints();
        
        // Load size data from SizeFactory
        this.sizeMultiplier = new LinkedHashMap<>();
        List<String> sizeNames = SizeFactory.getSizeNames();
        List<Double> sizeMultipliers = SizeFactory.getSizeMultipliers();
        
        for (int i = 0; i < sizeNames.size(); i++) {
            sizeMultiplier.put(sizeNames.get(i), sizeMultipliers.get(i));
        }
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