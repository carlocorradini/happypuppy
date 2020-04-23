package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.UserVerification;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserVerificationService {
    @POST("user_verification")
    Call<API.Response<String>> verify(@Body UserVerification userVerification);

    @POST("user_verification/{id}/resend")
    Call<API.Response> resend(@Path("id") UUID id);
}
