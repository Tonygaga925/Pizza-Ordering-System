package service;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
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
            pizzas.add(new Pizza(name, price, toppings));
        }

        sizeMultiplier = new HashMap<>();
        JsonObject sizeObj = menu.getAsJsonObject("sizeMultiplier");
        for (String key : sizeObj.keySet()) {
            sizeMultiplier.put(key, sizeObj.get(key).getAsDouble());
        }

        extraToppingPrice = menu.get("extraToppingPrice").getAsDouble();
    }

    public List<Pizza> getPizzas() { return pizzas; }
    public Map<String, Double> getSizeMultiplier() { return sizeMultiplier; }
    public double getExtraToppingPrice() { return extraToppingPrice; }
}