package com.tech.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "ErrorResponse", description = "response object for errors")
public class ErrorResponse{

    private BaseResponse meta;

    public ErrorResponse() {}

    public ErrorResponse(BaseResponse response) {
        this.meta = response;
    }

    @JsonProperty("meta")
    public BaseResponse getMeta() {
        return meta;
    }

    public void setMeta(BaseResponse meta) {
        this.meta = meta;
    }
}