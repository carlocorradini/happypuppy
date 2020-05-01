package com.unitn.disi.lpsmt.happypuppy.auth;

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

import com.unitn.disi.lpsmt.happypuppy.DatePickerFragment;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.apache.http.HttpStatus;

public class SignUp extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private LinearLayout root;
    private EditText inputName;
    private EditText inputSurname;
    private EditText inputUsername;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputPasswordRepeat;
    private RadioButton inputMale;
    private RadioButton inputFemale;
    private IntlPhoneInput inputPhone;
    private TextView inputDateOfBirth;
    private Button buttonSignIn;
    private Button buttonSignUp;
    private Switch inputPrivacy;
    public Calendar calendar;
    private LinearLayout loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_up);

        /* Layouts */
        root = findViewById(R.id.sign_up_root_view);
        loader = findViewById(R.id.sign_up_view_loader);
        /* Input fields */
        inputName = findViewById(R.id.sign_up_input_first_name);
        inputSurname = findViewById(R.id.sign_up_input_last_name);
        inputUsername = findViewById(R.id.sign_up_input_username);
        inputPhone = findViewById(R.id.sign_up_input_phone_intl);
        inputEmail = findViewById(R.id.sign_up_input_email);
        inputPassword = findViewById(R.id.sign_up_input_password);
        inputPasswordRepeat = findViewById(R.id.sign_up_input_password_repeat);
        inputDateOfBirth = findViewById(R.id.sign_up_input_age);
        inputPrivacy = findViewById(R.id.sign_up_accord_privacy);
        inputFemale = findViewById(R.id.sign_up_input_gender_female);
        inputMale = findViewById(R.id.sign_up_input_gender_male);

        buttonSignIn = findViewById(R.id.sign_up_button_sign_in);
        buttonSignUp = findViewById(R.id.sign_up_button_sign_up);

        /* Reset hint for optional fields */
        inputName.setHint(inputName.getHint() + " " + getString(R.string.optional_field));
        inputSurname.setHint(inputSurname.getHint() + " " + getString(R.string.optional_field));
        inputDateOfBirth.setHint(inputDateOfBirth.getHint() + " " + getString(R.string.optional_field));


        /* Button listeners */
        buttonSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SignIn.class);
            startActivity(intent);
            finish();
        });

        buttonSignUp.setOnClickListener(v -> {
            User user = new User();
            user.username = inputUsername.getText().toString();
            user.password = inputPassword.getText().toString();
            user.email = inputEmail.getText().toString();
            user.phone = inputPhone.getNumber();
            user.gender = User.Gender.UNKNOWN;
            if (!inputName.getText().toString().isEmpty()) {
                user.name = inputName.getText().toString();
            }
            if (!inputSurname.getText().toString().isEmpty()) {
                user.surname = inputSurname.getText().toString();
            }
            if (calendar != null) {
                user.dateOfBirth = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
            if (inputFemale.isSelected())
                user.gender = User.Gender.FEMALE;
            else if (inputMale.isSelected())
                user.gender = User.Gender.MALE;

            // Validation
            if (validateUser(user)) {
                signUp(v, user);
            }
        });

        /* Listeners for edit text birth date: date picker */
        inputDateOfBirth.setInputType(InputType.TYPE_NULL);
        inputDateOfBirth.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String userAge = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        inputDateOfBirth.setText(userAge);
    }

    private boolean validateUser(final User user) {
        if (user.username.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.insert_username, Toast.LENGTH_LONG).show();
            return false;
        }
        if (user.email.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.insert_email, Toast.LENGTH_LONG).show();
            return false;
        }
        if (user.password.isEmpty() || inputPasswordRepeat.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.insert_password, Toast.LENGTH_LONG).show();
            return false;
        }
        if (!inputPhone.isValid()) {
            Toast.makeText(getApplicationContext(), R.string.insert_phone, Toast.LENGTH_LONG).show();
            return false;
        }
        if (!user.password.equals(inputPasswordRepeat.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.pw_not_equals, Toast.LENGTH_LONG).show();
            return false;
        }
        if (!inputPrivacy.isChecked()) {
            Toast.makeText(getApplicationContext(), R.string.accept_eula, Toast.LENGTH_LONG).show();
            return false;
        }

        /* All fields ok */
        return true;
    }

    private void signUp(final View v, final User user) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            child.setEnabled(false);
            child.setClickable(false);
        }

        loader.setVisibility(View.VISIBLE);

        Call<API.Response<UUID>> call = API.getInstance().getClient().create(UserService.class).create(user);
        call.enqueue(new Callback<API.Response<UUID>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<UUID>> call, @NotNull Response<API.Response<UUID>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(v.getContext(), ActivateProfile.class);
                    intent.putExtra("uuid", response.body().data.toString());
                    startActivity(intent);
                    finish();
                } else if (response.errorBody() != null) {
                    switch (response.code()) {
                        case HttpStatus.SC_UNAUTHORIZED: {
                            Toast.makeText(getApplicationContext(), R.string.wrong_login, Toast.LENGTH_LONG).show();
                            break;
                        }
                        case HttpStatus.SC_CONFLICT: {
                            Toast.makeText(getApplicationContext(), R.string.user_already_exist, Toast.LENGTH_LONG).show();
                            break;
                        }
                        default: {
                            Toast.makeText(getApplicationContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.unknown_error, Toast.LENGTH_LONG).show();
                }

                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(true);
                    child.setClickable(true);
                }
                loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<UUID>> call, @NotNull Throwable t) {
                loader.setVisibility(View.GONE);
                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(true);
                    child.setClickable(true);
                }

                Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }
}