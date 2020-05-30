package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.entity.UserVerification;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * User Verification API Service
 *
 * @author Carlo Corradini
 * @see UserVerification
 */
public interface UserVerificationService {
    /**
     * Verify the {@link User} with the given userVerification.
     * If the verification succeeded a {@link JWT} will be returned
     *
     * @param userVerification The {@link UserVerification} to send
     * @return An {@link API.Response} with the {@link JWT} authentication
     * @see AuthManager
     */
    @POST("user_verification")
    Call<API.Response<JWT>> verify(@Body UserVerification userVerification);

    /**
     * Resend the {@link UserVerification} to the {@link User}
     *
     * @param id The {@link User} id
     * @return An {@link API.Response} if the resend operation succeeded
     */
    @POST("user_verification/{id}/resend")
    Call<API.Response> resend(@Path("id") UUID id);
}
