package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface UserService {
    @GET("auth/user")
    Call<API.Response<List<User>>> find(@QueryMap Map<String, String> options);

    @GET("auth/user")
    Call<API.Response<List<User>>> find();

    @GET("auth/user/{id}")
    Call<API.Response<User>> findById(@Path("id") UUID id);

    @POST("auth/user")
    Call<API.Response<UUID>> create(@Body User user);

    @POST("auth/user/sign_in")
    Call<API.Response<JWT>> signIn(@Body User user);

    @POST("auth/user/password_reset/{email}")
    Call<API.Response> passwordReset(@Path("email") String email);

    @PATCH("auth/user")
    Call<API.Response> update(@Body User user);

    @PATCH("auth/user/avatar")
    @Multipart
    Call<API.Response<URL>> updateAvatar(@Part MultipartBody.Part avatar);

    @DELETE("auth/user")
    Call<API.Response> delete();
}
