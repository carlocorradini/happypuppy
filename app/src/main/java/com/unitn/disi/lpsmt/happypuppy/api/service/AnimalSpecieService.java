package com.unitn.disi.lpsmt.happypuppy.api.service;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.AnimalSpecie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AnimalSpecieService {
    @GET("animal_specie")
    Call<API.Response<List<AnimalSpecie>>> all();

    @GET("animal_specie/{id}")
    Call<API.Response<AnimalSpecie>> findById(@Path("id") Long id);
}
