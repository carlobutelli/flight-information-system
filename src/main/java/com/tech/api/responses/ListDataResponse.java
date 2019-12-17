package com.tech.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

import java.util.List;

@ApiModel(value = "Lista Data Response", description = "Response for list of objects")
public class ListDataResponse {

    private BaseResponse meta;

    private List<Object> data;

    public ListDataResponse() {
    }

    public ListDataResponse(BaseResponse baseResponse, List<Object> data) {
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