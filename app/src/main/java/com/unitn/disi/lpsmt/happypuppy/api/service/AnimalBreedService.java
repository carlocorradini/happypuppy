package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.AnimalBreed;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AnimalBreedService {
    @GET("animal_breed")
    Call<API.Response<List<AnimalBreed>>> all();

    @GET("animal_breed/{id}")
    Call<API.Response<AnimalBreed>> findById(@Path("id") Long id);
}
