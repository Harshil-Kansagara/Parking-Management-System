package com.example.duepark_admin.Model;

public class State {
    public int id;
    public String StateName, StateAbbreviation;

    public State(String stateAbbreviation) {
        StateAbbreviation = stateAbbreviation;
    }

    public State(int id, String stateName, String stateAbbreviation) {
        this.id = id;
        StateName = stateName;
        StateAbbreviation = stateAbbreviation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStateName() {
        return StateName;
    }

    public void setStateName(String stateName) {
        StateName = stateName;
    }

    public String getStateAbbreviation() {
        return StateAbbreviation;
    }

    public void setStateAbbreviation(String stateAbbreviation) {
        StateAbbreviation = stateAbbreviation;
    }
}
