package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

/**
 * Animal Place mapping
 *
 * @author Carlo Corradini
 */
public class AnimalPlace {
    /**
     * Animal Place Types
     */
    public enum Type {
        /**
         * Park Type
         */
        @SerializedName("park")
        PARK("park"),

        /**
         * Shop Type
         */
        @SerializedName("shop")
        SHOP("shop"),

        /**
         * Veterinary Type
         */
        @SerializedName("veterinary")
        VETERINARY("veterinary"),

        /**
         * Grooming Type
         */
        @SerializedName("grooming")
        GROOMING("grooming");

        /**
         * Value of the Type
         */
        private final String value;

        /**
         * Construct a Type enum
         *
         * @param value Value of the Type
         */
        Type(String value) {
            this.value = value;
        }

        /**
         * Return the value of the current Type
         *
         * @return Type value
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * Id of the Animal Place
     */
    @SerializedName("id")
    @Expose
    public Long id;

    /**
     * Name of the Animal Place
     */
    @SerializedName("name")
    @Expose
    public String name;

    /**
     * Type of the Animal Place
     */
    @SerializedName("type")
    @Expose
    public Type type;

    /**
     * Latitude of the Animal Place
     */
    @SerializedName("latitude")
    @Expose
    public Double latitude;

    /**
     * Longitude of the Animal Place
     */
    @SerializedName("longitude")
    @Expose
    public Double longitude;

    /**
     * Creation {@link LocalDateTime} of the Animal Place
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the Animal Place
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct an Animal Place class
     *
     * @param id        Id of the Animal Place
     * @param name      Name of the Animal Place
     * @param type      Type of the Animal Place
     * @param latitude  Latitude of the Animal Place
     * @param longitude Longitude of the Animal Place
     * @param createdAt Creation {@link LocalDateTime} of the Animal Place
     * @param updatedAt Update {@link LocalDateTime} of the Animal Place
     */
    public AnimalPlace(Long id, String name, Type type, Double latitude, Double longitude, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty Animal Place class
     */
    public AnimalPlace() {
    }
}
