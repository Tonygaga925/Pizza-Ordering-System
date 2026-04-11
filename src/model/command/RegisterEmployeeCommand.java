package model.command;

import service.EmployeeManager;
import java.io.IOException;
import java.util.Scanner;

public class RegisterEmployeeCommand implements Command {
    private EmployeeManager employeeManager;
    private Scanner scanner;

    public RegisterEmployeeCommand(EmployeeManager employeeManager, Scanner scanner) {
        this.employeeManager = employeeManager;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\n=== Create Staff Account [-1 to go back to previous step] ===");
        String username = "";
        String password = "";
        String name = "";
        int step = 0;
        while (step < 3) {
            if (step == 0) {
                System.out.print("Username: ");
                username = scanner.nextLine().trim();
                if (username.equals("-1")) return;
                
                if (username.isEmpty()) {
                    System.out.println("Username cannot be empty.");
                } else {
                    step++;
                }
            } else if (step == 1) {
                System.out.print("Password: ");
                password = scanner.nextLine().trim();
                if (password.equals("-1")) {
                    step--; // Go back to Username
                } else if (password.isEmpty()) {
                    System.out.println("Password cannot be empty.");
                } else {
                    step++;
                }
            } else if (step == 2) {
                System.out.print("Staff Name: ");
                name = scanner.nextLine().trim();
                if (name.equals("-1")) {
                    step--; // Go back to Password
                } else if (name.isEmpty()) {
                    System.out.println("Name cannot be empty.");
                } else {
                    step++;
                }
            }
        }
        
        try {
            if (employeeManager.registerStaff(username, password, name)) {
                System.out.println("Staff account for '" + name + "' created successfully.");
            } else {
                System.out.println("Registration failed: Username already exists!");
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
        return "Create Staff";
    }
}
