package com.unitn.disi.lpsmt.happypuppy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;
import com.unitn.disi.lpsmt.happypuppy.profile.user.ProfileUser;
import com.unitn.disi.lpsmt.happypuppy.ui.fragment.MapsFragment;

import org.apache.http.HttpStatus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePage extends AppCompatActivity {

    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = HomePage.class.getName();

    BottomNavigationView bottomNavigation;
    ImageView icon_user;
    User userLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_activity);

        bottomNavigation = findViewById(R.id.home_page_bottom_nav);
        icon_user = findViewById(R.id.home_page_icon_user);

        Call<API.Response<User>> call = API.getInstance().getClient().create(UserService.class).me();
        call.enqueue(new Callback<API.Response<User>>() {
            @Override
            public void onResponse(Call<API.Response<User>> call, Response<API.Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userLogged = response.body().data;
                    Picasso.get().load(userLogged.avatar.toString()).resize(49, 49).into(icon_user);
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

        icon_user.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProfileUser.class);
            intent.putExtra("uuid_user", AuthManager.getInstance().getAuthUserId().toString());
            startActivity(intent);
        });

        /*
        runOnUiThread(() -> {
            User user = AuthManager.getInstance().getAuthUser();
            if(user != null) {
                Picasso.get().load(user.avatar.toString()).into(icon_user);
            }
        });
        */
    }
}
