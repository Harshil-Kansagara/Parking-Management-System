package com.example.duepark_consumer.Model;

public class Employee {
    public String EmployeeId, GeneratedEmployeeId, EmployeeName, EmployeeMobileNumber, EmployeeEmailId, EmployeeAdharNumber, EmployeePassword, EmployeeProfilePic, EmployeeRole, LocationName, VehicleType;

    public Employee() { }

    public Employee(String employeeId, String generatedEmployeeId, String employeeName, String employeeMobileNumber, String employeeEmailId,
                    String employeeAdharNumber, String employeePassword, String employeeProfilePic, String employeeRole, String locationName, String vehicleType) {
        EmployeeId = employeeId;
        GeneratedEmployeeId = generatedEmployeeId;
        EmployeeName = employeeName;
        EmployeeMobileNumber = employeeMobileNumber;
        EmployeeEmailId = employeeEmailId;
        EmployeeAdharNumber = employeeAdharNumber;
        EmployeePassword = employeePassword;
        EmployeeProfilePic = employeeProfilePic;
        EmployeeRole = employeeRole;
        LocationName = locationName;
        VehicleType = vehicleType;
    }

    public String getVehicleType() {
        return VehicleType;
    }

    public void setVehicleType(String vehicleType) {
        VehicleType = vehicleType;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public String getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(String employeeId) {
        EmployeeId = employeeId;
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

    public String getEmployeeMobileNumber() {
        return EmployeeMobileNumber;
    }

    public void setEmployeeMobileNumber(String employeeMobileNumber) {
        EmployeeMobileNumber = employeeMobileNumber;
    }

    public String getEmployeeEmailId() {
        return EmployeeEmailId;
    }

    public void setEmployeeEmailId(String employeeEmailId) {
        EmployeeEmailId = employeeEmailId;
    }

    public String getEmployeeAdharNumber() {
        return EmployeeAdharNumber;
    }

    public void setEmployeeAdharNumber(String employeeAdharNumber) {
        EmployeeAdharNumber = employeeAdharNumber;
    }

    public String getEmployeePassword() {
        return EmployeePassword;
    }

    public void setEmployeePassword(String employeePassword) {
        EmployeePassword = employeePassword;
    }

    public String getEmployeeProfilePic() {
        return EmployeeProfilePic;
    }

    public void setEmployeeProfilePic(String employeeProfilePic) {
        EmployeeProfilePic = employeeProfilePic;
    }

    public String getEmployeeRole() {
        return EmployeeRole;
    }

    public void setEmployeeRole(String employeeRole) {
        EmployeeRole = employeeRole;
    }
}
