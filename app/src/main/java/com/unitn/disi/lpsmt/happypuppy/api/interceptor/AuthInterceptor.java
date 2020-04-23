package com.unitn.disi.lpsmt.happypuppy.api.interceptor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private static final String AUTHORIZATION = "Authorization";

    private String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImY3YTQ2ZmVhLTZmOGEtNGFjNS05NWJlLWNiYzUxMmI3Y2IyYSIsInJvbGUiOiJzdGFuZGFyZCIsImlhdCI6MTU4NzM5MTA3MywiZXhwIjoxNTkwMTU1ODczfQ.ptfWUJEK8DSlf_oY5UwtmFYo3iK7FUGzaHC6OlCoow0";

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();

        if(token != null) {
            request = chain.request().newBuilder().addHeader(AUTHORIZATION, bearerToken(token)).build();
        }

        return chain.proceed(request);
    }

    private static String bearerToken(String token) {
        if(token == null) return null;

        return String.format("Bearer %s", token);
    }
}
