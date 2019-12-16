package com.tech.model;

import java.io.Serializable;
import java.util.Objects;


public class Airline2AirportId implements Serializable {

    private int airlineId;
    private String airportId;

    public Airline2AirportId() {}

    public Airline2AirportId(int airlineId, String airportId) {
        this.airlineId = airlineId;
        this.airportId = airportId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Airline2AirportId that = (Airline2AirportId) o;
        return Objects.equals(airlineId, that.airlineId) &&
                Objects.equals(airportId, that.airportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(airlineId, airportId);
    }

}