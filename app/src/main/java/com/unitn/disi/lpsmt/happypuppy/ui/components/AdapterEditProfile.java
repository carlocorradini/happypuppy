package com.unitn.disi.lpsmt.happypuppy.ui.components;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.unitn.disi.lpsmt.happypuppy.ui.profile.user.Tab_info;
import com.unitn.disi.lpsmt.happypuppy.ui.profile.user.Tab_password;

/**
 * AdapterEditProfile class
 * @author Anthony Farina
 */
public class AdapterEditProfile extends FragmentPagerAdapter {
    private int tabsNumber;

    /**
     *
     * @param fm fm
     * @param behavior behavior
     * @param tabs number of tabs
     */
    public AdapterEditProfile(@NonNull FragmentManager fm, int behavior, int tabs) {
        super(fm, behavior);
        this.tabsNumber = tabs;
    }

    /**
     *
     * @param position position
     * @return fragment
     */
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

    /**
     *
     * @return number of tabs
     */
    @Override
    public int getCount() {
        return tabsNumber;
    }
}
