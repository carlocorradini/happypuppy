package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserFriend {
    public enum Type {
        @SerializedName("friend")
        FRIEND("friend"),

        @SerializedName("blocked")
        BLOCKED("blocked"),

        @SerializedName("friend_request")
        FRIEND_REQUEST("friend_request"),

        @SerializedName("waiting_acceptance")
        WAITING_ACCEPTANCE("waiting_acceptance"),

        @SerializedName("deleted")
        DELETED("deleted");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @SerializedName("user")
    @Expose
    public UUID user;

    @SerializedName("friend")
    @Expose
    public UUID friend;

    @SerializedName("type")
    @Expose
    public Type type;

    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    public UserFriend(UUID user, UUID friend, Type type, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = user;
        this.friend = friend;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserFriend() {
    }
}
