package com.example.duepark_consumer.Model;

public class LocationListWithMonthlyPass {
    public String LocationId, LocationName, GeneratedLocationId, LocationActiveState, GeneratedParkingId, ParkingAcronym, BikeMonthlyPassRate, CarMonthlyPassRate;

    public LocationListWithMonthlyPass(String locationId, String locationName, String generatedLocationId, String locationActiveState, String generatedParkingId,
                                       String parkingAcronym, String carMonthlyPassRate, String bikeMonthlyPassRate) {
        LocationId = locationId;
        LocationName = locationName;
        GeneratedLocationId = generatedLocationId;
        LocationActiveState = locationActiveState;
        GeneratedParkingId = generatedParkingId;
        ParkingAcronym = parkingAcronym;
        BikeMonthlyPassRate = bikeMonthlyPassRate;
        CarMonthlyPassRate = carMonthlyPassRate;
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

    public String getLocationActiveState() {
        return LocationActiveState;
    }

    public void setLocationActiveState(String locationActiveState) {
        LocationActiveState = locationActiveState;
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

    public String getBikeMonthlyPassRate() {
        return BikeMonthlyPassRate;
    }

    public void setBikeMonthlyPassRate(String bikeMonthlyPassRate) {
        BikeMonthlyPassRate = bikeMonthlyPassRate;
    }

    public String getCarMonthlyPassRate() {
        return CarMonthlyPassRate;
    }

    public void setCarMonthlyPassRate(String carMonthlyPassRate) {
        CarMonthlyPassRate = carMonthlyPassRate;
    }
}
