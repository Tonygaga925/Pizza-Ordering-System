package model.employee;

public class StaffRole extends Role {
    @Override
    public boolean accessAdminPanel() {
        return false;
    }

    @Override 
    public boolean canEditOrder(){
        return false;
    }
}