package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnimalSpecie {
    @SerializedName("id")
    @Expose
    public Long id;

    @SerializedName("name")
    @Expose
    public String name;

    public AnimalSpecie(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public AnimalSpecie() {}
}
