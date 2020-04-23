package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Puppy {
    public enum Gender {
        @SerializedName("male")
        MALE("male"),

        @SerializedName("female")
        FEMALE("female");

        private final String value;

        Gender(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @SerializedName("id")
    @Expose
    public Long id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("gender")
    @Expose
    public Gender gender;

    @SerializedName("date_of_birth")
    @Expose
    public LocalDate dateOfBirth;

    @SerializedName("weight")
    @Expose
    public Long weight;

    @SerializedName("avatar")
    @Expose
    public URL avatar;

    @SerializedName("user")
    @Expose
    public UUID user;

    @SerializedName("specie")
    @Expose
    public Long specie;

    @SerializedName("breeds")
    @Expose
    public List<Long> breeds;

    @SerializedName("personalities")
    @Expose
    public List<Long> personalities;

    public Puppy(Long id, String name, Gender gender, LocalDate dateOfBirth, Long weight, URL avatar, UUID user, Long specie, List<Long> breeds, List<Long> personalities) {
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
    }

    public Puppy() {}
}
