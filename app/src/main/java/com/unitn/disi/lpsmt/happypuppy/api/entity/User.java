package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class User {
    public enum Gender {
        @SerializedName("male")
        MALE("male"),

        @SerializedName("female")
        FEMALE("female"),

        @SerializedName("unknown")
        UNKNOWN("unknown");

        private final String value;

        Gender(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum Role {
        @SerializedName("admin")
        ADMIN("admin"),

        @SerializedName("standard")
        STANDARD("standard");

        private final String value;

        Role(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @SerializedName("id")
    @Expose
    public UUID id;

    @SerializedName("username")
    @Expose
    public String username;

    @SerializedName("password")
    @Expose
    public String password;

    @SerializedName("role")
    @Expose
    public Role role;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("surname")
    @Expose
    public String surname;

    @SerializedName("gender")
    @Expose
    public Gender gender;

    @SerializedName("date_of_birth")
    @Expose
    public LocalDate dateOfBirth;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("phone")
    @Expose
    public String phone;

    @SerializedName("avatar")
    @Expose
    public URL avatar;

    @SerializedName("puppies")
    @Expose
    public List<Long> puppies;

    public User(UUID id, String username, String password, Role role, String name, String surname, Gender gender, LocalDate dateOfBirth, String email, String phone, URL avatar, List<Long> puppies) {
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
    }

    public User() {}
}
