package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

/**
 * Animal Specie mapping
 *
 * @author Carlo Corradini
 * @see Puppy
 */
public class AnimalSpecie {
    /**
     * Id of the Animal Specie
     */
    @SerializedName("id")
    @Expose
    public Long id;

    /**
     * Name of the Animal Specie
     */
    @SerializedName("name")
    @Expose
    public String name;

    /**
     * Creation {@link LocalDateTime} of the Animal Specie
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the Animal Specie
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct an Animal Specie class
     *
     * @param id        Id of the Animal Specie
     * @param name      Name of the Animal Specie
     * @param createdAt Creation {@link LocalDateTime} of the Animal Specie
     * @param updatedAt Update {@link LocalDateTime} of the Animal Specie
     */
    public AnimalSpecie(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty Animal Specie class
     */
    public AnimalSpecie() {
    }
}
