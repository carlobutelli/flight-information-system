package com.tech.api.payloads;

import javax.validation.constraints.NotNull;

public class ArrivalDeparturePayload {
    @NotNull
    private String airportId;

    public ArrivalDeparturePayload() {}

    public ArrivalDeparturePayload(@NotNull String airportId) {
        this.airportId = airportId;
    }

    public String getAirportId() {
        return airportId;
    }

    public void setAirportId(String airportId) {
        this.airportId = airportId;
    }
}
