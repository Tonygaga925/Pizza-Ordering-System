package model.employee;

public class ManagerRole extends Role {

    @Override
    public boolean accessAdminPanel() {
        return true;
    }
}
