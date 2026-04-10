package model.employee;

public class ManagerRole extends Role {

    @Override
    public void accessAdminPanel() {
        System.out.println("Access Granted: Manager opening the Admin Panel.");
    }
}
