package model.command;

import service.EmployeeManager;
import java.util.Scanner;

public class EmployeeLoginCommand implements Command {
    private EmployeeManager employeeManager;
    private Scanner scanner;

    public EmployeeLoginCommand(EmployeeManager employeeManager, Scanner scanner) {
        this.employeeManager = employeeManager;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        int step = 0;
        String username = "";
        String password = "";
        System.out.println("\n--- Employee Login (-1 to go back to previous step) ---");
        while (step < 2) {
            if (step == 0) {
                System.out.print("Username: ");
                username = scanner.nextLine();
                if (username.equals("-1")) return;
                step++;
            } else if (step == 1) {
                System.out.print("Password: ");
                password = scanner.nextLine();
                if (password.equals("-1")) 
                    step--;
                else 
                    step++;
            }
        }

        if (employeeManager.login(username, password)) {
            System.out.println("Employee login successful! Welcome, " + employeeManager.getCurrentEmployee().getName());
        } else {
            System.out.println("Invalid employee username or password!");
        }
    }

    @Override
    public void undo() {
        // No undo needed
    }

    @Override
    public String getDescription() {
        return "Employee Login";
    }
}
