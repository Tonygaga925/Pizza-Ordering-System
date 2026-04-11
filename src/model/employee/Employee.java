package model.employee;

import service.OrderManager;

public class Employee {
    private String id;
    private String username;
    private String password;
    private String name;
    private String role; // Matches "role": "normal"/"manager" in JSON
    private boolean isActive;
    private transient Role roleStrategy; 

    public Employee(String id, String username, String password, String name, boolean isActive, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.isActive = isActive;
        this.role = role;
    }

    public void initRole() {
        if ("manager".equalsIgnoreCase(this.role)) {
            this.roleStrategy = new ManagerRole();
        } else {
            this.roleStrategy = new StaffRole();
        }
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public boolean isActive() { return isActive; }
    public Role getRole() { return roleStrategy; }
    public void setId(String id) { this.id = id; }
    public boolean isManager() { return "manager".equalsIgnoreCase(this.role); }

    // Actions delegated to the Role Strategy
    public void handleOrder(OrderManager orderManager) {
        if (roleStrategy != null) {
            this.roleStrategy.handleOrder(orderManager);
        }
    }

    public void searchOrder(OrderManager orderManager){
        if (roleStrategy != null) {
            this.roleStrategy.searchOrder(orderManager);
        }
    }

    public boolean accessAdminPanel() {
        if (roleStrategy != null) {
            return this.roleStrategy.accessAdminPanel();
        }
        return false;
    }
}