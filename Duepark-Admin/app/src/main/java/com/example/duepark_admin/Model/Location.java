package com.example.duepark_admin.Model;

public class Location {
    public String LocationId, GeneratedLocationId, LocationName, LocationType, LocationAddress, LocationDate, LocationTime, LocationActiveState;
    public String OpenTime, CloseTime, CarChargeType, CarFixHour, CarFixHourRate, CarChargesOption, CarChargesOptionRate, CarCapacity;
    public String BikeChargeType, BikeFixHour, BikeFixHourRate, BikeChargesOption, BikeChargesOptionRate, BikeCapacity;
    public String CarMonthlyPass, BikeMonthlyPass, LocationDetailId;

    public Location() { }

    public Location(String locationId, String generatedLocationId, String locationName, String locationType, String locationAddress,
                    String locationDate, String locationTime, String locationActiveState) {
        LocationId = locationId;
        GeneratedLocationId = generatedLocationId;
        LocationName = locationName;
        LocationType = locationType;
        LocationAddress = locationAddress;
        LocationDate = locationDate;
        LocationTime = locationTime;
        LocationActiveState = locationActiveState;
    }

    public Location(String locationId, String generatedLocationId, String locationName, String locationType, String locationAddress, String locationDate,
                    String locationTime, String locationActiveState, String locationDetailId, String openTime, String closeTime, String carChargeType, String carFixHour,
                    String carFixHourRate, String carChargesOption, String carChargesOptionRate, String carCapacity, String bikeChargeType,
                    String bikeFixHour, String bikeFixHourRate, String bikeChargesOption, String bikeChargesOptionRate, String bikeCapacity,
                    String carMonthlyPass, String bikeMonthlyPass) {
        LocationId = locationId;
        GeneratedLocationId = generatedLocationId;
        LocationName = locationName;
        LocationType = locationType;
        LocationAddress = locationAddress;
        LocationDate = locationDate;
        LocationTime = locationTime;
        LocationActiveState = locationActiveState;
        OpenTime = openTime;
        CloseTime = closeTime;
        CarChargeType = carChargeType;
        CarFixHour = carFixHour;
        CarFixHourRate = carFixHourRate;
        CarChargesOption = carChargesOption;
        CarChargesOptionRate = carChargesOptionRate;
        CarCapacity = carCapacity;
        BikeChargeType = bikeChargeType;
        BikeFixHour = bikeFixHour;
        BikeFixHourRate = bikeFixHourRate;
        BikeChargesOption = bikeChargesOption;
        BikeChargesOptionRate = bikeChargesOptionRate;
        BikeCapacity = bikeCapacity;
        CarMonthlyPass = carMonthlyPass;
        BikeMonthlyPass = bikeMonthlyPass;
        LocationDetailId = locationDetailId;
    }

    public String getCarFixHour() {
        return CarFixHour;
    }

    public void setCarFixHour(String carFixHour) {
        CarFixHour = carFixHour;
    }

    public String getCarFixHourRate() {
        return CarFixHourRate;
    }

    public void setCarFixHourRate(String carFixHourRate) {
        CarFixHourRate = carFixHourRate;
    }

    public String getCarChargesOption() {
        return CarChargesOption;
    }

    public void setCarChargesOption(String carChargesOption) {
        CarChargesOption = carChargesOption;
    }

    public String getCarChargesOptionRate() {
        return CarChargesOptionRate;
    }

    public void setCarChargesOptionRate(String carChargesOptionRate) {
        CarChargesOptionRate = carChargesOptionRate;
    }

    public String getBikeFixHour() {
        return BikeFixHour;
    }

    public void setBikeFixHour(String bikeFixHour) {
        BikeFixHour = bikeFixHour;
    }

    public String getBikeFixHourRate() {
        return BikeFixHourRate;
    }

    public void setBikeFixHourRate(String bikeFixHourRate) {
        BikeFixHourRate = bikeFixHourRate;
    }

    public String getBikeChargesOption() {
        return BikeChargesOption;
    }

    public void setBikeChargesOption(String bikeChargesOption) {
        BikeChargesOption = bikeChargesOption;
    }

    public String getBikeChargesOptionRate() {
        return BikeChargesOptionRate;
    }

    public void setBikeChargesOptionRate(String bikeChargesOptionRate) {
        BikeChargesOptionRate = bikeChargesOptionRate;
    }

    public String getLocationDetailId() {
        return LocationDetailId;
    }

    public void setLocationDetailId(String locationDetailId) {
        LocationDetailId = locationDetailId;
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

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public String getLocationType() {
        return LocationType;
    }

    public void setLocationType(String locationType) {
        LocationType = locationType;
    }

    public String getLocationAddress() {
        return LocationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        LocationAddress = locationAddress;
    }

    public String getLocationDate() {
        return LocationDate;
    }

    public void setLocationDate(String locationDate) {
        LocationDate = locationDate;
    }

    public String getLocationTime() {
        return LocationTime;
    }

    public void setLocationTime(String locationTime) {
        LocationTime = locationTime;
    }

    public String getLocationActiveState() {
        return LocationActiveState;
    }

    public void setLocationActiveState(String locationActiveState) {
        LocationActiveState = locationActiveState;
    }

    public String getOpenTime() {
        return OpenTime;
    }

    public void setOpenTime(String openTime) {
        OpenTime = openTime;
    }

    public String getCloseTime() {
        return CloseTime;
    }

    public void setCloseTime(String closeTime) {
        CloseTime = closeTime;
    }

    public String getCarChargeType() {
        return CarChargeType;
    }

    public void setCarChargeType(String carChargeType) {
        CarChargeType = carChargeType;
    }

    public String getCarCapacity() {
        return CarCapacity;
    }

    public void setCarCapacity(String carCapacity) {
        CarCapacity = carCapacity;
    }

    public String getBikeChargeType() {
        return BikeChargeType;
    }

    public void setBikeChargeType(String bikeChargeType) {
        BikeChargeType = bikeChargeType;
    }

    public String getBikeCapacity() {
        return BikeCapacity;
    }

    public void setBikeCapacity(String bikeCapacity) {
        BikeCapacity = bikeCapacity;
    }

    public String getCarMonthlyPass() {
        return CarMonthlyPass;
    }

    public void setCarMonthlyPass(String carMonthlyPass) {
        CarMonthlyPass = carMonthlyPass;
    }

    public String getBikeMonthlyPass() {
        return BikeMonthlyPass;
    }

    public void setBikeMonthlyPass(String bikeMonthlyPass) {
        BikeMonthlyPass = bikeMonthlyPass;
    }
}
