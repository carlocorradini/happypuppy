package com.unitn.disi.lpsmt.happypuppy.ui.profile.user;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.ui.components.AdapterEditProfile;
import com.unitn.disi.lpsmt.happypuppy.ui.components.DatePicker;
import com.unitn.disi.lpsmt.happypuppy.util.UserUtil;

import java.sql.SQLOutput;
import java.text.DateFormat;
import java.util.Calendar;

public class EditUser extends AppCompatActivity {
    LinearLayout root;
    DrawerLayout drawerLayout;
    ViewPager pager;
    TabLayout mTabLayout;
    TabItem tabPersonalData,tabPassword;
    PagerAdapter adapter;

    LinearLayout loader;
    Button buttonBack;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user_edit_activity);

        //UI components map
        mTabLayout = findViewById(R.id.profile_user_edit_tabLayout);
        pager = findViewById(R.id.profile_user_edit_viewPager);
        buttonBack = findViewById(R.id.profile_user_edit_button_back);

        drawerLayout = findViewById(R.id.profile_user_edit_drawer_view);
        final AdapterEditProfile adapter = new AdapterEditProfile(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, mTabLayout.getTabCount());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
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
