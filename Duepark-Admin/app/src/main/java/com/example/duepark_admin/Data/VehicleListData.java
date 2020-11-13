package com.example.duepark_admin.Data;

public class VehicleListData {

    String number, fair, inTime, outTime, date;

    public VehicleListData(String number, String fair, String inTime, String outTime, String date) {
        this.number = number;
        this.fair = fair;
        this.inTime = inTime;
        this.outTime = outTime;
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFair() {
        return fair;
    }

    public void setFair(String fair) {
        this.fair = fair;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
