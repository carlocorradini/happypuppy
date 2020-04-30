package com.unitn.disi.lpsmt.happypuppy.api.entity.error;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConflictError {
    @SerializedName("property")
    @Expose
    public String property;

    @SerializedName("value")
    @Expose
    public String value;

    public ConflictError(String property, String value) {
        this.property = property;
        this.value = value;
    }

    public ConflictError() {
    }
}
