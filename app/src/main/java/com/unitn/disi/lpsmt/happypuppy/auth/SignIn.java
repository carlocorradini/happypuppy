package com.unitn.disi.lpsmt.happypuppy.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.happypuppy.HomePage;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignIn extends AppCompatActivity {

    private EditText inputUsername;
    private EditText inputPassword;
    private Button buttonSignIn;
    private Button buttonSignUp;
    private Button buttonForgotPassword;
    private TextView errorLogin;
    private LinearLayout loginLoader;
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_in);

        root = findViewById(R.id.sign_in_root_view);
        inputUsername = findViewById(R.id.sign_in_input_username);
        inputPassword = findViewById(R.id.sign_in_input_password);
        buttonSignIn = findViewById(R.id.sign_in_button_sign_in);
        buttonSignUp = findViewById(R.id.sign_in_button_sign_up);
        buttonForgotPassword = findViewById(R.id.sign_in_button_forgot_password);
        loginLoader = findViewById(R.id.sign_in_view_loader);

        buttonSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SignUp.class);
            startActivity(intent);
        });

        /* Authentication */
        buttonSignIn.setOnClickListener(v -> {
            User loginUser = new User();
            loginUser.username = inputUsername.getText().toString();
            loginUser.password = inputPassword.getText().toString();
            Call<API.Response<JWT>> call = API.getInstance().getClient().create(UserService.class).signIn(loginUser);

            /* Example of wrong login with message */
            for (int i = 0; i < root.getChildCount(); i++) {
                View child = root.getChildAt(i);
                child.setEnabled(false);
                child.setClickable(false);
            }
            loginLoader.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<API.Response<JWT>>() {
                @Override
                public void onResponse(Call<API.Response<JWT>> call, Response<API.Response<JWT>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        AuthManager.getInstance().setToken(response.body().data);
                        Intent intent = new Intent(v.getContext(), HomePage.class);
                        startActivity(intent);
                    } else {
                        loginLoader.setVisibility(View.GONE);
                        switch (response.code()){
                            case 401:
                                Toast.makeText(getApplicationContext(),R.string.wrong_login,Toast.LENGTH_LONG).show();
                                break;
                            case 403:
                                /* TODO: RESOLVE CRASH TEST WHEN USER IS NOT VERIFIED */
                                Intent intent = new Intent(v.getContext(),ActivateProfile.class);
                                assert response.errorBody() != null;
                                intent.putExtra("uuid", "ddcf9b0d-0abc-4953-9a5e-ed125fde5495");
                                startActivity(intent);
                                break;
                            default:
                                Toast.makeText(getApplicationContext(),R.string.insert_data_login,Toast.LENGTH_LONG).show();
                                break;
                        }
                        for (int i = 0; i < root.getChildCount(); i++) {
                            View child = root.getChildAt(i);
                            child.setEnabled(true);
                            child.setClickable(true);
                        }
                    }
                }

                @Override
                public void onFailure(Call<API.Response<JWT>> call, Throwable t) {
                    loginLoader.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),R.string.no_internet,Toast.LENGTH_SHORT).show();
                    //errorLogin.setText();
                    for (int i = 0; i < root.getChildCount(); i++) {
                        View child = root.getChildAt(i);
                        child.setEnabled(true);
                        child.setClickable(true);
                    }
                }
            });
        });
        buttonForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ForgotPassword.class);
            startActivity(intent);
        });
    }
}