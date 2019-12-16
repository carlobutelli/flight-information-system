package com.tech.api.payloads;

import javax.validation.constraints.NotNull;

public class ProbabilityPayload {
    @NotNull
    private int pDelayed;
    @NotNull
    private int pCancelled;

    public ProbabilityPayload() {}

    public int getpDelayed() {
        return pDelayed;
    }

    public void setpDelayed(int pDelayed) {
        this.pDelayed = pDelayed;
    }

    public int getpCancelled() {
        return pCancelled;
    }

    public void setpCancelled(int pCancelled) {
        this.pCancelled = pCancelled;
    }
}
