package com.unitn.disi.lpsmt.happypuppy.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.unitn.disi.lpsmt.happypuppy.api.adapter.deserializer.LocalDateDeserializer;
import com.unitn.disi.lpsmt.happypuppy.api.interceptor.AuthInterceptor;
import com.unitn.disi.lpsmt.happypuppy.api.adapter.serializer.LocalDateSerializer;

import java.time.LocalDate;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
    private static API instance = null;

    public final Retrofit client;

    public static final String BASE_URL = "http://192.168.0.25:8080/api/v1/"; // todo cambiare url

    private API() {
        client = buildClient();
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
        // Interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        AuthInterceptor authInterceptor = new AuthInterceptor();
        // END Interceptor
        // Gson
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .create();
        // END Gson

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(
                        new OkHttpClient.Builder()
                                .addInterceptor(loggingInterceptor)
                                .addInterceptor(authInterceptor)
                                .build()
                )
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
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