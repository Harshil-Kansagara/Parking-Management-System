package com.example.duepark_consumer.Model;

public class AssignVehicleNumberListForMonthlyPass {

    public String VehicleNumberMonthlyPassId, VehicleType, VehicleNumber;

    public AssignVehicleNumberListForMonthlyPass(String vehicleNumberMonthlyPassId, String vehicleType, String vehicleNumber) {
        VehicleNumberMonthlyPassId = vehicleNumberMonthlyPassId;
        VehicleType = vehicleType;
        VehicleNumber = vehicleNumber;
    }

    public String getVehicleNumberMonthlyPassId() {
        return VehicleNumberMonthlyPassId;
    }

    public void setVehicleNumberMonthlyPassId(String vehicleNumberMonthlyPassId) {
        VehicleNumberMonthlyPassId = vehicleNumberMonthlyPassId;
    }

    public String getVehicleType() {
        return VehicleType;
    }

    public void setVehicleType(String vehicleType) {
        VehicleType = vehicleType;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }
}
