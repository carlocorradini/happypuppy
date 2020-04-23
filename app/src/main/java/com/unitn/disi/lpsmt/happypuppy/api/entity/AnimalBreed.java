package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnimalBreed {
    @SerializedName("id")
    @Expose
    public Long id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("specie")
    @Expose
    public Long specie;

    public AnimalBreed(Long id, String name, Long specie) {
        this.id = id;
        this.name = name;
        this.specie = specie;
    }

    public AnimalBreed() {}
}
