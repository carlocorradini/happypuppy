package com.unitn.disi.lpsmt.happypuppy.ui.fragment;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;

import java.io.IOException;
import java.util.Objects;

/**
 * {@link GoogleMap} {@link FragmentActivity} map fragment
 *
 * @author Carlo Corradini
 */
public class MapsFragment extends FragmentActivity implements OnMapReadyCallback {
    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = MapsFragment.class.getName();

    /**
     * Access Fine Location permission identifier
     */
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;

    /**
     * Location update interval in ms
     */
    private static final long LOCATION_UPDATE_INTERVAL = 5000;

    /**
     * The map
     */
    private GoogleMap map;

    /**
     * Flag to check if the location permission has been granted
     */
    private boolean locationPermissionGranted = false;

    /**
     * {@link GoogleMap} location provider
     */
    private FusedLocationProviderClient fusedLocationProviderClient;

    /**
     * Location callback function called every LOCATION_UPDATE_INTERVAL
     */
    private LocationCallback locationUpdateCallback;

    /**
     * Current position marker on the {@link GoogleMap map}
     */
    private Marker locationMarker = null;

    /**
     * Current authenticated {@link User}
     */
    private User user = null;

    /**
     * Current authenticated {@link User} {@link Bitmap avatar}
     */
    private Bitmap userAvatar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps);

        // Maps layout
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // Maps
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationUpdateCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (locationMarker == null) {
                        // First loading
                        locationMarker = map.addMarker(new MarkerOptions().position(latLng));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
                    }

                    locationMarker.setPosition(latLng);
                    locationMarker.setTitle(user != null ? user.username : "YOU");
                    locationMarker.setIcon(userAvatar != null ? BitmapDescriptorFactory.fromBitmap(userAvatar) : null);
                    locationMarker.showInfoWindow();
                }
            }
        };

        // User data
        loadUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationPermissionGranted) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        getLocationPermission();
        setMapsUI();
        if (locationPermissionGranted)
            startLocationUpdates();
    }

    /**
     * Set {@link GoogleMap} UI.
     * The options may differ base on location permission.
     */
    private void setMapsUI() {
        if (map == null) return;

        try {
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setCompassEnabled(true);

            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Unable to update maps UI due to " + e.getMessage(), e);
        }
    }

    /**
     * Load current authenticated {@link User} data.
     * It can be called once.
     *
     * @see AuthManager
     */
    private void loadUserData() {
        if (user != null && userAvatar != null) return;

        Thread thread = new Thread(() -> {
            try {
                user = AuthManager.getInstance().getAuthUser();
                userAvatar = Bitmap.createScaledBitmap(
                        BitmapFactory.decodeStream(Objects.requireNonNull(user).avatar.openConnection().getInputStream()),
                        128, 128, false);
            } catch (IOException e) {
                Log.e(TAG, "Unable to load User avatar image due to " + e.getMessage(), e);
            }
        });
        thread.start();
    }

    /**
     * Start location updates
     */
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
                LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_NO_POWER)
                        .setInterval(LOCATION_UPDATE_INTERVAL),
                locationUpdateCallback,
                Looper.getMainLooper()
        );
    }

    /**
     * Stop location updates
     */
    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationUpdateCallback);
    }

    /**
     * Request map location permission, so that we can get the location of the device.
     * The result of the permission request is handled by a callback, onRequestPermissionsResult.
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }

        setMapsUI();
    }
}
