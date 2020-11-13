package com.example.duepark_consumer.Model;

public class IncomeList {
    public String GeneratedParkingId, ParkingAcronym, LocationId, LocationName, GeneratedLocationId;

    public IncomeList(){}

    public IncomeList(String locationId, String locationName, String generatedLocationId, String generatedParkingId, String parkingAcronym) {
        LocationId = locationId;
        LocationName = locationName;
        GeneratedLocationId = generatedLocationId;
        GeneratedParkingId = generatedParkingId;
        ParkingAcronym = parkingAcronym;
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

    public String getLocationId() {
        return LocationId;
    }

    public void setLocationId(String locationId) {
        LocationId = locationId;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public String getGeneratedLocationId() {
        return GeneratedLocationId;
    }

    public void setGeneratedLocationId(String generatedLocationId) {
        GeneratedLocationId = generatedLocationId;
    }
}
