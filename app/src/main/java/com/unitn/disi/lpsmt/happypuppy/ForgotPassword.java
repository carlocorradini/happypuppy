package com.unitn.disi.lpsmt.happypuppy;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPassword extends AppCompatActivity {
    private ImageButton sendEmail;
    private TextView sentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        sendEmail = findViewById(R.id.forgot_password_send_data);
        sentData = findViewById(R.id.forgot_password_email_sent);


        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentData.setText(R.string.wrong_email);
                sentData.setVisibility(View.VISIBLE);
            }
        });
    }
}
