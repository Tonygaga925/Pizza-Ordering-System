package model.employee;

public class StaffRole extends Role {
    @Override
    public boolean accessAdminPanel() {
        return false;
    }
}