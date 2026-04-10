package service;

import com.google.gson.reflect.TypeToken;
import model.employee.Employee;
import util.JsonUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EmployeeManager {
    private Map<String, Employee> employees;
    private Employee currentEmployee;
    private final String filePath;

    public EmployeeManager(String filePath) throws IOException {
        this.filePath = filePath;
        this.employees = new ConcurrentHashMap<>();
        loadEmployees();
    }

    private void loadEmployees() throws IOException {
        Type type = new TypeToken<Map<String, Employee>>(){}.getType();
        Map<String, Employee> loaded = JsonUtil.readFromFile(filePath, type);
        if (loaded != null) {
            employees = new ConcurrentHashMap<>(loaded);
            for (Map.Entry<String, Employee> entry : employees.entrySet()) {
                Employee e = entry.getValue();
                e.setId(entry.getKey());
                e.initRole();
            }
        }
    }

    public boolean login(String username, String password) {
        for (Employee e : employees.values()) {
            if (e.getUsername().equals(username) && e.getPassword().equals(password) && e.isActive()) {
                currentEmployee = e;
                return true;
            }
        }
        return false;
    }

    public void logout() {
        currentEmployee = null;
    }

    public boolean isLoggedIn() {
        return currentEmployee != null;
    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }
}
