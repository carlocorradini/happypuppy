package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.entity.UserFriend;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * User Friend API Service
 *
 * @author Carlo Corradini
 * @see UserFriend
 */
public interface UserFriendService {
    /**
     * Find all {@link UserFriend} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List} of {@link UserFriend} founds
     */
    @GET("auth/user_friend")
    Call<API.Response<List<UserFriend>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link UserFriend} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link UserFriend} founds
     */
    @GET("auth/user_friend")
    Call<API.Response<List<UserFriend>>> find();

    /**
     * Find the {@link UserFriend} that has the given id
     *
     * @param id The {@link UserFriend} id
     * @return An {@link API.Response} with the {@link UserFriend} found
     */
    @GET("auth/user_friend/{id}")
    Call<API.Response<UserFriend>> findById(@Path("id") UUID id);

    /**
     * Create a new {@link UserFriend} with the given userFriend
     *
     * @param userFriend The {@link UserFriend} to create
     * @return An {@link API.Response} with the {@link User} friend {@link UUID id}
     */
    @POST("auth/user_friend")
    Call<API.Response<UUID>> create(@Body UserFriend userFriend);

    /**
     * Update the {@link UserFriend} that has the given {@link User friend} {@link Long id} with the given userFriend
     *
     * @param id         The {@link User friend} id
     * @param userFriend The {@link UserFriend} to update with
     * @return An {@link API.Response} if the update operation succeeded
     */
    @PATCH("auth/user_friend/{id}")
    Call<API.Response> update(@Path("id") UUID id, @Body UserFriend userFriend);

    /**
     * Delete the {@link UserFriend} that has the given {@link User friend} id
     *
     * @param id The {@link User friend} id
     * @return An {@link API.Response} if the delete operation succeeded
     */
    @DELETE("auth/user_friend/{id}")
    Call<API.Response> delete(@Path("id") UUID id);
}
