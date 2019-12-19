package com.tech.api.payloads;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class ProbabilityPayload {
    @NotNull
    @Range(min = 0, max = 1)
    @ApiModelProperty(example = "0.0")
    private float delayedProbability;

    @NotNull
    @Range(min = 0, max = 1)
    @ApiModelProperty(example = "0.0")
    private float cancelledProbability;

    public ProbabilityPayload() {}

    public float getDelayedProbability() {
        return delayedProbability;
    }

    public void setDelayedProbability(float delayedProbability) {
        this.delayedProbability = delayedProbability;
    }

    public float getCancelledProbability() {
        return cancelledProbability;
    }

    public void setCancelledProbability(float cancelledProbability) {
        this.cancelledProbability = cancelledProbability;
    }
}
