package com.example.duepark_consumer.Model;

public class EmployeeList {
    public String id, GeneratedEmployeeId, EmployeeName, Role, EmployeeActiveState;

    public EmployeeList(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGeneratedEmployeeId() {
        return GeneratedEmployeeId;
    }

    public void setGeneratedEmployeeId(String generatedEmployeeId) {
        GeneratedEmployeeId = generatedEmployeeId;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getEmployeeActiveState() {
        return EmployeeActiveState;
    }

    public void setEmployeeActiveState(String employeeActiveState) {
        EmployeeActiveState = employeeActiveState;
    }

    public EmployeeList(String id, String generatedEmployeeId, String employeeName, String role, String employeeActiveState) {
        this.id = id;
        GeneratedEmployeeId = generatedEmployeeId;
        EmployeeName = employeeName;
        Role = role;
        EmployeeActiveState = employeeActiveState;
    }
}
