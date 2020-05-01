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
import com.unitn.disi.lpsmt.happypuppy.api.entity.UserVerification;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserVerificationService;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.apache.http.HttpStatus;

public class ActivateProfile extends AppCompatActivity {
    private UUID userId;

    private LinearLayout root;
    private Button buttonSendCodes;
    private Button buttonResendCodes;
    private EditText inputOtpSms;
    private EditText inputOtpEmail;
    private TextView txtMessage;
    private LinearLayout loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activate_profile);

        userId = UUID.fromString(getIntent().getStringExtra("uuid"));
        /* Elements */
        buttonSendCodes = findViewById(R.id.activate_profile_button_send);
        buttonResendCodes = findViewById(R.id.activate_profile_button_resend_codes);
        inputOtpEmail = findViewById(R.id.activate_profile_input_otp_mail);
        inputOtpSms = findViewById(R.id.activate_profile_input_otp_sms);
        txtMessage = findViewById(R.id.activate_profile_error_message);
        loader = findViewById(R.id.activate_profile_view_loader);
        root = findViewById(R.id.activate_profile_root_view);

        buttonSendCodes.setOnClickListener(v -> {
            if (!validate()) {
                Toast.makeText(getApplicationContext(), R.string.otp_codes_empty, Toast.LENGTH_SHORT).show();
                return;
            }

            UserVerification userVerification = new UserVerification();
            userVerification.user = userId;
            userVerification.otpPhone = inputOtpSms.getText().toString();
            userVerification.otpEmail = inputOtpEmail.getText().toString();

            for (int i = 0; i < root.getChildCount(); i++) {
                View child = root.getChildAt(i);
                child.setEnabled(false);
                child.setClickable(false);
            }
            loader.setVisibility(View.VISIBLE);

            Call<API.Response<JWT>> call = API.getInstance().getClient().create(UserVerificationService.class).verify(userVerification);
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
                            case HttpStatus.SC_FORBIDDEN: {
                                Toast.makeText(getApplicationContext(), R.string.user_already_verified, Toast.LENGTH_LONG).show();
                                break;
                            }
                            case HttpStatus.SC_NOT_FOUND: {
                                Toast.makeText(getApplicationContext(), R.string.user_not_found, Toast.LENGTH_LONG).show();
                                break;
                            }
                            case HttpStatus.SC_UNAUTHORIZED: {
                                Toast.makeText(getApplicationContext(), R.string.otp_codes_invalid, Toast.LENGTH_LONG).show();
                                break;
                            }
                            default: {
                                Toast.makeText(getApplicationContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.unknown_error, Toast.LENGTH_LONG).show();
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
                    loader.setVisibility(View.GONE);
                    for (int i = 0; i < root.getChildCount(); i++) {
                        View child = root.getChildAt(i);
                        child.setEnabled(true);
                        child.setClickable(true);
                    }

                    Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            });
        });

        buttonResendCodes.setOnClickListener(v -> {
            for (int i = 0; i < root.getChildCount(); i++) {
                View child = root.getChildAt(i);
                child.setEnabled(false);
                child.setClickable(false);
            }

            loader.setVisibility(View.VISIBLE);

            Call<API.Response> call = API.getInstance().getClient().create(UserVerificationService.class).resend(userId);
            call.enqueue(new Callback<API.Response>() {
                @Override
                public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getApplicationContext(), R.string.otp_codes_sent, Toast.LENGTH_SHORT).show();
                    } else if (response.errorBody() != null) {
                        switch (response.code()) {
                            case HttpStatus.SC_FORBIDDEN: {
                                Toast.makeText(getApplicationContext(), R.string.user_already_verified, Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case HttpStatus.SC_NOT_FOUND: {
                                Toast.makeText(getApplicationContext(), R.string.user_not_found, Toast.LENGTH_SHORT).show();
                                break;
                            }
                            default: {
                                Toast.makeText(getApplicationContext(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.unknown_error, Toast.LENGTH_LONG).show();
                    }

                    for (int i = 0; i < root.getChildCount(); i++) {
                        View child = root.getChildAt(i);
                        child.setEnabled(true);
                        child.setClickable(true);
                    }
                    loader.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                    loader.setVisibility(View.GONE);
                    for (int i = 0; i < root.getChildCount(); i++) {
                        View child = root.getChildAt(i);
                        child.setEnabled(true);
                        child.setClickable(true);
                    }

                    Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private boolean validate() {
        return !inputOtpEmail.getText().toString().isEmpty() && !inputOtpSms.getText().toString().isEmpty();
    }
}
