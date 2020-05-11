package com.unitn.disi.lpsmt.happypuppy.ui.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

public class ForgotPassword extends AppCompatActivity {
    private LinearLayout root;
    private Button buttonSendEmail;
    private EditText inputEmail;
    private TextView txtResultData;
    private LinearLayout loader;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_activity);

        root = findViewById(R.id.forgot_password_root_view);
        buttonSendEmail = findViewById(R.id.forgot_password_send_data);
        inputEmail = findViewById(R.id.forgot_password_input_email);
        txtResultData = findViewById(R.id.forgot_password_email_sent);
        loader = findViewById(R.id.forgot_password_view_loader);
        buttonBack = findViewById(R.id.forgot_password_button_back);

        buttonSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateMail()) {
                    new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(), v, getResources().getString(R.string.insert_email));
                    return;
                }

                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(false);
                    child.setClickable(false);
                }

                Call<API.Response> call = API.getInstance().getClient().create(UserService.class).passwordReset(inputEmail.getText().toString());
                call.enqueue(new Callback<API.Response>() {
                    @Override
                    public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            txtResultData.setVisibility(View.VISIBLE);
                        } else if (response.errorBody() != null) {
                            switch (response.code()) {
                                case HttpStatus.SC_NOT_FOUND: {
                                    new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(), v, getResources().getString(R.string.user_mail_not_found));
                                    break;
                                }
                                default: {
                                    new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(), v, getResources().getString(R.string.internal_server_error));
                                    break;
                                }
                            }
                        } else {
                            new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(), v, getResources().getString(R.string.unknown_error));
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
                        new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(), v, getResources().getString(R.string.no_internet));
                        for (int i = 0; i < root.getChildCount(); i++) {
                            View child = root.getChildAt(i);
                            child.setEnabled(true);
                            child.setClickable(true);
                        }
                    }
                });
            }

            private boolean validateMail() {
                return !inputEmail.getText().toString().isEmpty();
            }
        });
        buttonBack.setOnClickListener(v -> finish());
    }
}