package com.unitn.disi.lpsmt.happypuppy.ui.profile.user;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.ui.components.AdapterEditProfile;

public class EditUser extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ViewPager pager;
    TabLayout mTabLayout;
    TabItem tabPersonalData,tabPassword;
    PagerAdapter adapter;

    Button confirmInfo;
    Button confirmPassword;
    Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user_edit_activity);


        mTabLayout = findViewById(R.id.profile_user_edit_tabLayout);
        pager = findViewById(R.id.profile_user_edit_viewPager);
        buttonBack = findViewById(R.id.profile_user_edit_button_back);

        confirmInfo = findViewById(R.id.profile_user_edit_button_save_changes);
        confirmPassword = findViewById(R.id.profile_user_edit_button_save_password);

        tabPersonalData = findViewById(R.id.firstItem);
        tabPassword = findViewById(R.id.secondItem);

        drawerLayout = findViewById(R.id.profile_user_edit_drawer_view);
        final AdapterEditProfile adapter = new AdapterEditProfile(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, mTabLayout.getTabCount());
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));


        buttonBack.setOnClickListener(v -> finish());
    }
}
