package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface UserService {
    @POST("auth/user")
    Call<API.Response> create(@Body User user);

    @PATCH("auth/user")
    Call<API.Response> update(@Body User user);

}
