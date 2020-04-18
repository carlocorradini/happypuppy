package com.unitn.disi.lpsmt.happypuppy;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.text.DateFormat;
import java.util.Calendar;

public class SignUp extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputUser;
    private EditText inputEMail;
    private EditText inputPassword;
    private EditText inputRepeatPassword;
    private Button buttonSignIn;
    private Button buttonSignUp;
    private TextView date;
    private IntlPhoneInput phoneInputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_up);

        /* Input fields */
        inputFirstName = findViewById(R.id.sign_up_input_first_name);
        inputLastName = findViewById(R.id.sign_up_input_last_name);
        inputUser = findViewById(R.id.sign_up_input_username);
        phoneInputView =  findViewById(R.id.sign_up_input_phone_intl);
        inputEMail = findViewById(R.id.sign_up_input_email);
        inputPassword = findViewById(R.id.sign_up_input_password);
        inputRepeatPassword = findViewById(R.id.sign_up_input_password_repeat);
        date = findViewById(R.id.sign_up_input_age);

        buttonSignIn = findViewById(R.id.sign_up_button_sign_in);
        buttonSignUp = findViewById(R.id.sign_up_button_sign_up);

        /* Reset hint for optional fields */
        inputFirstName.setHint(inputFirstName.getHint() +" "+  getString(R.string.optional_field));
        inputLastName.setHint(inputLastName.getHint() +" "+ getString(R.string.optional_field));
        date.setHint(date.getHint() +" "+ getString(R.string.optional_field));


        /* Button listeners */
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SignIn.class);
                startActivity(intent);
            }
        });
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SignUp.class);
                startActivity(intent); /* TODO: set layout for otp codes */
            }
        });

        /* TODO: verify input phone number */
        String myInternationalNumber;
        if(phoneInputView.isValid()) {
            myInternationalNumber = phoneInputView.getNumber();
        }

        /* Listeners for edit text birth date: date picker */
        date.setInputType(InputType.TYPE_NULL);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String userAge = DateFormat.getDateInstance(DateFormat.MEDIUM).format(c.getTime());
        date.setText(userAge);
    }
}