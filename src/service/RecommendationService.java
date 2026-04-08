package service;

import model.Member;
import model.order.Order;
import model.order.OrderItem;
import java.util.*;

public class RecommendationService {
    
    // Similar pizza mappings for "try something similar" feature
    private static final Map<String, List<String>> PIZZA_SIMILARITIES = new HashMap<>();
    
    static {
        PIZZA_SIMILARITIES.put("Margherita", Arrays.asList("Vegetarian", "Hawaiian"));
        PIZZA_SIMILARITIES.put("Pepperoni", Arrays.asList("Meat Lovers", "BBQ Chicken"));
        PIZZA_SIMILARITIES.put("Hawaiian", Arrays.asList("BBQ Chicken", "Pepperoni"));
        PIZZA_SIMILARITIES.put("Vegetarian", Arrays.asList("Margherita", "Hawaiian"));
        PIZZA_SIMILARITIES.put("Meat Lovers", Arrays.asList("Pepperoni", "BBQ Chicken"));
        PIZZA_SIMILARITIES.put("BBQ Chicken", Arrays.asList("Hawaiian", "Pepperoni", "Meat Lovers"));
    }
    
    // Callback interface for communicating with Main
    public interface MainCallback {
        void startOrderWithRecommendedPizza(String pizzaName, boolean isMember);
    }
    
    private OrderManager orderManager;
    private MenuLoader menuLoader;
    private Scanner scanner;
    private MainCallback callback;
    private boolean currentIsMember;
    
    public RecommendationService(OrderManager orderManager, MenuLoader menuLoader, Scanner scanner) {
        this.orderManager = orderManager;
        this.menuLoader = menuLoader;
        this.scanner = scanner;
    }
    
    public void setCallback(MainCallback callback) {
        this.callback = callback;
    }
    
    public void getRecommendation(Member member, boolean isMember) {
        this.currentIsMember = isMember;
        
        System.out.println("\n=== Pizza Recommendation ===");
        System.out.println("Let me help you find your next favorite pizza!\n");
        
        // Get member's order history
        List<Order> orders = orderManager.getOrdersByMemberIdFromFile(member.getId());
        
        if (!orders.isEmpty()) {
            // Find the most frequently ordered pizza
            String mostOrdered = getMostOrderedPizza(orders);
            if (mostOrdered != null) {
                System.out.println("I see you've ordered '" + mostOrdered + "' quite often!");
                System.out.print("Would you like to try something similar but different? (y/n): ");
                String answer = scanner.nextLine().toLowerCase();
                
                if (answer.equals("y")) {
                    recommendSimilarPizza(mostOrdered);
                    return;
                }
            }
        }
        
        // No order history or user doesn't want similar flavors
        recommendByQuestionnaire();
    }
    
    // Get the most frequently ordered pizza from history
    private String getMostOrderedPizza(List<Order> orders) {
        Map<String, Integer> pizzaCount = new HashMap<>();
        
        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                String pizzaName = item.getBasePizzaName();
                pizzaCount.put(pizzaName, pizzaCount.getOrDefault(pizzaName, 0) + item.getQuantity());
            }
        }
        
        if (pizzaCount.isEmpty()) {
            return null;
        }
        
        return pizzaCount.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    
    // Recommend similar pizza based on user's favorite
    private void recommendSimilarPizza(String currentPizza) {
        List<String> similar = PIZZA_SIMILARITIES.get(currentPizza);
        if (similar == null || similar.isEmpty()) {
            System.out.println("Sorry, I couldn't find similar pizzas. Let me ask you a few questions instead.\n");
            recommendByQuestionnaire();
            return;
        }
        
        System.out.println("\nBased on your preference, you might like:");
        for (int i = 0; i < similar.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, similar.get(i));
        }
        
        System.out.print("\nChoose a number (1-" + similar.size() + ") or 0 to skip: ");
        int choice = getIntInput();
        
        if (choice >= 1 && choice <= similar.size()) {
            String recommended = similar.get(choice - 1);
            System.out.println("\nRecommendation: " + recommended);
            System.out.println("Why not try " + recommended + " on your next order?");
            displayPizzaDetails(recommended);
            askToOrder(recommended);
        } else {
            System.out.println("\nLet me ask you a few questions to find the perfect pizza for you.\n");
            recommendByQuestionnaire();
        }
    }
    
    // Questionnaire-based recommendation
    private void recommendByQuestionnaire() {
        System.out.println("Please answer a few questions to help me recommend the best pizza for you:\n");
        
        System.out.print("Q1. Are you vegetarian? (y/n): ");
        boolean isVegetarian = scanner.nextLine().toLowerCase().equals("y");
        
        if (isVegetarian) {
            recommendVegetarianPizza();
            return;
        }
        
        System.out.print("Q2. Are you on a diet / health-conscious? (y/n): ");
        boolean isOnDiet = scanner.nextLine().toLowerCase().equals("y");
        
        if (isOnDiet) {
            recommendHealthyPizza();
            return;
        }
        
        System.out.println("\nQ3. Which meat do you prefer?");
        System.out.println("  1. Pepperoni");
        System.out.println("  2. Sausage");
        System.out.println("  3. Chicken");
        System.out.println("  4. Bacon");
        System.out.println("  5. All of the above");
        System.out.print("Choose (1-5): ");

        int meatChoice = getIntInput();
        String recommended = "";
        
        switch (meatChoice) {
            case 1:
                recommended = "Pepperoni";
                displayMeatRecommendation("Pepperoni");
                break;
            case 2:
                recommended = "Meat Lovers";
                displayMeatRecommendation("Sausage");
                break;
            case 3:
                recommended = "BBQ Chicken";
                displayMeatRecommendation("Chicken");
                break;
            case 4:
                recommended = "Meat Lovers";
                displayMeatRecommendation("Bacon");
                break;
            case 5:
                recommended = "Meat Lovers";
                displayMeatRecommendation("Mixed");
                break;
            default:
                recommended = "Pepperoni";
                displayDefaultRecommendation();
        }
        
        displayPizzaDetails(recommended);
        askToOrder(recommended);
    }
    
    // Display vegetarian pizza recommendation
    private void recommendVegetarianPizza() {
        System.out.println("\n=== Vegetarian Options ===");
        System.out.println("Based on your preference, here are my recommendations:");
        System.out.println("  1. Margherita - Classic cheese and basil");
        System.out.println("  2. Vegetarian - Loaded with fresh vegetables");
        System.out.println("\nTop Recommendation: VEGETARIAN");
        System.out.println("   Why? It's packed with mushrooms, olives, and bell peppers - delicious and meat-free!");
        
        displayPizzaDetails("Vegetarian");
        askToOrder("Vegetarian");
    }
    
    // Display healthy pizza recommendation
    private void recommendHealthyPizza() {
        System.out.println("\n=== Healthy Options ===");
        System.out.println("Watching your calories? Here are lighter options:");
        System.out.println("  1. Margherita - Lower calorie, simple ingredients");
        System.out.println("  2. Vegetarian - Lots of vegetables");
        System.out.println("\nTop Recommendation: MARGHERITA");
        System.out.println("   Why? It's the lightest option with fresh ingredients and no processed meats.");
        
        displayPizzaDetails("Margherita");
        askToOrder("Margherita");
    }
    
    // Display meat-based pizza recommendation message
    private void displayMeatRecommendation(String meatType) {
        System.out.println("\n=== Meat Lovers Recommendations ===");
        
        switch (meatType) {
            case "Pepperoni":
                System.out.println("Top Recommendation: PEPPERONI");
                System.out.println("   Why? Classic pepperoni pizza with extra pepperoni topping!");
                break;
            case "Sausage":
                System.out.println("Top Recommendation: MEAT LOVERS");
                System.out.println("   Why? Loaded with sausage and other meats - perfect for sausage fans!");
                break;
            case "Chicken":
                System.out.println("Top Recommendation: BBQ CHICKEN");
                System.out.println("   Why? Tender chicken with tangy BBQ sauce - a customer favorite!");
                break;
            case "Bacon":
                System.out.println("Top Recommendation: MEAT LOVERS");
                System.out.println("   Why? Packed with bacon, pepperoni, and sausage - bacon lover's dream!");
                break;
            case "Mixed":
                System.out.println("Top Recommendation: MEAT LOVERS");
                System.out.println("   Why? Everything you want - pepperoni, sausage, beef, and bacon!");
                break;
        }
    }
    
    // Display default recommendation message
    private void displayDefaultRecommendation() {
        System.out.println("\nTop Recommendation: PEPPERONI");
        System.out.println("   It's our most popular pizza - you can't go wrong!");
    }
    
    // Display pizza details (price and points) - Updated to use MenuLoader
    private void displayPizzaDetails(String pizzaName) {
        List<String> pizzaNames = menuLoader.getPizzaNames();
        List<Double> pizzaPrices = menuLoader.getPizzaPrices();
        List<Integer> pizzaPoints = menuLoader.getPizzaPoints();
        
        for (int i = 0; i < pizzaNames.size(); i++) {
            if (pizzaNames.get(i).equalsIgnoreCase(pizzaName)) {
                System.out.println("\n--- Pizza Details ---");
                System.out.println("Name: " + pizzaNames.get(i));
                System.out.printf("Base price: $%.2f%n", pizzaPrices.get(i));
                System.out.println("Points: " + pizzaPoints.get(i));
                System.out.println("=====================");
                break;
            }
        }
    }
    
    // Ask user if they want to order the recommended pizza
    private void askToOrder(String recommendedPizza) {
        System.out.print("\nWould you like to order this pizza now? (y/n): ");
        String answer = scanner.nextLine().toLowerCase();
        
        if (answer.equals("y")) {
            System.out.println("\nGreat! Let's customize your " + recommendedPizza + "...\n");
            if (callback != null) {
                callback.startOrderWithRecommendedPizza(recommendedPizza, currentIsMember);
            }
        } else {
            System.out.println("\nNo problem! Returning to main menu.");
        }
    }
    
    // Safe integer input handling
    private int getIntInput() {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                return input;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input! Please enter a number: ");
            }
        }
    }
}