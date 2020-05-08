package com.unitn.disi.lpsmt.happypuppy.ui.profile.user;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.unitn.disi.lpsmt.happypuppy.R;

public class EditUser extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;

    Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user_edit_activity);

        tabLayout = findViewById(R.id.profile_user_edit_tabLayout);
        viewPager = findViewById(R.id.profile_user_edit_viewPager);
        buttonBack = findViewById(R.id.profile_user_edit_button_back);

        Button confirmInfo = findViewById(R.id.profile_user_edit_button_save_changes);

        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.info)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.password)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final MyAdapter adapter = new MyAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        buttonBack.setOnClickListener(v -> finish());
    }
}
