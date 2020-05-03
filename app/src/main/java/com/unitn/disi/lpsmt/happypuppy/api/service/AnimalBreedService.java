package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.AnimalBreed;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Animal Breed API Service
 *
 * @author Carlo Corradini
 * @see AnimalBreed
 */
public interface AnimalBreedService {
    /**
     * Find all {@link AnimalBreed} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List} of {@link AnimalBreed} founds
     */
    @GET("animal_breed")
    Call<API.Response<List<AnimalBreed>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link AnimalBreed} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link AnimalBreed} founds
     */
    @GET("animal_breed")
    Call<API.Response<List<AnimalBreed>>> find();

    /**
     * Find the {@link AnimalBreed} that has the given id
     *
     * @param id The {@link AnimalBreed} id
     * @return An {@link API.Response} with the {@link AnimalBreed} found
     */
    @GET("animal_breed/{id}")
    Call<API.Response<AnimalBreed>> findById(@Path("id") Long id);
}
