package com.tech.api.payloads;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class ProbabilityPayload {
    @NotNull
    @Range(min = 0, max = 1)
    @ApiModelProperty(example = "0.0")
    private double delayedProbability;

    @NotNull
    @Range(min = 0, max = 1)
    @ApiModelProperty(example = "0.0")
    private double cancelledProbability;

    public ProbabilityPayload() {}

    public double getDelayedProbability() {
        return delayedProbability;
    }

    public void setDelayedProbability(double delayedProbability) {
        this.delayedProbability = delayedProbability;
    }

    public double getCancelledProbability() {
        return cancelledProbability;
    }

    public void setCancelledProbability(double cancelledProbability) {
        this.cancelledProbability = cancelledProbability;
    }
}
