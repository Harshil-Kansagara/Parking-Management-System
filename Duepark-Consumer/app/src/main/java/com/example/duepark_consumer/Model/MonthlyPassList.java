package com.example.duepark_consumer.Model;

public class MonthlyPassList {
    public String MonthlyPassId, GeneratedLocationId, GeneratedMonthlyPassId, PassUserName;

    public MonthlyPassList(String monthlyPassId, String generatedLocationId, String generatedMonthlyPassId, String passUserName) {
        MonthlyPassId = monthlyPassId;
        GeneratedLocationId = generatedLocationId;
        GeneratedMonthlyPassId = generatedMonthlyPassId;
        PassUserName = passUserName;
    }

    public String getMonthlyPassId() {
        return MonthlyPassId;
    }

    public void setMonthlyPassId(String monthlyPassId) {
        MonthlyPassId = monthlyPassId;
    }

    public String getGeneratedLocationId() {
        return GeneratedLocationId;
    }

    public void setGeneratedLocationId(String generatedLocationId) {
        GeneratedLocationId = generatedLocationId;
    }

    public String getGeneratedMonthlyPassId() {
        return GeneratedMonthlyPassId;
    }

    public void setGeneratedMonthlyPassId(String generatedMonthlyPassId) {
        GeneratedMonthlyPassId = generatedMonthlyPassId;
    }

    public String getPassUserName() {
        return PassUserName;
    }

    public void setPassUserName(String passUserName) {
        PassUserName = passUserName;
    }
}
