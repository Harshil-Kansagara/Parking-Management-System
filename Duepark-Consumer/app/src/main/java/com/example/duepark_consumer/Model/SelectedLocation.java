package com.example.duepark_consumer.Model;

public class SelectedLocation {
    private String id, locationName;

    public SelectedLocation() {
    }

    public SelectedLocation(String id, String locationName) {
        this.id = id;
        this.locationName = locationName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
