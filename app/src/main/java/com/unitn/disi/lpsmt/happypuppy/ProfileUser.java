package com.unitn.disi.lpsmt.happypuppy;

import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.happypuppy.HomePage;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ProfileUser extends AppCompatActivity {
    Button buttonBack;
    TextView usernameTop;
    ImageView settings;
    ImageView userAvatar;
    Button changeAvatar;
    TextView numberFriends;
    TextView numberPuppies;
    LinearLayout buttonsUser;
    LinearLayout buttonsVisit;

    User userLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user_activity);


        buttonBack = findViewById(R.id.profile_user_button_back);
        usernameTop = findViewById(R.id.profile_user_username);
        settings = findViewById(R.id.profile_user_settings);
        userAvatar = findViewById(R.id.profile_user_profile_image);
        changeAvatar = findViewById(R.id.profile_user_button_image);
        numberFriends = findViewById(R.id.profile_user_number_friends);
        numberPuppies = findViewById(R.id.profile_user_number_puppies);

        buttonsUser = findViewById(R.id.profile_user_buttons_profile);
        buttonsVisit = findViewById(R.id.profile_user_buttons_visit);

        UUID uuid = UUID.fromString(getIntent().getStringExtra("uuid_user"));

        if(uuid != AuthManager.getInstance().getAuthUserId()){
            buttonsUser.setVisibility(View.VISIBLE);
            buttonsVisit.setVisibility(View.GONE);
            changeAvatar.setVisibility(View.VISIBLE);
        }else{
            buttonsUser.setVisibility(View.GONE);
            buttonsVisit.setVisibility(View.VISIBLE);
            changeAvatar.setVisibility(View.GONE);
        }

        Call<API.Response<User>> call = API.getInstance().getClient().create(UserService.class).findById(uuid);
        call.enqueue(new Callback<API.Response<User>>() {
            @Override
            public void onResponse(Call<API.Response<User>> call, Response<API.Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userLogged = response.body().data;
                    Picasso.get().load(userLogged.avatar.toString()).into(userAvatar);
                    usernameTop.setText(userLogged.username);

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

        buttonBack.setOnClickListener(v -> {
            finish();
        });
    }
}
