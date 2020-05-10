package com.unitn.disi.lpsmt.happypuppy.ui.profile.user;

import android.app.DatePickerDialog;
import android.os.Bundle;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;
import com.unitn.disi.lpsmt.happypuppy.ui.components.DatePicker;
import com.unitn.disi.lpsmt.happypuppy.util.UserUtil;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab_info extends Fragment implements DatePickerDialog.OnDateSetListener{
    public EditText firstName;
    public EditText lastName;
    public RadioButton inputMale;
    public RadioButton inputFemale;
    public EditText birthDate;
    public Button confirmInfo;
    public LinearLayout root;
    public LinearLayout loader;
    User user;
    Calendar calendar;

    public Tab_info() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profile_user_edit_info_fragment, container, false);

        root = view.findViewById(R.id.profile_user_edit_info_root_view);
        loader = view.findViewById(R.id.profile_user_edit_info_view_loader);
        firstName = view.findViewById(R.id.profile_user_edit_input_first_name);
        lastName = view.findViewById(R.id.profile_user_edit_input_last_name);
        inputFemale = view.findViewById(R.id.profile_user_edit_input_gender_female);
        inputMale = view.findViewById(R.id.profile_user_edit_input_gender_male);
        birthDate = view.findViewById(R.id.profile_user_edit_input_age);
        confirmInfo = view.findViewById(R.id.profile_user_edit_button_save_changes);

        /* Listeners for edit text birth date: date picker */
        birthDate.setInputType(InputType.TYPE_NULL);
        birthDate.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePicker();
            datePicker.show(requireFragmentManager() , "date picker");
        });

        confirmInfo.setOnClickListener(v -> {
            User user = new User();
            user.gender = User.Gender.UNKNOWN;
            if (!firstName.getText().toString().isEmpty()) {
                user.name = firstName.getText().toString();
            }
            if (!lastName.getText().toString().isEmpty()) {
                user.surname = lastName.getText().toString();
            }
            if (calendar != null) {
                user.dateOfBirth = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
            if (inputFemale.isSelected())
                user.gender = User.Gender.FEMALE;
            else if (inputMale.isSelected())
                user.gender = User.Gender.MALE;

            // Validation
            updateUser(v, user);
        });

        loadData();
        return view;
    }
    private void updateUser(final View v, final User user) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            child.setEnabled(false);
            child.setClickable(false);
        }

        loader.setVisibility(View.VISIBLE);

        Call<API.Response> call = API.getInstance().getClient().create(UserService.class).update(user);
        call.enqueue(new Callback<API.Response>() {
            @Override
            public void onResponse(Call<API.Response> call, Response<API.Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new com.unitn.disi.lpsmt.happypuppy.ui.components.ToastConfirm(getContext(), v, getResources().getString(R.string.changes_applied));
                } else {
                    new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getContext(), v, getResources().getString(R.string.unknown_error));
                }
                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(true);
                    child.setClickable(true);
                }
                loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<API.Response> call, Throwable t) {
                loader.setVisibility(View.GONE);
                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(true);
                    child.setClickable(true);
                }

                new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getContext(), v, getResources().getString(R.string.no_internet));
            }
        });
    }

    private void loadData() {
        if (user != null ) return;

        new UserUtil.DownloadAuthUser(user -> {
            this.user = user;

            if(this.user.name != null)
                firstName.setText(this.user.name);
            if(this.user.surname != null)
                lastName.setText(this.user.surname);
            if(this.user.gender == User.Gender.MALE)
                inputMale.setChecked(true);
            else if(this.user.gender == User.Gender.FEMALE)
                inputFemale.setChecked(true);
            if(this.user.dateOfBirth != null)
                birthDate.setText(this.user.dateOfBirth.toString());
        }).execute();
    }
    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String userAge = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        birthDate.setText(userAge);
    }
}
