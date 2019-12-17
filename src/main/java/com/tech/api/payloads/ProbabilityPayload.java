package com.tech.api.payloads;

import javax.validation.constraints.NotNull;

public class ProbabilityPayload {
    @NotNull
    private int delayedProbability;
    @NotNull
    private int cancelledProbability;

    public ProbabilityPayload() {}

    public int getDelayedProbability() {
        return delayedProbability;
    }

    public void setDelayedProbability(int delayedProbability) {
        this.delayedProbability = delayedProbability;
    }

    public int getCancelledProbability() {
        return cancelledProbability;
    }

    public void setCancelledProbability(int cancelledProbability) {
        this.cancelledProbability = cancelledProbability;
    }
}
