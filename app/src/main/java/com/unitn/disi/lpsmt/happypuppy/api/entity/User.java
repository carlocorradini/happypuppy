package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * User mapping
 *
 * @author Carlo Corradini
 */
public class User {
    /**
     * User Genders
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
        FEMALE("female"),

        /**
         * Unknown Gender
         */
        @SerializedName("unknown")
        UNKNOWN("unknown");

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
     * User Roles
     */
    public enum Role {
        /**
         * Admin Role
         */
        @SerializedName("admin")
        ADMIN("admin"),

        /**
         * Standard Role
         */
        @SerializedName("standard")
        STANDARD("standard");

        /**
         * Value of the Role
         */
        private final String value;

        /**
         * Construct a Role enum
         *
         * @param value Value of the Role
         */
        Role(String value) {
            this.value = value;
        }

        /**
         * Return the value of the current Role
         *
         * @return Role value
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * Id of the User
     */
    @SerializedName("id")
    @Expose
    public UUID id;

    /**
     * Username of the User
     */
    @SerializedName("username")
    @Expose
    public String username;

    /**
     * Password of the User
     */
    @SerializedName("password")
    @Expose
    public String password;

    /**
     * Role of the User
     *
     * @see Role
     */
    @SerializedName("role")
    @Expose
    public Role role;

    /**
     * Name of the User
     */
    @SerializedName("name")
    @Expose
    public String name;

    /**
     * Surname of the User
     */
    @SerializedName("surname")
    @Expose
    public String surname;

    /**
     * Gender of the User
     *
     * @see Gender
     */
    @SerializedName("gender")
    @Expose
    public Gender gender;

    /**
     * Date of Birth of the User
     */
    @SerializedName("date_of_birth")
    @Expose
    public LocalDate dateOfBirth;

    /**
     * Email of the User
     */
    @SerializedName("email")
    @Expose
    public String email;

    /**
     * Phone number of the User
     */
    @SerializedName("phone")
    @Expose
    public String phone;

    /**
     * Avatar image url of the User
     */
    @SerializedName("avatar")
    @Expose
    public URI avatar;

    /**
     * List of Puppy ids of the User
     *
     * @see Puppy
     */
    @SerializedName("puppies")
    @Expose
    public List<Long> puppies;

    /**
     * The number of {@link UserFriend friends} of the User
     *
     * @see UserFriend
     */
    @SerializedName("friends")
    @Expose
    public Long friends;

    /**
     * Creation {@link LocalDateTime} of the User
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the User
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct an User class
     *
     * @param id          Id of the User
     * @param username    Username of the User
     * @param password    Password of the User
     * @param role        Role of the User
     * @param name        Name of the User
     * @param surname     Surname of the User
     * @param gender      Gender of the User
     * @param dateOfBirth Date of Birth of the User
     * @param email       Email of the User
     * @param phone       Phone number of the User
     * @param avatar      Avatar image url of the User
     * @param puppies     List of Puppies ids of the User
     * @param friends     The number of {@link UserFriend friends} of the User
     * @param createdAt   Creation {@link LocalDateTime} of the User
     * @param updatedAt   Update {@link LocalDateTime} of the User
     */
    public User(UUID id, String username, String password, Role role, String name, String surname, Gender gender, LocalDate dateOfBirth, String email, String phone, URI avatar, List<Long> puppies, Long friends, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.surname = surname;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
        this.puppies = puppies;
        this.friends = friends;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty User class
     */
    public User() {
    }
}
