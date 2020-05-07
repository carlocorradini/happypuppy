package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.AnimalPersonality;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Animal Personality API Service
 *
 * @author Carlo Corradini
 * @see AnimalPersonality
 */
public interface AnimalPersonalityService {
    /**
     * Find all {@link AnimalPersonality} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List} of {@link AnimalPersonality} founds
     */
    @GET("auth/animal_personality")
    Call<API.Response<List<AnimalPersonality>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link AnimalPersonality} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link AnimalPersonality} founds
     */
    @GET("auth/animal_personality")
    Call<API.Response<List<AnimalPersonality>>> find();

    /**
     * Find the {@link AnimalPersonality} that has the given id
     *
     * @param id The {@link AnimalPersonality} id
     * @return An {@link API.Response} with the {@link AnimalPersonality} found
     */
    @GET("auth/animal_personality/{id}")
    Call<API.Response<AnimalPersonality>> findById(@Path("id") Long id);
}
