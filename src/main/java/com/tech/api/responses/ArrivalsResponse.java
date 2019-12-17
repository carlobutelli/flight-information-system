package com.tech.api.responses;

import com.tech.model.Flight;

import javax.validation.constraints.NotNull;

public class ArrivalsResponse {
    @NotNull
    private String flight;

    @NotNull
    private String source;

    @NotNull
    private String scheduledTime;

    @NotNull
    private String estimatedTime;

    @NotNull
    private String actualTime;

    @NotNull
    private Flight.StatusEnum status;

    public ArrivalsResponse() {}

    public ArrivalsResponse(@NotNull String flight,
                            @NotNull String source,
                            @NotNull String scheduledTime,
                            @NotNull String estimatedTime,
                            @NotNull String actualTime,
                            @NotNull Flight.StatusEnum status) {
        this.flight = flight;
        this.source = source;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public Flight.StatusEnum getStatus() {
        return status;
    }

    public void setStatus(Flight.StatusEnum status) {
        this.status = status;
    }
}