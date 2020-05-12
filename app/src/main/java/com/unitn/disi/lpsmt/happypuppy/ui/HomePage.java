package com.unitn.disi.lpsmt.happypuppy.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.ui.profile.puppy.ListPuppy;
import com.unitn.disi.lpsmt.happypuppy.ui.profile.user.ProfileUser;
import com.unitn.disi.lpsmt.happypuppy.helper.MapHelper;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_activity);

        bottomNavigation = findViewById(R.id.home_page_bottom_nav);
        bottomNavigation.setOnNavigationItemSelectedListener(navListener);
        imageUserAvatar = findViewById(R.id.home_page_image_user_avatar);

        imageUserAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProfileUser.class);
            /* PASS AuthManager.getInstance().getAuthUserId().toString() or UUID USER FOR TESTING: ddcf9b0d-0abc-4953-9a5e-ed125fde5495 */
            intent.putExtra("uuid_user", AuthManager.getInstance().getAuthUserId().toString());
            startActivity(intent);
        });


        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new ListPuppy()).commit();
        // Load data
        loadData();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            (BottomNavigationView.OnNavigationItemSelectedListener) item -> {
                Fragment selectedFragment = new MapFragment();
                switch (item.getItemId()){
                    case R.id.nav_explore:
                        selectedFragment = new MapFragment();
                        break;
                    case R.id.nav_puppies:
                        selectedFragment = new ListPuppy();
                        break;
                    case R.id.nav_notification:
                        selectedFragment = new ListPuppy();
                        break;
                    default:
                        selectedFragment = new MapFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, selectedFragment).commit();
                return true;
            };

    /**
     * Load/Download useful data used in the current {@link ActivityCompat activity} and {@link GoogleMap map}.
     * It can be called once.
     */
    private void loadData() {
        if (user != null && userAvatar != null) return;

        new UserUtil.DownloadAuthUser(user -> {
            if (user == null) return;
            this.user = user;
            new ImageUtil.DownloadImage(avatar -> {
                if (avatar == null) return;
                this.userAvatar = Bitmap.createScaledBitmap(avatar, USER_MARKER_SIZE.getLeft(), USER_MARKER_SIZE.getRight(), false);
                this.imageUserAvatar.setImageBitmap(avatar);
            }).execute(user.avatar);
        }).execute();
    }
}
