package com.example.duepark_admin.Model;

public class RequestParking {
    private String id, employeeName, employeeMobileNumber;

    public RequestParking(){}

    public RequestParking(String id, String employeeName, String employeeMobileNumber) {
        this.id = id;
        this.employeeName = employeeName;
        this.employeeMobileNumber = employeeMobileNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeMobileNumber() {
        return employeeMobileNumber;
    }

    public void setEmployeeMobileNumber(String employeeMobileNumber) {
        this.employeeMobileNumber = employeeMobileNumber;
    }
}
