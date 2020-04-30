package com.unitn.disi.lpsmt.happypuppy.auth;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.happypuppy.DatePickerFragment;
import com.unitn.disi.lpsmt.happypuppy.HomePage;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputUser;
    private EditText inputEMail;
    private EditText inputPassword;
    private EditText inputRepeatPassword;
    private RadioButton inputMale;
    private RadioButton inputFemale;
    private Button buttonSignIn;
    private Button buttonSignUp;
    private TextView date;
    private IntlPhoneInput phoneInputView;
    private Switch accordPrivacy;
    public Calendar c;

    private LinearLayout signupLoader;
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_up);

        /* Layouts */
        root = findViewById(R.id.sign_up_root_view);
        signupLoader = findViewById(R.id.sign_up_view_loader);
        /* Input fields */
        inputFirstName = findViewById(R.id.sign_up_input_first_name);
        inputLastName = findViewById(R.id.sign_up_input_last_name);
        inputUser = findViewById(R.id.sign_up_input_username);
        phoneInputView =  findViewById(R.id.sign_up_input_phone_intl);
        inputEMail = findViewById(R.id.sign_up_input_email);
        inputPassword = findViewById(R.id.sign_up_input_password);
        inputRepeatPassword = findViewById(R.id.sign_up_input_password_repeat);
        date = findViewById(R.id.sign_up_input_age);
        accordPrivacy = findViewById(R.id.sign_up_accord_privacy);
        inputFemale = findViewById(R.id.sign_up_input_gender_female);
        inputMale = findViewById(R.id.sign_up_input_gender_male);

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
            //Intent intent = new Intent(v.getContext(), ActivateProfile.class);
            //startActivity(intent);
            User newUser;
            @Override
            public void onClick(View v) {
                switch(validate()){
                    case 1:
                        Toast.makeText(getApplicationContext(),R.string.insert_username,Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(),R.string.insert_email,Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(),R.string.insert_password,Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        Toast.makeText(getApplicationContext(),R.string.insert_phone,Toast.LENGTH_LONG).show();
                        break;
                    case 5:
                        Toast.makeText(getApplicationContext(),R.string.pw_not_equals,Toast.LENGTH_LONG).show();
                        break;
                    case 6:
                        Toast.makeText(getApplicationContext(),R.string.accept_eula,Toast.LENGTH_LONG).show();
                        break;
                    case 0:
                        signUp(v);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),R.string.internal_server_error,Toast.LENGTH_LONG).show();
                        break;
                }
            }
            private int validate() {
                newUser = new User();
                newUser.name = inputFirstName.getText().toString();
                newUser.surname = inputLastName.getText().toString();
                if(c != null)
                    newUser.dateOfBirth = c.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                newUser.username = inputUser.getText().toString();
                newUser.email = inputEMail.getText().toString();


                String pass = inputPassword.getText().toString();
                String cpass = inputRepeatPassword.getText().toString();
                /* Check empty data */
                if(newUser.username.isEmpty())
                    return 1;
                if(newUser.email.isEmpty())
                    return 2;
                if (pass.isEmpty() || cpass.isEmpty())
                    return 3;
                /* Check phone input valid, passwords equals and accord privacy accepted */
                if( !phoneInputView.isValid())
                    return 4;
                newUser.phone = phoneInputView.getNumber();
                if(!pass.equals(cpass))
                    return 5;
                newUser.password = pass;
                if(!accordPrivacy.isChecked())
                    return 6;
                /* Gender chose */
                if(inputFemale.isSelected())
                    newUser.gender = User.Gender.FEMALE;
                else if(inputMale.isSelected())
                    newUser.gender = User.Gender.MALE;
                else
                    newUser.gender = User.Gender.UNKNOWN;
                /* All fields are ok */
                return 0;
            }
            private void signUp(View v){
                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(false);
                    child.setClickable(false);
                }
                signupLoader.setVisibility(View.VISIBLE);
                Call<API.Response<UUID>> call = API.getInstance().getClient().create(UserService.class).create(newUser);
                call.enqueue(new Callback<API.Response<UUID>>() {
                    @Override
                    public void onResponse(Call<API.Response<UUID>> call, Response<API.Response<UUID>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            /* TODO: RESOLVE CRASH TEST WHEN USER SIGN UP */
                            Intent intent = new Intent(v.getContext(), ActivateProfile.class);
                            intent.putExtra("uuid", "ddcf9b0d-0abc-4953-9a5e-ed125fde5495");
                            startActivity(intent);
                        } else {
                            signupLoader.setVisibility(View.GONE);
                            switch (response.code()){
                                case 401:
                                    Toast.makeText(getApplicationContext(),R.string.wrong_login,Toast.LENGTH_LONG).show();
                                    break;
                                case 409:
                                    Toast.makeText(getApplicationContext(),R.string.user_already_exist,Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(),R.string.internal_server_error,Toast.LENGTH_LONG).show();
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
                    public void onFailure(Call<API.Response<UUID>> call, Throwable t) {
                        signupLoader.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),R.string.no_internet,Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < root.getChildCount(); i++) {
                            View child = root.getChildAt(i);
                            child.setEnabled(true);
                            child.setClickable(true);
                        }
                    }
                });
            }
        });

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
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String userAge = DateFormat.getDateInstance(DateFormat.MEDIUM).format(c.getTime());
        date.setText(userAge);
    }
}