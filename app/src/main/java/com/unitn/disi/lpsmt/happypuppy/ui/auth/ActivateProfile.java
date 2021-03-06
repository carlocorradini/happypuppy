package com.unitn.disi.lpsmt.happypuppy.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.UserVerification;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserVerificationService;
import com.unitn.disi.lpsmt.happypuppy.helper.ErrorHelper;
import com.unitn.disi.lpsmt.happypuppy.ui.HomePage;
import com.unitn.disi.lpsmt.happypuppy.ui.components.Toasty;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ActivateProfile class
 * @author Anthony Farina
 */
public class ActivateProfile extends AppCompatActivity {
    private UUID userId;

    private LinearLayout root;
    private Button buttonSendCodes;
    private Button buttonResendCodes;
    private EditText inputOtpSms;
    private EditText inputOtpEmail;
    private LinearLayout loader;

    /**
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activate_profile_activity);

        userId = UUID.fromString(getIntent().getStringExtra("uuid"));
        /* Elements */
        buttonSendCodes = findViewById(R.id.activate_profile_button_send);
        buttonResendCodes = findViewById(R.id.activate_profile_button_resend_codes);
        inputOtpEmail = findViewById(R.id.activate_profile_input_otp_mail);
        inputOtpSms = findViewById(R.id.activate_profile_input_otp_sms);
        loader = findViewById(R.id.activate_profile_view_loader);
        root = findViewById(R.id.activate_profile_root_view);

        buttonSendCodes.setOnClickListener(v -> {
            if (!validate()) {
                new Toasty(getBaseContext(), v, R.string.otp_codes_empty);
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
                /**
                 *
                 * @param call call with api
                 * @param response response with data
                 */
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
                                new Toasty(getBaseContext(), v, R.string.user_already_verified);
                                break;
                            }
                            case HttpStatus.SC_NOT_FOUND: {
                                new Toasty(getBaseContext(), v, R.string.user_not_found);
                                break;
                            }
                            case HttpStatus.SC_UNAUTHORIZED: {
                                new Toasty(getBaseContext(), v, R.string.otp_codes_invalid);
                                break;
                            }
                            default: {
                                new Toasty(getBaseContext(), v, R.string.internal_server_error);
                                break;
                            }
                        }
                    } else {
                        new Toasty(getApplicationContext(), v, R.string.error_unknown);
                    }

                    for (int i = 0; i < root.getChildCount(); i++) {
                        View child = root.getChildAt(i);
                        child.setEnabled(true);
                        child.setClickable(true);
                    }
                    loader.setVisibility(View.GONE);
                }

                /**
                 *
                 * @param call call with API
                 * @param t throwable
                 */
                @Override
                public void onFailure(@NotNull Call<API.Response<JWT>> call, @NotNull Throwable t) {
                    ErrorHelper.showFailureError(getBaseContext(), v, t);
                    loader.setVisibility(View.GONE);
                    for (int i = 0; i < root.getChildCount(); i++) {
                        View child = root.getChildAt(i);
                        child.setEnabled(true);
                        child.setClickable(true);
                    }
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
                /**
                 *
                 * @param call call with API
                 * @param response response with data
                 */
                @Override
                public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        new Toasty(getBaseContext(), v, R.string.otp_codes_sent);
                    } else if (response.errorBody() != null) {
                        switch (response.code()) {
                            case HttpStatus.SC_FORBIDDEN: {
                                new Toasty(getBaseContext(), v, R.string.user_already_verified);
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
                        new Toasty(getApplicationContext(), v, R.string.error_unknown);
                    }

                    for (int i = 0; i < root.getChildCount(); i++) {
                        View child = root.getChildAt(i);
                        child.setEnabled(true);
                        child.setClickable(true);
                    }
                    loader.setVisibility(View.GONE);
                }

                /**
                 *
                 * @param call call with API
                 * @param t throwable
                 */
                @Override
                public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                    ErrorHelper.showFailureError(getBaseContext(), v, t);
                    loader.setVisibility(View.GONE);
                    for (int i = 0; i < root.getChildCount(); i++) {
                        View child = root.getChildAt(i);
                        child.setEnabled(true);
                        child.setClickable(true);
                    }
                }
            });
        });
    }

    /**
     *
     * @return boolean
     */
    private boolean validate() {
        return !inputOtpEmail.getText().toString().isEmpty() && !inputOtpSms.getText().toString().isEmpty();
    }
}
