package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.AnimalPersonality;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AnimalPersonalityService {
    @GET("animal_personality")
    Call<API.Response<List<AnimalPersonality>>> all();

    @GET("animal_personality/{id}")
    Call<API.Response<AnimalPersonality>> findById(@Path("id") Long id);
}
