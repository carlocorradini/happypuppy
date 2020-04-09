package com.unitn.disi.lpsmt.happypuppy;

import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {

    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputUser;
    private EditText inputEMail;
    private EditText inputPassword;
    private EditText inputRepeatPassword;
    private Button buttonSignIn;
    private Button buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_up);

        inputFirstName = findViewById(R.id.sign_up_input_first_name);
        inputLastName = findViewById(R.id.sign_up_input_last_name);
        inputUser = findViewById(R.id.sign_up_input_username);
        inputEMail = findViewById(R.id.sign_up_input_email);
        inputPassword = findViewById(R.id.sign_up_input_password);
        inputRepeatPassword = findViewById(R.id.sign_up_input_password_repeat);

        buttonSignIn = findViewById(R.id.sign_up_button_sign_in);
        buttonSignUp = findViewById(R.id.sign_up_button_sign_up);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SignIn.class);
                startActivity(intent);
            }
        });
    }
}