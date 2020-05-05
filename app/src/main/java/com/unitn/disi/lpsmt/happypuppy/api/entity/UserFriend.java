package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User Friend mapping
 *
 * @author Carlo Corradini
 * @see User
 */
public class UserFriend {
    /**
     * User Friend Types
     */
    public enum Type {
        /**
         * User to Friend is friend
         */
        @SerializedName("friend")
        FRIEND("friend"),

        /**
         * User to Friend is blocked
         */
        @SerializedName("blocked")
        BLOCKED("blocked"),

        /**
         * Friend to User sent a friend request
         */
        @SerializedName("friend_request")
        FRIEND_REQUEST("friend_request"),

        /**
         * User to Friend is waiting for friend request acceptance
         */
        @SerializedName("waiting_acceptance")
        WAITING_ACCEPTANCE("waiting_acceptance");

        /**
         * Value of the friend Type
         */
        private final String value;

        /**
         * Construct a Type enum
         *
         * @param value Value of the friend Type
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
     * Id of the User
     *
     * @see User
     */
    @SerializedName("user")
    @Expose
    public UUID user;

    /**
     * Id of the User's friend
     *
     * @see User
     */
    @SerializedName("friend")
    @Expose
    public UUID friend;

    /**
     * Type of friend
     *
     * @see Type
     */
    @SerializedName("type")
    @Expose
    public Type type;

    /**
     * Creation {@link LocalDateTime} of the User Friend
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the User Friend
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct an User Friend class
     *
     * @param user      Id of the User
     * @param friend    Id of the User's friend
     * @param type      Type of friend
     * @param createdAt Creation {@link LocalDateTime} of the User Friend
     * @param updatedAt Update {@link LocalDateTime} of the User Friend
     */
    public UserFriend(UUID user, UUID friend, Type type, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = user;
        this.friend = friend;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty User Friend class
     */
    public UserFriend() {
    }
}
