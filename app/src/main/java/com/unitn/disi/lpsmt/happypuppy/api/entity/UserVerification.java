package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User Verification mapping
 *
 * @author Carlo Corradini
 * @see User
 */
public class UserVerification {
    /**
     * Id of the User
     */
    @SerializedName("user")
    @Expose
    public UUID user;

    /**
     * OTP email code
     */
    @SerializedName("otp_email")
    @Expose
    public String otpEmail;

    /**
     * OTP phone code
     */
    @SerializedName("otp_phone")
    @Expose
    public String otpPhone;

    /**
     * Creation {@link LocalDateTime} of the User Verification
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the User Verification
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct an User Verification class
     *
     * @param user      Id of the User
     * @param otpEmail  OTP email code
     * @param otpPhone  OTP phone code
     * @param createdAt Creation {@link LocalDateTime} of the User Verification
     * @param updatedAt Update {@link LocalDateTime} of the User Verification
     */
    public UserVerification(UUID user, String otpEmail, String otpPhone, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = user;
        this.otpEmail = otpEmail;
        this.otpPhone = otpPhone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty User Verification class
     */
    public UserVerification() {
    }
}
