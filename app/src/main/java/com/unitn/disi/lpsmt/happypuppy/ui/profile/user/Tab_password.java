package com.unitn.disi.lpsmt.happypuppy.ui.profile.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;
import com.unitn.disi.lpsmt.happypuppy.helper.ErrorHelper;
import com.unitn.disi.lpsmt.happypuppy.ui.components.Toasty;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tab_password extends Fragment {
    public EditText password;
    public EditText repeatPassword;
    public Button confirmPassword;
    public LinearLayout root;
    public LinearLayout loader;


    public Tab_password() {
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
        View view =  inflater.inflate(R.layout.profile_user_edit_password_fragment, container, false);

        root = view.findViewById(R.id.profile_user_edit_password_root_view);
        loader = view.findViewById(R.id.profile_user_password_view_loader);
        password = view.findViewById(R.id.profile_password_edit_input_password);
        repeatPassword = view.findViewById(R.id.profile_password_edit_input_password_repeat);
        confirmPassword = view.findViewById(R.id.profile_password_edit_button_save_password);


        confirmPassword.setOnClickListener(v -> {
            User user = new User();
            user.password = password.getText().toString();
            // Validation
            if (validatePassword(v,user)) {
                changePassword(v, user);
            }
        });
        return view;
    }
    private boolean validatePassword(View v, User user){
        if (user.password.isEmpty() || repeatPassword.getText().toString().isEmpty()) {
            new Toasty(getContext(), v, R.string.insert_password);
            return false;
        }
        if (!user.password.equals(repeatPassword.getText().toString())) {
            new Toasty(getContext(), v, R.string.pw_not_equals);
            return false;
        }
        return true;
    }
    private void changePassword(final View v, final User user) {
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
                    new com.unitn.disi.lpsmt.happypuppy.ui.components.ToastConfirm(getContext(), v, getResources().getString(R.string.changes_applied));
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
}