package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.AnimalBreed;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface AnimalBreedService {
    @GET("animal_breed")
    Call<API.Response<List<AnimalBreed>>> find(@QueryMap Map<String, String> options);

    @GET("animal_breed")
    Call<API.Response<List<AnimalBreed>>> find();

    @GET("animal_breed/{id}")
    Call<API.Response<AnimalBreed>> findById(@Path("id") Long id);
}
