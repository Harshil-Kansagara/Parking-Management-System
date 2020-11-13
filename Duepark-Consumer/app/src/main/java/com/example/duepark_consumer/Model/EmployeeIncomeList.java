package com.example.duepark_consumer.Model;

public class EmployeeIncomeList {
    public String LocationId, EmployeeId, EmployeeName, CashPayment, OnlinePayment;

    public EmployeeIncomeList() {
    }

    public EmployeeIncomeList(String employeeId, String cashPayment, String onlinePayment){
        EmployeeId = employeeId;
        CashPayment = cashPayment;
        OnlinePayment = onlinePayment;
    }

    public EmployeeIncomeList(String locationId, String employeeId, String employeeName, String cashPayment, String onlinePayment) {
        LocationId = locationId;
        EmployeeId = employeeId;
        EmployeeName = employeeName;
        CashPayment = cashPayment;
        OnlinePayment = onlinePayment;
    }

    public String getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(String employeeId) {
        EmployeeId = employeeId;
    }

    public String getLocationId() {
        return LocationId;
    }

    public void setLocationId(String locationId) {
        LocationId = locationId;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String getCashPayment() {
        return CashPayment;
    }

    public void setCashPayment(String cashPayment) {
        CashPayment = cashPayment;
    }

    public String getOnlinePayment() {
        return OnlinePayment;
    }

    public void setOnlinePayment(String onlinePayment) {
        OnlinePayment = onlinePayment;
    }
}
