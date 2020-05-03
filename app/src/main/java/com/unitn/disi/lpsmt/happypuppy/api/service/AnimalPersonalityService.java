package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.AnimalPersonality;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface AnimalPersonalityService {
    @GET("animal_personality")
    Call<API.Response<List<AnimalPersonality>>> find(@QueryMap Map<String, String> options);

    @GET("animal_personality")
    Call<API.Response<List<AnimalPersonality>>> find();

    @GET("animal_personality/{id}")
    Call<API.Response<AnimalPersonality>> findById(@Path("id") Long id);
}
