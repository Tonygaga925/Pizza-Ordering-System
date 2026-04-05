package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.Pizza;
import util.JsonUtil;

import java.io.IOException;
import java.util.*;

public class MenuLoader {
    private List<Pizza> pizzas;
    private Map<String, Double> sizeMultiplier;
    private double extraToppingPrice;

    public MenuLoader(String menuFilePath) throws IOException {
        JsonObject menu = JsonUtil.readFromFile(menuFilePath, JsonObject.class);
        
        // Load pizzas
        JsonArray pizzaArray = menu.getAsJsonArray("pizzas");
        pizzas = new ArrayList<>();
        for (var elem : pizzaArray) {
            JsonObject p = elem.getAsJsonObject();
            String name = p.get("name").getAsString();
            double price = p.get("basePrice").getAsDouble();
            List<String> toppings = new ArrayList<>();
            for (var t : p.getAsJsonArray("toppings")) {
                toppings.add(t.getAsString());
            }
            
            // Read pointsValue (default to 0 if not present)
            int pointsValue = 0;
            if (p.has("pointsValue")) {
                pointsValue = p.get("pointsValue").getAsInt();
            }
            
            pizzas.add(new Pizza(name, price, toppings, pointsValue));
        }
        
        // Load size multipliers
        JsonObject sizeMultiplierObj = menu.getAsJsonObject("sizeMultiplier");
        sizeMultiplier = new HashMap<>();
        for (var entry : sizeMultiplierObj.entrySet()) {
            sizeMultiplier.put(entry.getKey(), entry.getValue().getAsDouble());
        }
        
        // Load extra topping price
        extraToppingPrice = menu.get("extraToppingPrice").getAsDouble();
    }
    
    public List<Pizza> getPizzas() {
        return pizzas;
    }
    
    public Map<String, Double> getSizeMultiplier() {
        return sizeMultiplier;
    }
    
    public double getExtraToppingPrice() {
        return extraToppingPrice;
    }
}