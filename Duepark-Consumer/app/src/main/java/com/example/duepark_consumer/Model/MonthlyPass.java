package com.example.duepark_consumer.Model;

public class MonthlyPass {
    public String MonthlyPassId, PassUserName, PassUserMobileNumber, PayableAmount, PayableType, IssuedDate, ExpiryDate, VehicleNumber, VehicleType, GeneratedLocationId, GeneratedMonthlyPassId, IssuedBy, IssuedTime;

    public MonthlyPass() { }

    public MonthlyPass(String monthlyPassId, String passUserName, String passUserMobileNumber, String payableAmount, String payableType,
                       String issuedDate, String expiryDate, String vehicleNumber, String vehicleType, String generatedLocationId,
                       String generatedMonthlyPassId, String issuedBy, String issuedTime) {
        MonthlyPassId = monthlyPassId;
        PassUserName = passUserName;
        PassUserMobileNumber = passUserMobileNumber;
        PayableAmount = payableAmount;
        PayableType = payableType;
        IssuedDate = issuedDate;
        ExpiryDate = expiryDate;
        VehicleNumber = vehicleNumber;
        VehicleType = vehicleType;
        GeneratedLocationId = generatedLocationId;
        GeneratedMonthlyPassId = generatedMonthlyPassId;
        IssuedBy = issuedBy;
        IssuedTime = issuedTime;
    }

    public String getIssuedTime() {
        return IssuedTime;
    }

    public void setIssuedTime(String issuedTime) {
        IssuedTime = issuedTime;
    }

    public String getIssuedBy() {
        return IssuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        IssuedBy = issuedBy;
    }

    public String getMonthlyPassId() {
        return MonthlyPassId;
    }

    public void setMonthlyPassId(String monthlyPassId) {
        MonthlyPassId = monthlyPassId;
    }

    public String getPassUserName() {
        return PassUserName;
    }

    public void setPassUserName(String passUserName) {
        PassUserName = passUserName;
    }

    public String getPassUserMobileNumber() {
        return PassUserMobileNumber;
    }

    public void setPassUserMobileNumber(String passUserMobileNumber) {
        PassUserMobileNumber = passUserMobileNumber;
    }

    public String getPayableAmount() {
        return PayableAmount;
    }

    public void setPayableAmount(String payableAmount) {
        PayableAmount = payableAmount;
    }

    public String getPayableType() {
        return PayableType;
    }

    public void setPayableType(String payableType) {
        PayableType = payableType;
    }

    public String getIssuedDate() {
        return IssuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        IssuedDate = issuedDate;
    }

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return VehicleType;
    }

    public void setVehicleType(String vehicleType) {
        VehicleType = vehicleType;
    }

    public String getGeneratedLocationId() {
        return GeneratedLocationId;
    }

    public void setGeneratedLocationId(String generatedLocationId) {
        GeneratedLocationId = generatedLocationId;
    }

    public String getGeneratedMonthlyPassId() {
        return GeneratedMonthlyPassId;
    }

    public void setGeneratedMonthlyPassId(String generatedMonthlyPassId) {
        GeneratedMonthlyPassId = generatedMonthlyPassId;
    }
}
