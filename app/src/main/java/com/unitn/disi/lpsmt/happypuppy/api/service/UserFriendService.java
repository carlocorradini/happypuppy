package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.UserFriend;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface UserFriendService {
    @GET("auth/user_friend")
    Call<API.Response<List<UserFriend>>> find();

    @GET("auth/user_friend/{id}")
    Call<API.Response<UserFriend>> findById(@Path("id") UUID id);

    @PATCH("auth/user_friend/{id}")
    Call<API.Response> update(@Path("id") UUID id, @Body UserFriend userFriend);

    @DELETE("auth/user_friend/{id}")
    Call<API.Response> delete(@Path("id") UUID id);
}
