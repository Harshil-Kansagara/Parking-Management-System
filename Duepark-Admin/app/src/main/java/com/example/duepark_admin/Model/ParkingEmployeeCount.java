package com.example.duepark_admin.Model;

public class ParkingEmployeeCount {
    private String parkingId, generatedParkingId, parkingAcronym, parkingName, totalSuperAdminCount, totalAdminCount, totalManagerCount, totalValetCount;

    public ParkingEmployeeCount(String parkingId, String generatedParkingId, String parkingAcronym, String parkingName, String totalSuperAdminCount,
                                String totalAdminCount, String totalManagerCount, String totalValetCount) {
        this.parkingId = parkingId;
        this.generatedParkingId = generatedParkingId;
        this.parkingAcronym = parkingAcronym;
        this.parkingName = parkingName;
        this.totalSuperAdminCount = totalSuperAdminCount;
        this.totalAdminCount = totalAdminCount;
        this.totalManagerCount = totalManagerCount;
        this.totalValetCount = totalValetCount;
    }

    public String getTotalSuperAdminCount() {
        return totalSuperAdminCount;
    }

    public void setTotalSuperAdminCount(String totalSuperAdminCount) {
        this.totalSuperAdminCount = totalSuperAdminCount;
    }

    public String getTotalAdminCount() {
        return totalAdminCount;
    }

    public void setTotalAdminCount(String totalAdminCount) {
        this.totalAdminCount = totalAdminCount;
    }

    public String getTotalManagerCount() {
        return totalManagerCount;
    }

    public void setTotalManagerCount(String totalManagerCount) {
        this.totalManagerCount = totalManagerCount;
    }

    public String getTotalValetCount() {
        return totalValetCount;
    }

    public void setTotalValetCount(String totalValetCount) {
        this.totalValetCount = totalValetCount;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
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

    public String getParkingName() {
        return parkingName;
    }

    public void setParkingName(String parkingName) {
        this.parkingName = parkingName;
    }
}
