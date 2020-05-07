package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.AnimalPlace;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Animal Place API Service
 *
 * @author Carlo Corradini
 * @see AnimalPlace
 */
public interface AnimalPlaceService {
    /**
     * Find all {@link AnimalPlace} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List} of {@link AnimalPlace} founds
     */
    @GET("auth/animal_place")
    Call<API.Response<List<AnimalPlace>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link AnimalPlace} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link AnimalPlace} founds
     */
    @GET("auth/animal_place")
    Call<API.Response<List<AnimalPlace>>> find();

    /**
     * Find the {@link AnimalPlace} that has the given id
     *
     * @param id The {@link AnimalPlace} id
     * @return An {@link API.Response} with the {@link AnimalPlace} found
     */
    @GET("auth/animal_place/{id}")
    Call<API.Response<AnimalPlace>> findById(@Path("id") Long id);

    /**
     * Find all {@link AnimalPlace} within specified radius of latitude & longitude
     *
     * @param latitude  Pivot latitude
     * @param longitude Pivot longitude
     * @param radius    Bounding Box radius in meters
     * @return An {@link API.Response} with the {@link List} of {@link AnimalPlace} that are contained in the generated Bounding Box with centered pivot and radius
     */
    @GET("auth/animal_place")
    Call<API.Response<List<AnimalPlace>>> findByRadius(@Query("latitude") Double latitude, @Query("longitude") Double longitude, @Query("radius") Integer radius);
}
