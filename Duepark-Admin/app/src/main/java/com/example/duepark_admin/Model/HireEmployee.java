package com.example.duepark_admin.Model;

public class HireEmployee {
    private String employeeId, employeeName;
    private boolean isChecked;

    public HireEmployee(String employeeId, String employeeName, boolean isChecked) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.isChecked = isChecked;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
