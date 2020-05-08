package com.unitn.disi.lpsmt.happypuppy.ui.components;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.unitn.disi.lpsmt.happypuppy.ui.profile.user.Tab_info;
import com.unitn.disi.lpsmt.happypuppy.ui.profile.user.Tab_password;

public class AdapterEditProfile extends FragmentPagerAdapter {
    private int tabsNumber;

    public AdapterEditProfile(@NonNull FragmentManager fm, int behavior, int tabs) {
        super(fm, behavior);
        this.tabsNumber = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Tab_info();
            case 1:
                return new Tab_password();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabsNumber;
    }
}
