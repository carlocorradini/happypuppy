package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

/**
 * Animal Breed mapping
 *
 * @author Carlo Corradini
 * @see AnimalSpecie
 * @see Puppy
 */
public class AnimalBreed {
    /**
     * Id of the Animal Breed
     */
    @SerializedName("id")
    @Expose
    public Long id;

    /**
     * Name of the Animal Breed
     */
    @SerializedName("name")
    @Expose
    public String name;

    /**
     * Animal Specie id of the Animal Breed
     *
     * @see AnimalSpecie
     */
    @SerializedName("specie")
    @Expose
    public Long specie;

    /**
     * Creation {@link LocalDateTime} of the Animal Breed
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the Animal Breed
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct an Animal Breed class
     *
     * @param id        Id of the Animal Breed
     * @param name      Name of the Animal Breed
     * @param specie    Animal Specie id of the Animal Breed
     * @param createdAt Creation {@link LocalDateTime} of the Animal Breed
     * @param updatedAt Update {@link LocalDateTime} of the Animal Breed
     */
    public AnimalBreed(Long id, String name, Long specie, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.specie = specie;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty Animal Breed class
     */
    public AnimalBreed() {
    }
}
