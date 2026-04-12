package model.command;

import java.io.IOException;
import java.util.Scanner;
import service.MemberManager;

public class MemberLoginCommand implements Command {
    private MemberManager memberManager;
    private Scanner scanner;

    public MemberLoginCommand(MemberManager memberManager, Scanner scanner) {
        this.memberManager = memberManager;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\n--- Member Login (-1 to go back to previous step) ---");
        String username = "";
        String password = "";
        int step = 0;
        
        while (step < 2) {
            if (step == 0) {
                System.out.print("Username: ");
                username = scanner.nextLine();
                if (username.equals("-1")) return;
                step++;
            } else if (step == 1) {
                System.out.print("Password: ");
                password = scanner.nextLine();
                if (password.equals("-1")) {
                    step--;
                } else {
                    step++;
                }
            }
        }

        try {
            if (memberManager.login(username, password)) {
                System.out.println("Login successful");
            } else {
                System.out.println("Invalid username or password!");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void undo() {
        // No undo needed
    }

    @Override
    public String getDescription() {
        return "Member Login";
    }
}
