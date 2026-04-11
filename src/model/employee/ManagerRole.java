package model.employee;

public class ManagerRole extends Role {

    @Override
    public boolean accessAdminPanel() {
        return true;
    }

    @Override 
    public boolean canEditOrder(){
        return true;
    }
}
