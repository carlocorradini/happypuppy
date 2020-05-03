package com.unitn.disi.lpsmt.happypuppy.api.interceptor;

import android.util.Log;

import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import com.unitn.disi.lpsmt.happypuppy.api.entity.User;

/**
 * Authentication Interceptor class
 * Intercept a http request and inject the {@link JWT} if the {@link User} is authenticated using the {@link AuthManager}
 *
 * @author Carlo Corradini
 * @see Interceptor
 * @see AuthManager
 */
public final class AuthInterceptor implements Interceptor {

    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = AuthInterceptor.class.getName();

    /**
     * Name of the Authorization http header key
     */
    private static final String AUTHORIZATION = "Authorization";

    /**
     * Intercept a http request and inject the {@link JWT} if the {@link User} is authenticated using the {@link AuthManager}
     *
     * @param chain Request chain
     * @return The Chain Response
     * @throws IOException If the modification fails
     * @see JWT
     * @see User
     * @see AuthManager
     */
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

    /**
     * Transform a {@link JWT} into a Bearer authorization string
     *
     * @param token The {@link JWT} to transform
     * @return The Bearer string with the {@link JWT} attached
     */
    private static String toBearerToken(JWT token) {
        if (token == null) return null;

        return String.format("Bearer %s", token.toString());
    }
}
