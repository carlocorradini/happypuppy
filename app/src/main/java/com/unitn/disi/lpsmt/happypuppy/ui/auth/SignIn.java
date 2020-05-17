package com.unitn.disi.lpsmt.happypuppy.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.happypuppy.helper.ErrorHelper;
import com.unitn.disi.lpsmt.happypuppy.ui.HomePage;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;
import com.unitn.disi.lpsmt.happypuppy.ui.components.Toasty;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SignIn extends AppCompatActivity {

    private LinearLayout root;
    private EditText inputUsername;
    private EditText inputPassword;
    private Button buttonSignIn;
    private Button buttonSignUp;
    private Button buttonForgotPassword;
    private LinearLayout loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        root = findViewById(R.id.sign_in_root_view);
        inputUsername = findViewById(R.id.sign_in_input_username);
        inputPassword = findViewById(R.id.sign_in_input_password);
        buttonSignIn = findViewById(R.id.sign_in_button_sign_in);
        buttonSignUp = findViewById(R.id.sign_in_button_sign_up);
        buttonForgotPassword = findViewById(R.id.sign_in_button_forgot_password);
        loader = findViewById(R.id.sign_in_view_loader);

        buttonSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SignUp.class);
            startActivity(intent);
        });

        buttonForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ForgotPassword.class);
            startActivity(intent);
        });

        /* Authentication */
        buttonSignIn.setOnClickListener(v -> {
            /* Example of wrong login with message */
            for (int i = 0; i < root.getChildCount(); i++) {
                View child = root.getChildAt(i);
                child.setEnabled(false);
                child.setClickable(false);
            }

            loader.setVisibility(View.VISIBLE);

            User user = new User();
            user.username = inputUsername.getText().toString();
            user.password = inputPassword.getText().toString();

            // Validation
            if (validateLogin(v, user)) {
                signIn(v, user);
            } else {
                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(true);
                    child.setClickable(true);
                }
                loader.setVisibility(View.GONE);
            }
        });
    }

    public void onBackPressed() {
        //do nothing
        //prevent the back android button
    }

    private boolean validateLogin(final View v, final User user) {
        if (user.username.isEmpty()) {
            new Toasty(getBaseContext(), v, R.string.insert_username);
            return false;
        }
        if (user.password.isEmpty()) {
            new Toasty(getBaseContext(), v, R.string.insert_password);
            return false;
        }
        if (user.password.length() < 8) {
            new Toasty(getBaseContext(), v, R.string.insert_password_length);
            return false;
        }
        return true;
    }

    private void signIn(final View v, final User user) {
        Call<API.Response<JWT>> call = API.getInstance().getClient().create(UserService.class).signIn(user);
        call.enqueue(new Callback<API.Response<JWT>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<JWT>> call, @NotNull Response<API.Response<JWT>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthManager.getInstance().setToken(response.body().data);
                    Intent intent = new Intent(v.getContext(), HomePage.class);
                    startActivity(intent);
                    finish();
                } else if (response.errorBody() != null) {
                    switch (response.code()) {
                        case HttpStatus.SC_UNAUTHORIZED: {
                            new Toasty(getBaseContext(), v, R.string.wrong_login);
                            break;
                        }
                        case HttpStatus.SC_FORBIDDEN: {
                            API.Response<UUID> error = API.ErrorConverter.convert(response.errorBody(), API.ErrorConverter.TYPE_UUID);
                            Intent intent = new Intent(v.getContext(), ActivateProfile.class);
                            intent.putExtra("uuid", error.data.toString());
                            startActivity(intent);
                            finish();
                            break;
                        }
                        case HttpStatus.SC_NOT_FOUND: {
                            new Toasty(getBaseContext(), v, R.string.user_not_found);
                            break;
                        }
                        default: {
                            new Toasty(getBaseContext(), v, R.string.internal_server_error);
                            break;
                        }
                    }
                } else {
                    new Toasty(getBaseContext(), v, R.string.error_unknown);
                }

                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(true);
                    child.setClickable(true);
                }
                loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<JWT>> call, @NotNull Throwable t) {
                ErrorHelper.showFailureError(getBaseContext(), v, t);

                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(true);
                    child.setClickable(true);
                }
            }
        });
    }
}