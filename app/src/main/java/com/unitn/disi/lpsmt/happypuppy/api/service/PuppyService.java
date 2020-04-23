package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.Puppy;

import java.net.URL;

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

public interface PuppyService {
    @GET("auth/puppy/{id}")
    Call<API.Response<Puppy>> findById(@Path("id") Long id);

    @POST("auth/puppy")
    Call<API.Response<Long>> create(@Body Puppy puppy);

    @PATCH("auth/puppy/{id}")
    Call<API.Response> update(@Path("id") Long id, @Body Puppy puppy);

    @PATCH("auth/puppy/{id}/avatar")
    @Multipart
    Call<API.Response<URL>> updateAvatar(@Path("id") Long id, @Part MultipartBody.Part avatar);

    @DELETE("auth/puppy/{id}")
    Call<API.Response> delete(@Path("id") Long id);
}
