package com.example.duepark_admin.Model;

public class ActivationState {
    private String id, locationId, acronym, locationName, locationActiveState, parkingId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationActiveState() {
        return locationActiveState;
    }

    public void setLocationActiveState(String locationActiveState) {
        this.locationActiveState = locationActiveState;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public ActivationState(String id, String locationId, String acronym, String locationName, String locationActiveState, String parkingId) {
        this.id = id;
        this.locationId = locationId;
        this.acronym = acronym;
        this.locationName = locationName;
        this.locationActiveState = locationActiveState;
        this.parkingId = parkingId;
    }
}
