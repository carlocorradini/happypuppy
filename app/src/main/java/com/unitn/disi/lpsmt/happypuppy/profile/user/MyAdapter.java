package com.unitn.disi.lpsmt.happypuppy.profile.user;

import android.content.Context;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

public class MyAdapter extends FragmentPagerAdapter {

    private Context myContext;
    private int totalTabs;

    public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                tab_info info = new tab_info();
                return info;
            case 1:
                tab_password password = new tab_password();
                return password;
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}
