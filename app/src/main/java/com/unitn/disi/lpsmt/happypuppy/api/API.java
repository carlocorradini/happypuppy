package com.unitn.disi.lpsmt.happypuppy.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.unitn.disi.lpsmt.happypuppy.api.deserializer.LocalDateDeserializer;
import com.unitn.disi.lpsmt.happypuppy.api.interceptor.AuthInterceptor;
import com.unitn.disi.lpsmt.happypuppy.api.serializer.LocalDateSerializer;

import java.time.LocalDate;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
    private static API instance = null;

    private final Retrofit client;

    private static final String BASE_URL = "http://192.168.0.25:8080/api/v1/"; // todo cambiare url

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
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        AuthInterceptor authInterceptor = new AuthInterceptor();
        // END Interceptor
        // Gson
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
        gson.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer());
        // END Gson

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(
                        new OkHttpClient.Builder()
                                .addInterceptor(loggingInterceptor)
                                .addInterceptor(authInterceptor)
                                .build()
                )
                .addConverterFactory(GsonConverterFactory.create(gson.create()))
                .build();
    }

    public Retrofit getClient() {
        return client;
    }

    public static class Response<T> {
        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("is_success")
        @Expose
        private Boolean isSuccess;

        @SerializedName("status_code")
        @Expose
        private Short statusCode;

        @SerializedName("status_code_name")
        @Expose
        private String statusCodeName;

        @SerializedName("data")
        @Expose
        private T data;

        public Response(String status, Boolean isSuccess, Short statusCode, String statusCodeName, T data) {
            this.status = status;
            this.isSuccess = isSuccess;
            this.statusCode = statusCode;
            this.statusCodeName = statusCodeName;
            this.data = data;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Boolean getSuccess() {
            return isSuccess;
        }

        public void setSuccess(Boolean success) {
            isSuccess = success;
        }

        public Short getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Short statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatusCodeName() {
            return statusCodeName;
        }

        public void setStatusCodeName(String statusCodeName) {
            this.statusCodeName = statusCodeName;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}