package com.example.duepark_admin.Model;

public class Parking {
    private String id, parkingId;
    private String acronym, parkingName, parkingActiveState;

    public String getParkingActiveState() {
        return parkingActiveState;
    }

    public void setParkingActiveState(String parkingActiveState) {
        this.parkingActiveState = parkingActiveState;
    }

    public Parking() {
    }

    public Parking(String id, String parkingId, String acronym, String parkingName, String parkingActiveState) {
        this.id = id;
        this.parkingId = parkingId;
        this.acronym = acronym;
        this.parkingName = parkingName;
        this.parkingActiveState = parkingActiveState;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getParkingName() {
        return parkingName;
    }

    public void setParkingName(String parkingName) {
        this.parkingName = parkingName;
    }
}
