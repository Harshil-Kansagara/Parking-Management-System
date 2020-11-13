package com.example.duepark_consumer.Model;

public class ParkedVehicleList {
    public String Id, VehicleNumber, PaidAmount, ParkedTime, ParkedDate, VehicleType;

    public ParkedVehicleList(){}

    public ParkedVehicleList(String id, String vehicleNumber, String paidAmount, String parkedTime, String parkedDate, String vehicleType) {
        Id = id;
        VehicleNumber = vehicleNumber;
        PaidAmount = paidAmount;
        ParkedTime = parkedTime;
        ParkedDate = parkedDate;
        VehicleType = vehicleType;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

    public String getPaidAmount() {
        return PaidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        PaidAmount = paidAmount;
    }

    public String getParkedTime() {
        return ParkedTime;
    }

    public void setParkedTime(String parkedTime) {
        ParkedTime = parkedTime;
    }

    public String getParkedDate() {
        return ParkedDate;
    }

    public void setParkedDate(String parkedDate) {
        ParkedDate = parkedDate;
    }

    public String getVehicleType() {
        return VehicleType;
    }

    public void setVehicleType(String vehicleType) {
        VehicleType = vehicleType;
    }
}
