package model.employee;

public class StaffRole extends Role {
    @Override
    public void accessAdminPanel() {
        System.out.println("Access Denied: Staff do not have permission to access the Admin Panel.");
    }
}