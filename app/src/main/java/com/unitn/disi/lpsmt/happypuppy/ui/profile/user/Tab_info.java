package com.unitn.disi.lpsmt.happypuppy.ui.profile.user;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import com.unitn.disi.lpsmt.happypuppy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab_info extends Fragment {
    public Tab_info() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile_user_edit_info_fragment, container, false);
    }
}
