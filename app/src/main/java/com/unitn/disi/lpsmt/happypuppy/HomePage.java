package com.unitn.disi.lpsmt.happypuppy;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.adapter.deserializer.JWTDeserializer;
import com.unitn.disi.lpsmt.happypuppy.api.adapter.serializer.JWTSerializer;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.entity.error.ConflictError;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;

import org.apache.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePage extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    BottomNavigationItemView profile;
    User userLogged;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        bottomNavigation = findViewById(R.id.home_page_bottom_nav);
        profile = findViewById(R.id.nav_account);

        JWT userToken = AuthManager.getInstance().getToken();
        UUID uuid = UUID.fromString("");

        Call<API.Response<User>> call = API.getInstance().getClient().create(UserService.class).findById(uuid);
        call.enqueue(new Callback<API.Response<User>>() {
            @Override
            public void onResponse(Call<API.Response<User>> call, Response<API.Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userLogged = response.body().data;
                } else if (response.errorBody() != null) {
                    switch (response.code()) {
                        case HttpStatus.SC_NOT_FOUND: {
                            new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(),
                                    getWindow().getDecorView().findViewById(android.R.id.content),
                                    getResources().getString(R.string.user_not_found));
                            break;
                        }
                        default:
                            new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(),
                                    getWindow().getDecorView().findViewById(android.R.id.content),
                                    getResources().getString(R.string.internal_server_error));
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<API.Response<User>> call, Throwable t) {
                new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(),
                        getWindow().getDecorView().findViewById(android.R.id.content),
                        getResources().getString(R.string.no_internet));
            }
        });
        profile.setBackground(Drawable.createFromPath(userLogged.avatar.toString()));
    }
}
