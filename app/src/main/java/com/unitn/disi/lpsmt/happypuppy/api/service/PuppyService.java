package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.Puppy;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PuppyService {
    @GET("auth/puppy/{id}")
    Call<API.Response<Puppy>> findById(@Path("id") Long id);

    @POST("auth/puppy")
    Call<API.Response<Long>> create(@Body Puppy puppy);

    @PATCH("auth/puppy")
    Call<API.Response> update(@Body Puppy puppy);
}
