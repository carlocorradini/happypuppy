package com.unitn.disi.lpsmt.happypuppy.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    public UserVerification(UUID user, String otpEmail, String otpPhone) {
        this.user = user;
        this.otpEmail = otpEmail;
        this.otpPhone = otpPhone;
    }

    public UserVerification() {}
}
