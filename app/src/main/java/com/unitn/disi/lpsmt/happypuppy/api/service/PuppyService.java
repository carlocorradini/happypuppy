package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.Puppy;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Puppy API Service
 *
 * @author Carlo Corradini
 * @see Puppy
 */
public interface PuppyService {
    /**
     * Find all {@link Puppy} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List} of {@link Puppy} founds
     */
    @GET("auth/puppy")
    Call<API.Response<List<Puppy>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link Puppy} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link Puppy} founds
     */
    @GET("auth/puppy")
    Call<API.Response<List<Puppy>>> find();

    /**
     * Find the {@link Puppy} that has the given {@link Long id}
     *
     * @param id The {@link Puppy} {@link Long id}
     * @return An {@link API.Response} with the {@link Puppy} found
     */
    @GET("auth/puppy/{id}")
    Call<API.Response<Puppy>> findById(@Path("id") Long id);

    /**
     * Find a {@link List} of {@link Puppy} that correspond to the given {@link User} {@link UUID id}
     *
     * @param user The {@link User} {@link UUID id}
     * @return An {@link API.Response} with the {@link List} of {@link Puppy} founds for the corresponding {@link User} {@link UUID id}
     */
    @GET("auth/puppy")
    Call<API.Response<List<Puppy>>> findByUser(@Query("user") UUID user);

    /**
     * Create a new {@link Puppy} with the given puppy
     *
     * @param puppy The {@link Puppy} to create
     * @return An {@link API.Response} with the newly created {@link Puppy} {@link Long id}
     */
    @POST("auth/puppy")
    Call<API.Response<Long>> create(@Body Puppy puppy);

    /**
     * Update the {@link Puppy} that has the given {@link Puppy} {@link Long id} with the given puppy
     *
     * @param id         The {@link Puppy} {@link Long id}
     * @param userFriend The {@link Puppy} to update with
     * @return An {@link API.Response} if the update operation succeeded
     */
    @PATCH("auth/puppy/{id}")
    Call<API.Response> update(@Path("id") Long id, @Body Puppy puppy);

    /**
     * Update the {@link Puppy} avatar that has the given {@link Puppy} {@link Long id} with the given avatar
     *
     * @param id     The {@link Puppy} {@link Long id}
     * @param avatar The {@link MultipartBody.Part avatar} to update with
     * @return An {@link API.Response} if the update operation succeeded
     * @see MultipartBody.Part
     */
    @PATCH("auth/puppy/{id}/avatar")
    @Multipart
    Call<API.Response<URI>> updateAvatar(@Path("id") Long id, @Part MultipartBody.Part avatar);

    /**
     * Delete the {@link Puppy} that has the given {@link Puppy} {@link Long id}
     *
     * @param id The {@link Puppy} {@link Long id}
     * @return An {@link API.Response} if the delete operation succeeded
     */
    @DELETE("auth/puppy/{id}")
    Call<API.Response> delete(@Path("id") Long id);
}
