package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.AnimalSpecie;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Animal Specie API Service
 *
 * @see AnimalSpecie
 */
public interface AnimalSpecieService {
    /**
     * Find all {@link AnimalSpecie} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List} of {@link AnimalSpecie} founds
     */
    @GET("auth/animal_specie")
    Call<API.Response<List<AnimalSpecie>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link AnimalSpecie} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link AnimalSpecie} founds
     */
    @GET("auth/animal_specie")
    Call<API.Response<List<AnimalSpecie>>> find();

    /**
     * Find the {@link AnimalSpecie} that has the given id
     *
     * @param id The {@link AnimalSpecie} id
     * @return An {@link API.Response} with the {@link AnimalSpecie} found
     */
    @GET("auth/animal_specie/{id}")
    Call<API.Response<AnimalSpecie>> findById(@Path("id") Long id);
}
