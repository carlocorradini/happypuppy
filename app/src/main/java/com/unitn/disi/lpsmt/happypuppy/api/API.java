package com.unitn.disi.lpsmt.happypuppy.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.unitn.disi.lpsmt.happypuppy.BuildConfig;
import com.unitn.disi.lpsmt.happypuppy.api.adapter.deserializer.LocalDateDeserializer;
import com.unitn.disi.lpsmt.happypuppy.api.adapter.deserializer.LocalDateTimeDeserializer;
import com.unitn.disi.lpsmt.happypuppy.api.adapter.serializer.LocalDateTimeSerializer;
import com.unitn.disi.lpsmt.happypuppy.api.interceptor.AuthInterceptor;
import com.unitn.disi.lpsmt.happypuppy.api.adapter.serializer.LocalDateSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class API {

    private static final String TAG = API.class.getName();

    public static final String BASE_URL = "http://192.168.0.25:8080/api/v1/"; // todo cambiare url

    private static API instance = null;

    private final Retrofit client;

    private API() {
        client = buildClient();

        Log.d(TAG, "Client builded");
        Log.i(TAG, "Initialized");
    }

    public static API getInstance() {
        if (instance == null) {
            synchronized (API.class) {
                if (instance == null) {
                    instance = new API();
                }
            }
        }
        return instance;
    }

    private Retrofit buildClient() {
        // OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC))
                .addInterceptor(new AuthInterceptor())
                .build();
        // END OkHttpClient

        // Gson
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .create();
        // END Gson

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public Retrofit getClient() {
        return client;
    }

    public static class Response<T> {
        @SerializedName("status")
        @Expose
        public String status;

        @SerializedName("is_success")
        @Expose
        public Boolean isSuccess;

        @SerializedName("status_code")
        @Expose
        public Short statusCode;

        @SerializedName("status_code_name")
        @Expose
        public String statusCodeName;

        @SerializedName("data")
        @Expose
        public T data;

        public Response(String status, Boolean isSuccess, Short statusCode, String statusCodeName, T data) {
            this.status = status;
            this.isSuccess = isSuccess;
            this.statusCode = statusCode;
            this.statusCodeName = statusCodeName;
            this.data = data;
        }
    }
}