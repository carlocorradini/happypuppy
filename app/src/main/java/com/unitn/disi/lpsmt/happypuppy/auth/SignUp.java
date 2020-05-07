package com.unitn.disi.lpsmt.happypuppy.auth;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.unitn.disi.lpsmt.happypuppy.ui.components.DatePicker;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.entity.error.ConflictError;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
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
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

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
        buttonBack = findViewById(R.id.sign_up_button_back);

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
            if (validateUser(v,user)) {
                signUp(v, user);
            }
        });

        /* Listeners for edit text birth date: date picker */
        inputDateOfBirth.setInputType(InputType.TYPE_NULL);
        inputDateOfBirth.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePicker();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });

        buttonBack.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String userAge = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        inputDateOfBirth.setText(userAge);
    }

    private boolean validateUser(final View v,final User user) {
        if (user.username.isEmpty()) {
            new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(), v, getResources().getString(R.string.insert_username));
            return false;
        }
        if (user.email.isEmpty()) {
            new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(), v, getResources().getString(R.string.insert_email));
            return false;
        }
        if (user.password.isEmpty() || inputPasswordRepeat.getText().toString().isEmpty()) {
            new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(), v, getResources().getString(R.string.insert_password));
            return false;
        }
        if (!inputPhone.isValid()) {
            new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(), v, getResources().getString(R.string.insert_phone));
            return false;
        }
        if (!user.password.equals(inputPasswordRepeat.getText().toString())) {
            new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(), v, getResources().getString(R.string.pw_not_equals));
            return false;
        }
        if (!inputPrivacy.isChecked()) {
            new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(), v, getResources().getString(R.string.accept_eula));
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
                        case HttpStatus.SC_CONFLICT: {
                            String conflicts = "";
                            API.Response<List<ConflictError>> error = API.ErrorConverter.convert(response.errorBody(), API.ErrorConverter.TYPE_CONFLICT_LIST);
                            for (int i = 0; i < error.data.size(); i++) {
                                conflicts += error.data.get(i).property;
                                if(i != error.data.size()-1)
                                    conflicts += ", ";
                            }
                            System.out.println("INFO: " + conflicts);
                            String message = getResources().getString(R.string.conflicts_on)+conflicts;
                            new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(), v, message);
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
            public void onFailure(@NotNull Call<API.Response<UUID>> call, @NotNull Throwable t) {
                loader.setVisibility(View.GONE);
                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(true);
                    child.setClickable(true);
                }

                new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(), v, getResources().getString(R.string.no_internet));
            }
        });
    }
}