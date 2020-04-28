package com.unitn.disi.lpsmt.happypuppy.api;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.auth0.android.jwt.DecodeException;
import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.happypuppy.App;

public final class AuthManager {

    private static final String TAG = AuthManager.class.getName();

    private static final String AUTH_TOKEN = "auth_token";

    private static AuthManager instance = null;

    private final SharedPreferences sharedPreferences;

    private AuthManager() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());

        Log.i(TAG, "Initialized");
    }

    public static AuthManager getInstance() {
        if (instance == null) {
            synchronized (AuthManager.class) {
                if (instance == null) {
                    instance = new AuthManager();
                }
            }
        }
        return instance;
    }

    public JWT getToken() throws DecodeException {
        JWT token = null;
        String tokenString = sharedPreferences.getString(AUTH_TOKEN, null);

        if (tokenString != null) {
            try {
                token = new JWT(tokenString);
            } catch (DecodeException ex) {
                Log.w(TAG, "Error retrieving token due to " + ex.getMessage());
            }
        }

        return token;
    }

    public void setToken(JWT token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN, token.toString());
        editor.apply();

        Log.i(TAG, "Token set");
    }

    public void clearToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(AUTH_TOKEN);
        editor.apply();

        Log.i(TAG, "Token cleared");
    }

    public boolean isAuth() {
        return getToken() != null;
    }
}
