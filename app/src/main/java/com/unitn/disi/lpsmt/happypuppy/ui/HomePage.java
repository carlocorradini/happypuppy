package com.unitn.disi.lpsmt.happypuppy.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.ui.profile.puppy.ListPuppy;
import com.unitn.disi.lpsmt.happypuppy.ui.profile.user.ProfileUser;
import com.unitn.disi.lpsmt.happypuppy.util.ImageUtil;
import com.unitn.disi.lpsmt.happypuppy.util.UserUtil;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Home page {@link AppCompatActivity activity} with {@link GoogleMap map} integration
 *
 * @author Carlo Corradini
 */
public class HomePage extends AppCompatActivity {
    /**
     * {@link User} {@link Marker} size
     */
    private static final Pair<Integer, Integer> USER_MARKER_SIZE = Pair.of(128, 128);

    /**
     * Bottom Navigation View
     */
    BottomNavigationView bottomNavigation;
    /**
     * Current authenticated {@link User}
     */
    private User user = null;

    /**
     * Current authenticated {@link User} {@link Bitmap avatar}
     */
    private Bitmap userAvatar = null;

    /**
     * Top right {@link User} avatar
     */
    ImageView imageUserAvatar;

    /**
     * Top center title of fragment
     */
    private TextView titleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_activity);

        bottomNavigation = findViewById(R.id.home_page_bottom_nav);
        bottomNavigation.setOnNavigationItemSelectedListener(navListener);
        imageUserAvatar = findViewById(R.id.home_page_image_user_avatar);
        titleFragment = findViewById(R.id.home_page_name_app);

        imageUserAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProfileUser.class);
            /* PASS AuthManager.getInstance().getAuthUserId().toString() or UUID USER FOR TESTING: ddcf9b0d-0abc-4953-9a5e-ed125fde5495 */
            intent.putExtra("uuid_user", AuthManager.getInstance().getAuthUserId().toString());
            startActivity(intent);
        });


        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new MapFragment()).commit();
        // Load data
        loadData();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment;
                switch (item.getItemId()){
                    case R.id.nav_explore:
                        titleFragment.setText(getString(R.string.app_name));
                        selectedFragment = new MapFragment();
                        break;
                    case R.id.nav_puppies:
                        titleFragment.setText(getString(R.string.my_puppies));
                        selectedFragment = new ListPuppy();
                        break;
                    case R.id.nav_notification:
                        titleFragment.setText(getString(R.string.notification));
                        selectedFragment = new NotificationsFragment();
                        break;
                    default:
                        selectedFragment = new MapFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, selectedFragment).commit();
                return true;
            };

    /**
     * Load/Download useful data used in the current {@link ActivityCompat activity}
     * It can be called once.
     */
    private void loadData() {
        if (user != null && userAvatar != null) return;

        new UserUtil.DownloadAuthUser(user -> {
            if (user == null) return;
            this.user = user;
            Picasso.get().load(String.valueOf(this.user.avatar)).into(imageUserAvatar);
        }).execute();
    }
}
