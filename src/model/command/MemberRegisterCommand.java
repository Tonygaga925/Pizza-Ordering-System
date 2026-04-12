package model.command;

import java.io.IOException;
import java.util.Scanner;
import service.MemberManager;

public class MemberRegisterCommand implements Command {
    private MemberManager memberManager;
    private Scanner scanner;

    public MemberRegisterCommand(MemberManager memberManager, Scanner scanner) {
        this.memberManager = memberManager;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\n--- Register (-1 to go back to previous step) ---");
        String username = "";
        String password = "";
        String name = "";
        String phone = "";
        int step = 0;

        while (step < 4) {
            if (step == 0) {
                boolean inputUserName = true;
                while(inputUserName){
                System.out.print("Username: ");
                username = scanner.nextLine().trim();
                if (username.equals("-1")) {
                    inputUserName = false;
                    break;
                }
                if (username.isEmpty()) {
                    System.out.println("Username cannot be empty.");
                    continue;
                }
                if(memberManager.isExistUserName(username)){
                    System.out.println("Username already exist!");
                    continue;
                }
                step++;
                break;
                }
                if (!inputUserName) return;
            } else if (step == 1) {
                System.out.print("Password: ");
                password = scanner.nextLine();
                if (password.equals("-1")) {
                    step--;
                } else {
                    step++;
                }
            } else if (step == 2) {
                System.out.print("Your Name: ");
                name = scanner.nextLine();
                if (name.equals("-1")) {
                    step--;
                } else {
                    step++;
                }
            } else if (step == 3) {
                System.out.print("Phone Number: ");
                phone = scanner.nextLine();
                if (phone.equals("-1")) {
                    step--;
                } else if (phone.length() != 8) {
                    System.out.println("Phone number must be exactly 8 digits.");
                } else if (!phone.matches("\\d+")) {
                    System.out.println("Phone number can only contain numbers.");
                } else {
                    step++;
                }
            }
        }

        try {
            if (memberManager.register(username, password, name, phone)) {
                System.out.println("Registration successful. Please login.");
            } else {
                System.out.println("Username already exists!");
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
        return "Member Register";
    }
}
