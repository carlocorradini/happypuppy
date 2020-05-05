package com.unitn.disi.lpsmt.happypuppy.api;

import android.os.Build;
import android.util.Log;

import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.unitn.disi.lpsmt.happypuppy.BuildConfig;
import com.unitn.disi.lpsmt.happypuppy.api.adapter.deserializer.JWTDeserializer;
import com.unitn.disi.lpsmt.happypuppy.api.adapter.deserializer.LocalDateDeserializer;
import com.unitn.disi.lpsmt.happypuppy.api.adapter.deserializer.LocalDateTimeDeserializer;
import com.unitn.disi.lpsmt.happypuppy.api.adapter.serializer.JWTSerializer;
import com.unitn.disi.lpsmt.happypuppy.api.adapter.serializer.LocalDateTimeSerializer;
import com.unitn.disi.lpsmt.happypuppy.api.entity.error.ConflictError;
import com.unitn.disi.lpsmt.happypuppy.api.entity.error.UnprocessableEntityError;
import com.unitn.disi.lpsmt.happypuppy.api.interceptor.AuthInterceptor;
import com.unitn.disi.lpsmt.happypuppy.api.adapter.serializer.LocalDateSerializer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class API {

    private static final String TAG = API.class.getName();

    public static final String BASE_URL = "http://192.168.1.6:8080/api/v1/"; // todo cambiare url

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
                .registerTypeAdapter(JWT.class, new JWTSerializer())
                .registerTypeAdapter(JWT.class, new JWTDeserializer())
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

    public static final class Response<T> {
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

    public static final class ErrorConverter {

        private static final String TAG = ErrorConverter.class.getName();

        public static final Type TYPE_EMPTY = new TypeToken<API.Response>() {
        }.getType();

        public static final Type TYPE_JWT = new TypeToken<API.Response<JWT>>() {
        }.getType();

        public static final Type TYPE_UUID = new TypeToken<API.Response<UUID>>() {
        }.getType();

        public static final Type TYPE_UNPROCESSABLE_ENTITY_LIST = new TypeToken<API.Response<List<UnprocessableEntityError>>>() {
        }.getType();

        public static final Type TYPE_CONFLICT_LIST = new TypeToken<API.Response<List<ConflictError>>>() {
        }.getType();

        public static <T> API.Response<T> convert(ResponseBody errorBody, Type type) {
            if (errorBody == null || type == null) return null;

            Converter<ResponseBody, API.Response<T>> converter = API.getInstance().getClient().responseBodyConverter(type, new Annotation[0]);
            API.Response<T> error = null;

            try {
                error = converter.convert(errorBody);
            } catch (IOException ex) {
                Log.e(TAG, "Unable to convert error body due to " + ex.getMessage());
            }

            return error;
        }

        public static <T> API.Response<T> convert(ResponseBody errorBody) {
            return convert(errorBody, TYPE_EMPTY);
        }
    }
}