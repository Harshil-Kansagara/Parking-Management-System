package com.example.duepark_consumer.Model;

public class LocationListWithGeneratedParkedVehicleId {
    public String GeneratedParkedVehicleId, LocationId, GeneratedLocationId, GeneratedParkingId, ParkingAcronym;

    public LocationListWithGeneratedParkedVehicleId(String generatedParkedVehicleId, String locationId,
                                                    String generatedLocationId, String generatedParkingId, String parkingAcronym) {
        GeneratedParkedVehicleId = generatedParkedVehicleId;
        LocationId = locationId;
        GeneratedLocationId = generatedLocationId;
        GeneratedParkingId = generatedParkingId;
        ParkingAcronym = parkingAcronym;
    }

    public String getGeneratedParkedVehicleId() {
        return GeneratedParkedVehicleId;
    }

    public void setGeneratedParkedVehicleId(String generatedParkedVehicleId) {
        GeneratedParkedVehicleId = generatedParkedVehicleId;
    }

    public String getLocationId() {
        return LocationId;
    }

    public void setLocationId(String locationId) {
        LocationId = locationId;
    }

    public String getGeneratedLocationId() {
        return GeneratedLocationId;
    }

    public void setGeneratedLocationId(String generatedLocationId) {
        GeneratedLocationId = generatedLocationId;
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
}
