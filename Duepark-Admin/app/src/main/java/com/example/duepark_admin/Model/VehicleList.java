package com.example.duepark_admin.Model;

public class VehicleList {
    public String ParkedVehicleId, VehicleNumber, VehicleType, TotalAmount, TotalTime, Date;

    public VehicleList(String parkedVehicleId, String vehicleNumber, String vehicleType, String totalAmount, String totalTime, String date) {
        ParkedVehicleId = parkedVehicleId;
        VehicleNumber = vehicleNumber;
        VehicleType = vehicleType;
        TotalAmount = totalAmount;
        TotalTime = totalTime;
        Date = date;
    }

    public String getParkedVehicleId() {
        return ParkedVehicleId;
    }

    public void setParkedVehicleId(String parkedVehicleId) {
        ParkedVehicleId = parkedVehicleId;
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

    public String getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        TotalAmount = totalAmount;
    }

    public String getTotalTime() {
        return TotalTime;
    }

    public void setTotalTime(String totalTime) {
        TotalTime = totalTime;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
