package com.tech.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

import java.util.List;

@ApiModel(value = "AirlineList", description = "Fetch list of available Airlines")
public class ListDataResponse {

    private BaseResponse meta;

    private List<?> data;

    public ListDataResponse() {
    }

    public ListDataResponse(BaseResponse baseResponse, List<?> data) {
        this.meta = baseResponse;
        this.data = data;
    }

    @JsonProperty("meta")
    public BaseResponse getMeta() {
        return meta;
    }


    @JsonProperty("data")
    public List<?> getData() {
        return data;
    }
}