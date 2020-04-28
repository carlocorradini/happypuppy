package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserVerification {
    @SerializedName("user")
    @Expose
    public UUID user;

    @SerializedName("otp_email")
    @Expose
    public String otpEmail;

    @SerializedName("otp_phone")
    @Expose
    public String otpPhone;

    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    public UserVerification(UUID user, String otpEmail, String otpPhone, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = user;
        this.otpEmail = otpEmail;
        this.otpPhone = otpPhone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserVerification() {
    }
}
