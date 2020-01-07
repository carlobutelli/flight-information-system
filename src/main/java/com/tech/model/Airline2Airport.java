package com.tech.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(Airline2AirportId.class)
@ApiModel(value = "Airline2Airport", description = "Represents the many2many relation between airline and airport")
public class Airline2Airport implements Serializable {

    @Id
    @ApiModelProperty(example = "1111")
    private int airlineId;

    @Id
    @ApiModelProperty(example = "FCO")
    private String airportId;

    @Column(nullable = false)
    @ApiModelProperty(example = "3")
    private int numOfArrivals;

    @Column(nullable = false)
    @ApiModelProperty(example = "3")
    private int numOfDepartures;

    @Column(nullable = false)
    @ColumnDefault("false")
    @ApiModelProperty(example = "false")
    private boolean generated;

    public Airline2Airport() {}

    public Airline2Airport(int airlineId, String airportId, int numOfArrivals, int numOfDepartures) {
        this.airlineId = airlineId;
        this.airportId = airportId;
        this.numOfArrivals = numOfArrivals;
        this.numOfDepartures = numOfDepartures;
    }

    public int getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    public String getAirportId() {
        return airportId;
    }

    public void setAirportId(String airportId) {
        this.airportId = airportId;
    }

    public int getNumOfArrivals() {
        return numOfArrivals;
    }

    public void setNumOfArrivals(int numOfArrivals) {
        this.numOfArrivals = numOfArrivals;
    }

    public int getNumOfDepartures() {
        return numOfDepartures;
    }

    public void setNumOfDepartures(int numOfDepartures) {
        this.numOfDepartures = numOfDepartures;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Airline2Airport that = (Airline2Airport) o;
        return Objects.equals(airlineId, that.airlineId) &&
                Objects.equals(airportId, that.airportId) &&
                Objects.equals(numOfArrivals, that.numOfArrivals) &&
                Objects.equals(numOfDepartures, that.numOfDepartures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(airlineId, airportId, numOfArrivals, numOfDepartures);
    }

}