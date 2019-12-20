package com.tech.model;

import java.util.List;

public class DelayedCancelledArrays {
    private List<Integer> delayedFlights;
    private List<Integer> cancelledFlights;

    public DelayedCancelledArrays() {}

    public DelayedCancelledArrays(List<Integer> delayedFlights, List<Integer> cancelledFlights) {
        this.delayedFlights = delayedFlights;
        this.cancelledFlights = cancelledFlights;
    }

    public List<Integer> getDelayedFlights() {
        return delayedFlights;
    }

    public void setDelayedFlights(List<Integer> delayedFlights) {
        this.delayedFlights = delayedFlights;
    }

    public List<Integer> getCancelledFlights() {
        return cancelledFlights;
    }

    public void setCancelledFlights(List<Integer> cancelledFlights) {
        this.cancelledFlights = cancelledFlights;
    }
}