package com.example.duepark_admin.Model;

public class Employee {
    private String id, userName, generatedEmployeeId, role;

    public Employee(String id, String userName, String generatedEmployeeId, String role) {
        this.id = id;
        this.userName = userName;
        this.generatedEmployeeId = generatedEmployeeId;
        this.role = role;
    }

    public String getGeneratedEmployeeId() {
        return generatedEmployeeId;
    }

    public void setGeneratedEmployeeId(String generatedEmployeeId) {
        this.generatedEmployeeId = generatedEmployeeId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
