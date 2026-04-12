package model.employee;

public abstract class Role {
    public abstract boolean accessAdminPanel();

    public abstract boolean canEditOrder();
}
