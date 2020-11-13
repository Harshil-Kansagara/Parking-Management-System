package com.example.duepark_consumer.Model;

public class AssignedEmployeeList {
    public String id, EmployeeName, GeneratedEmployeeId, EmployeeRole;

    public AssignedEmployeeList(String id, String employeeName, String generatedEmployeeId, String employeeRole) {
        this.id = id;
        EmployeeName = employeeName;
        GeneratedEmployeeId = generatedEmployeeId;
        EmployeeRole = employeeRole;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String getGeneratedEmployeeId() {
        return GeneratedEmployeeId;
    }

    public void setGeneratedEmployeeId(String generatedEmployeeId) {
        GeneratedEmployeeId = generatedEmployeeId;
    }

    public String getEmployeeRole() {
        return EmployeeRole;
    }

    public void setEmployeeRole(String employeeRole) {
        EmployeeRole = employeeRole;
    }
}
