package com.example.duepark_admin.Data;

public class LocationListData {
    String location_id, location_name;

    public LocationListData(String location_id, String location_name) {
        this.location_id = location_id;
        this.location_name = location_name;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }
}
