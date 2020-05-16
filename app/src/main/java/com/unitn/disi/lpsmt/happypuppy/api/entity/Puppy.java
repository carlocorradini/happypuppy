package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Puppy mapping
 *
 * @author Carlo Corradini
 */
public class Puppy {
    /**
     * Puppy Genders
     */
    public enum Gender {
        /**
         * Male Gender
         */
        @SerializedName("male")
        MALE("male"),

        /**
         * Female Gender
         */
        @SerializedName("female")
        FEMALE("female");

        /**
         * Value of the Gender
         */
        private final String value;

        /**
         * Construct a Gender enum
         *
         * @param value Value of the Gender
         */
        Gender(String value) {
            this.value = value;
        }

        /**
         * Return the value of the current Gender
         *
         * @return Gender value
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * Id of the Puppy
     */
    @SerializedName("id")
    @Expose
    public Long id;

    /**
     * Name of the Puppy
     */
    @SerializedName("name")
    @Expose
    public String name;

    /**
     * Gender of the Puppy
     *
     * @see Gender
     */
    @SerializedName("gender")
    @Expose
    public Gender gender;

    /**
     * Date of Birth of the Puppy
     */
    @SerializedName("date_of_birth")
    @Expose
    public LocalDate dateOfBirth;

    /**
     * Weight in grams of the Puppy
     */
    @SerializedName("weight")
    @Expose
    public Long weight;

    /**
     * Avatar image url of the User
     */
    @SerializedName("avatar")
    @Expose
    public URI avatar;

    /**
     * Owner id of the Puppy
     *
     * @see User
     */
    @SerializedName("user")
    @Expose
    public UUID user;

    /**
     * Id of the Animal Specie of the Puppy
     *
     * @see AnimalSpecie
     */
    @SerializedName("specie")
    @Expose
    public Long specie;

    /**
     * List of Breed ids of the Puppy
     *
     * @see AnimalBreed
     */
    @SerializedName("breeds")
    @Expose
    public List<Long> breeds;

    /**
     * List of Personality ids of the Puppy
     *
     * @see AnimalPersonality
     */
    @SerializedName("personalities")
    @Expose
    public List<Long> personalities;

    /**
     * Creation {@link LocalDateTime} of the Puppy
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the Puppy
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct a Puppy class
     *
     * @param id            Id of the Puppy
     * @param name          Name of the Puppy
     * @param gender        Gender of the Puppy
     * @param dateOfBirth   Date of Birth of the Puppy
     * @param weight        Weight in grams of the Puppy
     * @param avatar        Avatar image url of the Puppy
     * @param user          Owner id of the Puppy
     * @param specie        Specie of the Puppy
     * @param breeds        List of Breed ids of the Puppy
     * @param personalities List of Personality ids of the Puppy
     * @param createdAt     Creation {@link LocalDateTime} of the Puppy
     * @param updatedAt     Update {@link LocalDateTime} of the Puppy
     */
    public Puppy(Long id, String name, Gender gender, LocalDate dateOfBirth, Long weight, URI avatar, UUID user, Long specie, List<Long> breeds, List<Long> personalities, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.weight = weight;
        this.avatar = avatar;
        this.user = user;
        this.specie = specie;
        this.breeds = breeds;
        this.personalities = personalities;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty Puppy class
     */
    public Puppy() {
    }
}
