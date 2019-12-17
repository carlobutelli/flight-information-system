package com.tech.api.responses;

import java.util.List;

public class SimulationResponse {

    private BaseResponse meta;

    private List<ArrivalsResponse> arrivals;

    private List<DeparturesResponse> departures;

    public SimulationResponse() { }

    public SimulationResponse(BaseResponse meta,
                              List<ArrivalsResponse> arrivals,
                              List<DeparturesResponse> departures) {
        this.meta = meta;
        this.arrivals = arrivals;
        this.departures = departures;
    }

    public BaseResponse getMeta() {
        return meta;
    }

    public void setMeta(BaseResponse meta) {
        this.meta = meta;
    }

    public List<ArrivalsResponse> getArrivals() {
        return arrivals;
    }

    public void setArrivals(List<ArrivalsResponse> arrivals) {
        this.arrivals = arrivals;
    }

    public List<DeparturesResponse> getDepartures() {
        return departures;
    }

    public void setDepartures(List<DeparturesResponse> departures) {
        this.departures = departures;
    }
}
