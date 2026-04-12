package model.employee;

public class StaffRole extends Role {
    @Override 
    public boolean canEditOrder(){
        return false;
    }

    @Override
    public boolean accessAdminPanel() {
        return false;
    }
}