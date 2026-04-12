package view;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputView {
    private static final Scanner scanner = new Scanner(System.in);

    public static Scanner getScanner() {
        return scanner;
    }

    public static int getIntInput() {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine();
                return input;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input! Please enter a number: ");
                scanner.nextLine();
            }
        }
    }

    public static int getIntInput(String prompt) {
        System.out.print(prompt);
        return getIntInput();
    }

    public static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
