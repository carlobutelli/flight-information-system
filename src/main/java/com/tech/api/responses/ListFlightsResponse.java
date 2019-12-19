package com.tech.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tech.model.Flight;
import io.swagger.annotations.ApiModel;

import java.util.List;

@ApiModel(value = "Lista Flights Response", description = "Response for list of flights")
public class ListFlightsResponse {

    private BaseResponse meta;

    private List<Flight> data;

    public ListFlightsResponse() {
    }

    public ListFlightsResponse(BaseResponse baseResponse, List<Flight> data) {
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