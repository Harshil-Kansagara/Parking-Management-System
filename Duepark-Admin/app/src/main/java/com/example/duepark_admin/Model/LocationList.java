package com.example.duepark_admin.Model;

public class LocationList {
    public String LocationId, LocationName, GeneratedLocationId, LocationActiveState, GeneratedParkingId, ParkingAcronym;

    public LocationList(String locationId, String locationName, String generatedLocationId, String locationActiveState, String generatedParkingId, String parkingAcronym) {
        LocationId = locationId;
        LocationName = locationName;
        GeneratedLocationId = generatedLocationId;
        LocationActiveState = locationActiveState;
        GeneratedParkingId = generatedParkingId;
        ParkingAcronym = parkingAcronym;
    }

    public String getLocationActiveState() {
        return LocationActiveState;
    }

    public void setLocationActiveState(String locationActiveState) {
        LocationActiveState = locationActiveState;
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
