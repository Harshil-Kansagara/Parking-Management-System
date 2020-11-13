package com.example.duepark_admin.Model;

public class LocationEmployeeCount {
    private String locationId, generatedLocationId, locationName, locationManagerCount, locationValetCount, generatedParkingId, parkingAcronym;

    public LocationEmployeeCount(String locationId, String generatedLocationId, String locationName, String locationManagerCount,
                                 String locationValetCount, String generatedParkingId, String parkingAcronym) {
        this.locationId = locationId;
        this.generatedLocationId = generatedLocationId;
        this.locationName = locationName;
        this.locationManagerCount = locationManagerCount;
        this.locationValetCount = locationValetCount;
        this.generatedParkingId = generatedParkingId;
        this.parkingAcronym = parkingAcronym;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getGeneratedLocationId() {
        return generatedLocationId;
    }

    public void setGeneratedLocationId(String generatedLocationId) {
        this.generatedLocationId = generatedLocationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationManagerCount() {
        return locationManagerCount;
    }

    public void setLocationManagerCount(String locationManagerCount) {
        this.locationManagerCount = locationManagerCount;
    }

    public String getLocationValetCount() {
        return locationValetCount;
    }

    public void setLocationValetCount(String locationValetCount) {
        this.locationValetCount = locationValetCount;
    }

    public String getGeneratedParkingId() {
        return generatedParkingId;
    }

    public void setGeneratedParkingId(String generatedParkingId) {
        this.generatedParkingId = generatedParkingId;
    }

    public String getParkingAcronym() {
        return parkingAcronym;
    }

    public void setParkingAcronym(String parkingAcronym) {
        this.parkingAcronym = parkingAcronym;
    }
}
