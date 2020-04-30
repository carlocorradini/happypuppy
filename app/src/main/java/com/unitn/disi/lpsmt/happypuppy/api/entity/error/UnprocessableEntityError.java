package com.unitn.disi.lpsmt.happypuppy.api.entity.error;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class UnprocessableEntityError {
    @SerializedName("property")
    @Expose
    public String property;

    @SerializedName("value")
    @Expose
    public String value;

    @SerializedName("constraints")
    @Expose
    public Map<String, String> constraints;

    public UnprocessableEntityError(String property, String value, Map<String, String> constraints) {
        this.property = property;
        this.value = value;
        this.constraints = constraints;
    }

    public UnprocessableEntityError() {
    }
}
