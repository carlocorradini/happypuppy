package com.unitn.disi.lpsmt.happypuppy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SignIn extends AppCompatActivity {

    private EditText inputUsername;
    private EditText inputPassword;
    private Button buttonSignIn;
    private Button buttonSignUp;
    private Button buttonForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_in);

        inputUsername = findViewById(R.id.sign_in_input_username);
        inputPassword = findViewById(R.id.sign_in_input_password);
        buttonSignIn = findViewById(R.id.sign_in_button_sign_in);
        buttonSignUp = findViewById(R.id.sign_in_button_sign_up);
        buttonForgotPassword = findViewById(R.id.sign_in_button_forgot_password);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SignUp.class);
                startActivity(intent);
            }
        });
    }

}