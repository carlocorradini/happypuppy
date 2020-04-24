package com.unitn.disi.lpsmt.happypuppy.api.interceptor;

import android.util.Log;

import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class AuthInterceptor implements Interceptor {

    private static final String TAG = AuthInterceptor.class.getName();

    private static final String AUTHORIZATION = "Authorization";

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        JWT token = AuthManager.getInstance().getToken();

        if (token != null) {
            request = chain.request().newBuilder().addHeader(AUTHORIZATION, toBearerToken(token)).build();
            Log.d(TAG, "Sending request with auth token");
        }

        return chain.proceed(request);
    }

    private static String toBearerToken(JWT token) {
        if (token == null) return null;

        return String.format("Bearer %s", token.toString());
    }
}
