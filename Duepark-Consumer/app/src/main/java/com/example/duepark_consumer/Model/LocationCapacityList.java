package com.example.duepark_consumer.Model;

public class LocationCapacityList {
    public String Id, LocationName, CarCapacity, BikeCapacity, OccupiedCarCapacity, OccupiedBikeCapacity;

    public LocationCapacityList(String id, String locationName, String carCapacity, String bikeCapacity, String occupiedCarCapacity,
                                String occupiedBikeCapacity) {
        Id = id;
        LocationName = locationName;
        CarCapacity = carCapacity;
        BikeCapacity = bikeCapacity;
        OccupiedCarCapacity = occupiedCarCapacity;
        OccupiedBikeCapacity = occupiedBikeCapacity;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public String getCarCapacity() {
        return CarCapacity;
    }

    public void setTotalCarCapacity(String carCapacity) {
        CarCapacity = carCapacity;
    }

    public String getBikeCapacity() {
        return BikeCapacity;
    }

    public void setBikeCapacity(String bikeCapacity) {
        BikeCapacity = bikeCapacity;
    }

    public String getOccupiedCarCapacity() {
        return OccupiedCarCapacity;
    }

    public void setOccupiedCarCapacity(String occupiedCarCapacity) {
        OccupiedCarCapacity = occupiedCarCapacity;
    }

    public String getOccupiedBikeCapacity() {
        return OccupiedBikeCapacity;
    }

    public void setOccupiedBikeCapacity(String occupiedBikeCapacity) {
        OccupiedBikeCapacity = occupiedBikeCapacity;
    }
}
