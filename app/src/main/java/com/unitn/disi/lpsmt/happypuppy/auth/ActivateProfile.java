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
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserVerificationService;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivateProfile extends AppCompatActivity {
    private Button sendCodes;
    private Button resendCodes;
    private EditText otpSMS;
    private EditText otpEmail;
    private TextView message;

    private UserVerification confirmNewUser;

    private LinearLayout activateProfileLoader;
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activate_profile);
        Intent thisIntent = getIntent(); // gets the previously created intent
        confirmNewUser = new UserVerification();
        confirmNewUser.user = UUID.fromString(thisIntent.getStringExtra("uuid"));
        /* Elements of the activity */
        sendCodes = findViewById(R.id.activate_profile_button_send);
        resendCodes = findViewById(R.id.activate_profile_button_resend_codes);
        otpEmail = findViewById(R.id.activate_profile_input_otp_mail);
        otpSMS = findViewById(R.id.activate_profile_input_otp_sms);
        message = findViewById(R.id.activate_profile_error_message);
        activateProfileLoader = findViewById(R.id.activate_profile_view_loader);
        root = findViewById(R.id.activate_profile_root_view);


        sendCodes.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (validate()) {
                     verifyCodes(v);
                 } else {
                     Toast.makeText(getApplicationContext(), R.string.unauthorized, Toast.LENGTH_SHORT).show();
                 }
             }

             private boolean validate() {
                 if (otpEmail.getText().toString().equals("") || otpSMS.getText().toString().equals(""))
                     return false;
                 return true;
             }

             private void verifyCodes(View v) {
                 for (int i = 0; i < root.getChildCount(); i++) {
                     View child = root.getChildAt(i);
                     child.setEnabled(false);
                     child.setClickable(false);
                 }
                 activateProfileLoader.setVisibility(View.VISIBLE);
                 /* Fill UserVerification with codes */
                 confirmNewUser.otpPhone = otpSMS.getText().toString();
                 confirmNewUser.otpEmail = otpEmail.getText().toString();
                 Call<API.Response<String>> call = API.getInstance().getClient().create(UserVerificationService.class).verify(confirmNewUser);
                 call.enqueue(new Callback<API.Response<String>>() {
                     @Override
                     public void onResponse(Call<API.Response<String>> call, Response<API.Response<String>> response) {
                         if (response.isSuccessful() && response.body() != null) {
                             AuthManager.getInstance().setToken(new JWT(response.body().data));
                             Intent intent = new Intent(v.getContext(), HomePage.class);
                             startActivity(intent);
                         } else {
                             activateProfileLoader.setVisibility(View.GONE);
                             switch (response.code()) {
                                 case 403:
                                     Toast.makeText(getApplicationContext(), R.string.user_already_exist, Toast.LENGTH_LONG).show();
                                     break;
                                 case 404:
                                     Toast.makeText(getApplicationContext(), R.string.user_not_found, Toast.LENGTH_LONG).show();
                                     break;
                                 default:
                                     Toast.makeText(getApplicationContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show();
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
                     public void onFailure(Call<API.Response<String>> call, Throwable t) {
                         activateProfileLoader.setVisibility(View.GONE);
                         Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                         for (int i = 0; i < root.getChildCount(); i++) {
                             View child = root.getChildAt(i);
                             child.setEnabled(true);
                             child.setClickable(true);
                         }
                     }
                 });
             }
        });
        resendCodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<API.Response> call = API.getInstance().getClient().create(UserVerificationService.class).resend(confirmNewUser.user);
                call.enqueue(new Callback<API.Response>() {
                    @Override
                    public void onResponse(Call<API.Response> call, Response<API.Response> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(getApplicationContext(),R.string.otp_codes_sent,Toast.LENGTH_SHORT).show();
                        } else {
                            switch (response.code()) {
                                case 403:
                                    Toast.makeText(getApplicationContext(),R.string.unauthorized,Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(),R.string.internal_server_error,Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<API.Response> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),R.string.no_internet,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
