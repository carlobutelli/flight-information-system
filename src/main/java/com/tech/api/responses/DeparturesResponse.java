package com.tech.api.responses;

import javax.validation.constraints.NotNull;

public class DeparturesResponse {
    @NotNull
    private String flight;

    @NotNull
    private String destination;

    @NotNull
    private String scheduledTime;

    @NotNull
    private String estimatedTime;

    @NotNull
    private String actualTime;

    @NotNull
    private String status;

    public DeparturesResponse() {}

    public DeparturesResponse(@NotNull String flight,
                              @NotNull String destination,
                              @NotNull String scheduledTime,
                              @NotNull String estimatedTime,
                              @NotNull String actualTime,
                              @NotNull String status) {
        this.flight = flight;
        this.destination = destination;
        this.scheduledTime = scheduledTime;
        this.estimatedTime = estimatedTime;
        this.actualTime = actualTime;
        this.status = status;
    }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getActualTime() {
        return actualTime;
    }

    public void setActualTime(String actualTime) {
        this.actualTime = actualTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
