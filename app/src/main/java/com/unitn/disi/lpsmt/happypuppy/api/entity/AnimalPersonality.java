package com.unitn.disi.lpsmt.happypuppy.api.entity;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Animal Personality mapping
 *
 * @author Carlo Corradini
 * @see Puppy
 */
public class AnimalPersonality {
    /**
     * Id of the Animal Personality
     */
    @SerializedName("id")
    @Expose
    public Long id;

    /**
     * Name of the Animal Personality
     */
    @SerializedName("name")
    @Expose
    public String name;

    /**
     * Creation {@link LocalDateTime} of the Animal Personality
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the Animal Personality
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct an Animal Personality class
     *
     * @param id        Id of the Animal Personality
     * @param name      Name of the Animal Personality
     * @param createdAt Creation {@link LocalDateTime} of the Animal Personality
     * @param updatedAt Update {@link LocalDateTime} of the Animal Personality
     */
    public AnimalPersonality(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty Animal Personality class
     */
    public AnimalPersonality() {
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnimalPersonality animalPersonality = (AnimalPersonality) obj;
        return Objects.equals(id, animalPersonality.id);
    }
}
