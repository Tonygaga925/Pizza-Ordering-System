package view;

import java.util.List;

public class MenuView {
    public static void displayPizzaMenu(List<String> pizzaNames, List<Double> pizzaPrices, List<Integer> pizzaPoints, boolean isCartEmpty, boolean isMember) {
        System.out.println("\n--- Pizza Menu ---");
        for (int i = 0; i < pizzaNames.size(); i++) {
            if (isMember) {
                System.out.printf("%d. %s - $%.2f (%d points)%n",
                        i + 1, pizzaNames.get(i), pizzaPrices.get(i), pizzaPoints.get(i));
            } else {
                System.out.printf("%d. %s - $%.2f %n",
                        i + 1, pizzaNames.get(i), pizzaPrices.get(i));
            }
        }
        if (!isCartEmpty) {
            System.out.println("0. Finish / Checkout");
        }
    }

    public static void displayToppingMenu(List<String> toppingNames, List<Double> toppingPrices, List<Integer> toppingPoints, boolean isMember) {
        System.out.println("\n=== Extra Toppings Menu ===");
        System.out.println("Enter topping numbers (1-" + toppingNames.size() + "), one at a time.");
        System.out.println("Enter 0 when you are done.\n");

        for (int i = 0; i < toppingNames.size(); i++) {
            if (isMember) {
                System.out.printf("  %d. %s - $%.2f (%d points)%n",
                        i + 1, toppingNames.get(i), toppingPrices.get(i), toppingPoints.get(i));
            } else {
                System.out.printf("  %d. %s - $%.2f %n",
                        i + 1, toppingNames.get(i), toppingPrices.get(i));
            }
        }
        System.out.println("  0. Done / Finish selecting toppings");
    }

    public static void displaySizeOptions(List<String> sizeNames, List<Double> sizeMultipliers, double currentPrice) {
        System.out.println("\n=== Size Options ===");
        for (int i = 0; i < sizeNames.size(); i++) {
            System.out.printf("%d. %s - $%.2f%n", i + 1, sizeNames.get(i), currentPrice * sizeMultipliers.get(i));
        }
    }
}
