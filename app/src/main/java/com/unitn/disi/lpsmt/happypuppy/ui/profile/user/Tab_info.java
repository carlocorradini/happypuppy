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

import androidx.fragment.app.Fragment;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;
import com.unitn.disi.lpsmt.happypuppy.helper.ErrorHelper;
import com.unitn.disi.lpsmt.happypuppy.ui.components.Toasty;
import com.unitn.disi.lpsmt.happypuppy.ui.components.ToastyConfirm;
import com.unitn.disi.lpsmt.happypuppy.util.UserUtil;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab_info extends Fragment {
    public EditText firstName;
    public EditText lastName;
    public RadioButton inputMale;
    public RadioButton inputFemale;
    public EditText birthDate;
    public Button confirmInfo;
    public LinearLayout root;
    public LinearLayout loader;
    Calendar calendarDate;
    User user;
    View view;

    public Tab_info() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.profile_user_edit_info_fragment, container, false);

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
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (view, year, month, dayOfMonth) -> {
                Calendar dateOfBirth = Calendar.getInstance();
                dateOfBirth.set(year, month, dayOfMonth);
                System.out.println(dateOfBirth.toString());
                user.dateOfBirth = LocalDate.of(year, month + 1, dayOfMonth);
                System.out.println(user.dateOfBirth
                );
                birthDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateOfBirth.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        });

        confirmInfo.setOnClickListener(v -> {
            User user = new User();
            user.gender = User.Gender.UNKNOWN;
            if (!firstName.getText().toString().isEmpty()) {
                user.name = firstName.getText().toString();
            }else
                user.name = "";
            if (!lastName.getText().toString().isEmpty()) {
                user.surname = lastName.getText().toString();
            }else
                user.surname = "";
            user.dateOfBirth = this.user.dateOfBirth;
            if (inputFemale.isChecked())
                user.gender = User.Gender.FEMALE;
            else if (inputMale.isChecked())
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
            public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new ToastyConfirm(getContext(), v, getResources().getString(R.string.changes_applied));
                } else {
                    new Toasty(getContext(), v, R.string.error_unknown);
                }
                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(true);
                    child.setClickable(true);
                }
                loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                ErrorHelper.showFailureError(getContext(), v, t);
                loader.setVisibility(View.GONE);
                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(true);
                    child.setClickable(true);
                }
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
}
