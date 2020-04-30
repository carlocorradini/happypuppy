package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class AnimalPersonality {
    @SerializedName("id")
    @Expose
    public Long id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    public AnimalPersonality(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public AnimalPersonality() {
    }
}
