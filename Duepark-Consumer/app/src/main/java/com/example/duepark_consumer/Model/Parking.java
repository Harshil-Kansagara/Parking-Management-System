package com.example.duepark_consumer.Model;

public class Parking {
    public String EmployeeId, EmployeeName, EmployeeMobileNumber, EmployeePhoneNumber, EmployeeEmailId, EmployeePassword, EmployeeRole, ParkingId, GeneratedParkingId, ParkingAcronym,
            ParkingName, ParkingType, ParkingAddress, ParkingCity, ParkingDate, ParkingTime, ParkingActiveState;

    public String getEmployeePhoneNumber() {
        return EmployeePhoneNumber;
    }

    public void setEmployeePhoneNumber(String employeePhoneNumber) {
        EmployeePhoneNumber = employeePhoneNumber;
    }

    public Parking(){}

    public Parking(String employeeId, String employeeName, String employeeMobileNumber, String employeePhoneNumber,  String employeeEmailId, String employeePassword, String employeeRole,
                   String parkingId, String generatedParkingId, String parkingAcronym, String parkingName, String parkingType, String parkingAddress,
                   String parkingCity, String parkingDate, String parkingTime, String parkingActiveState) {
        EmployeeId = employeeId;
        EmployeeName = employeeName;
        EmployeeMobileNumber = employeeMobileNumber;
        EmployeePhoneNumber = employeePhoneNumber;
        EmployeeEmailId = employeeEmailId;
        EmployeePassword = employeePassword;
        EmployeeRole = employeeRole;
        ParkingId = parkingId;
        GeneratedParkingId = generatedParkingId;
        ParkingAcronym = parkingAcronym;
        ParkingName = parkingName;
        ParkingType = parkingType;
        ParkingAddress = parkingAddress;
        ParkingCity = parkingCity;
        ParkingDate = parkingDate;
        ParkingTime = parkingTime;
        ParkingActiveState = parkingActiveState;
    }

    public String getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(String employeeId) {
        EmployeeId = employeeId;
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

    public String getEmployeePassword() {
        return EmployeePassword;
    }

    public void setEmployeePassword(String employeePassword) {
        EmployeePassword = employeePassword;
    }

    public String getEmployeeRole() {
        return EmployeeRole;
    }

    public void setEmployeeRole(String employeeRole) {
        EmployeeRole = employeeRole;
    }

    public String getParkingId() {
        return ParkingId;
    }

    public void setParkingId(String parkingId) {
        ParkingId = parkingId;
    }

    public String getGeneratedParkingId() {
        return GeneratedParkingId;
    }

    public void setGeneratedParkingId(String generatedParkingId) {
        GeneratedParkingId = generatedParkingId;
    }

    public String getParkingAcronym() {
        return ParkingAcronym;
    }

    public void setParkingAcronym(String parkingAcronym) {
        ParkingAcronym = parkingAcronym;
    }

    public String getParkingName() {
        return ParkingName;
    }

    public void setParkingName(String parkingName) {
        ParkingName = parkingName;
    }

    public String getParkingType() {
        return ParkingType;
    }

    public void setParkingType(String parkingType) {
        ParkingType = parkingType;
    }

    public String getParkingAddress() {
        return ParkingAddress;
    }

    public void setParkingAddress(String parkingAddress) {
        ParkingAddress = parkingAddress;
    }

    public String getParkingCity() {
        return ParkingCity;
    }

    public void setParkingCity(String parkingCity) {
        ParkingCity = parkingCity;
    }

    public String getParkingDate() {
        return ParkingDate;
    }

    public void setParkingDate(String parkingDate) {
        ParkingDate = parkingDate;
    }

    public String getParkingTime() {
        return ParkingTime;
    }

    public void setParkingTime(String parkingTime) {
        ParkingTime = parkingTime;
    }

    public String getParkingActiveState() {
        return ParkingActiveState;
    }

    public void setParkingActiveState(String parkingActiveState) {
        ParkingActiveState = parkingActiveState;
    }
}
