package com.unitn.disi.lpsmt.happypuppy.ui.profile.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.unitn.disi.lpsmt.happypuppy.R;

public class tab_password extends Fragment {
    public tab_password() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile_user_edit_password_fragment, container, false);
    }
}