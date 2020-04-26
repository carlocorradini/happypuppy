package com.unitn.disi.lpsmt.happypuppy.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.unitn.disi.lpsmt.happypuppy.HomePage;
import com.unitn.disi.lpsmt.happypuppy.R;

public class ActivateProfile extends AppCompatActivity {
    private Button sendCodes;
    private Button resendCodes;
    private EditText otpSMS;
    private EditText otpEmail;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activate_profile);

        /* Elements of the activity */
        sendCodes = findViewById(R.id.activate_profile_button_send);
        resendCodes = findViewById(R.id.activate_profile_button_resend_codes);
        otpEmail = findViewById(R.id.activate_profile_input_otp_mail);
        otpSMS = findViewById(R.id.activate_profile_input_otp_sms);
        message = findViewById(R.id.activate_profile_error_message);



        sendCodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HomePage.class);
                startActivity(intent);
            }
        });
        resendCodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
