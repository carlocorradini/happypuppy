package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

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

    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    public AnimalBreed(Long id, String name, Long specie, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.specie = specie;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public AnimalBreed() {
    }
}
