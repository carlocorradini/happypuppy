package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnimalBreed {
    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("specie")
    @Expose
    private Long specie;

    public AnimalBreed(Long id, String name, Long specie) {
        this.id = id;
        this.name = name;
        this.specie = specie;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSpecie() {
        return specie;
    }

    public void setSpecie(Long specie) {
        this.specie = specie;
    }
}
