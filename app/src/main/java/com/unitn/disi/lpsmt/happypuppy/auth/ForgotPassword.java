package com.unitn.disi.lpsmt.happypuppy.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.happypuppy.HomePage;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {
    private Button sendEmail;
    private TextView sentData;
    private EditText emailField;
    private LinearLayout root;
    private LinearLayout loaderFP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        sendEmail = findViewById(R.id.forgot_password_send_data);
        sentData = findViewById(R.id.forgot_password_email_sent);
        root = findViewById(R.id.forgot_password_root_view);
        loaderFP = findViewById(R.id.forgot_password_view_loader);
        emailField = findViewById(R.id.forgot_password_input_email);


        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateMail()){
                    for (int i = 0; i < root.getChildCount(); i++) {
                        View child = root.getChildAt(i);
                        child.setEnabled(false);
                        child.setClickable(false);
                    }

                    Call<API.Response> call = API.getInstance().getClient().create(UserService.class).passwordReset(emailField.getText().toString());
                    call.enqueue(new Callback<API.Response>() {
                        @Override
                        public void onResponse(Call<API.Response> call, Response<API.Response> response) {
                            if (response.isSuccessful()) {
                                sentData.setVisibility(View.VISIBLE);
                            } else {
                                switch (response.code()) {
                                    case 200:
                                        Toast.makeText(getApplicationContext(),R.string.user_mail_not_found,Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Toast.makeText(getApplicationContext(),R.string.internal_server_error,Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                            for (int i = 0; i < root.getChildCount(); i++) {
                                View child = root.getChildAt(i);
                                child.setEnabled(true);
                                child.setClickable(true);
                            }
                        }
                        @Override
                        public void onFailure(Call<API.Response> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < root.getChildCount(); i++) {
                                View child = root.getChildAt(i);
                                child.setEnabled(true);
                                child.setClickable(true);
                            }
                        }
                    });
                }
                else
                    Toast.makeText(getApplicationContext(),R.string.insert_email,Toast.LENGTH_SHORT).show();
            }
            private boolean validateMail(){
                return !emailField.getText().toString().equals("");
            }
        });
    }
}
