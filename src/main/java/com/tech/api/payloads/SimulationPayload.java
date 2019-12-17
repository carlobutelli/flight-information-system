package com.tech.api.payloads;

import javax.validation.constraints.NotNull;

public class SimulationPayload {

    @NotNull
    private String airportId;

    private String currentTime;

    public SimulationPayload(@NotNull String airportId, String currentTime) {
        this.airportId = airportId;
        this.currentTime = currentTime;
    }

    public String getAirportId() {
        return airportId;
    }

    public void setAirportId(String airportId) {
        this.airportId = airportId;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public SimulationPayload() {}

}
