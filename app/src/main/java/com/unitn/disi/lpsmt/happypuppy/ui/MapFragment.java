package com.unitn.disi.lpsmt.happypuppy.ui;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.helper.MapHelper;
import com.unitn.disi.lpsmt.happypuppy.util.ImageUtil;
import com.unitn.disi.lpsmt.happypuppy.util.UserUtil;

import org.apache.commons.lang3.tuple.Pair;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = HomePage.class.getName();

    /**
     * Access Fine Location permission identifier
     */
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;

    /**
     * Location update interval in ms
     */
    private static final long LOCATION_UPDATE_INTERVAL = 5000;

    /**
     * Default map zoom
     */
    private static final float DEFAULT_ZOOM = 17.0f;

    /**
     * Threshold in meters used for reloading API resources that defines the maximum distance from lastLocationPivot
     */
    private static final float DISTANCE_THRESHOLD = 2000f;

    /**
     * {@link User} {@link Marker} size
     */
    private static final Pair<Integer, Integer> USER_MARKER_SIZE = Pair.of(128, 128);

    /**
     * The {@link GoogleMap} map instance
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
     * Current position marker of the {@link User} on the {@link GoogleMap map}
     */
    private Marker locationMarker = null;

    /**
     * Last {@link Location} of the {@link User} used for API resource filtering
     */
    private Location lastLocationPivot = null;

    /**
     * Current authenticated {@link User}
     */
    private User user;

    /**
     * Current authenticated {@link User} {@link Bitmap avatar}
     */
    private Bitmap userAvatar = null;

    /**
     * Map Helper class instance
     */
    private MapHelper mapHelper;

    /**
     * View of fragment
     */
    private View view;

    /**
     * {@link MapView} of map
     */
    private MapView mapView;

    /**
     * Construct a {@link MapFragment} class
     *
     * @param user The current authenticated {@link User} to display on the {@link GoogleMap map}
     */
    public MapFragment(final User user) {
        this.user = user;
        loadData();
    }

    /**
     * Construct a {@link MapFragment} class
     */
    public MapFragment() {
        this(null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.explore_map_fragment, container, false);

        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        initMap();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (locationPermissionGranted) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        mapHelper = new MapHelper(map);

        getLocationPermission();
        setMapsUI();
        if (locationPermissionGranted)
            startLocationUpdates();
    }

    /**
     * Initialize {@link GoogleMap map} layout and functionality
     */
    private void initMap() {
        MapsInitializer.initialize(requireActivity().getApplicationContext());
        mapView.getMapAsync(this);

        // Maps provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        // Update location callback
        locationUpdateCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if (locationMarker == null) {
                        // First loading
                        locationMarker = map.addMarker(new MarkerOptions().position(latLng));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                        lastLocationPivot = location;
                        mapHelper.loadAnimalPlaces(latLng);
                    } else if (lastLocationPivot != null && lastLocationPivot.distanceTo(location) >= DISTANCE_THRESHOLD) {
                        // Distance Threshold reached
                        lastLocationPivot = location;
                        mapHelper.loadAnimalPlaces(location);
                    }

                    locationMarker.setPosition(latLng);
                    locationMarker.setTitle(user != null ? user.username : "YOU");
                    locationMarker.setIcon(userAvatar != null ? BitmapDescriptorFactory.fromBitmap(userAvatar) : null);
                }
            }
        };
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
        if (ContextCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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

    /**
     * Load/Download useful data used in the current {@link ActivityCompat activity} and {@link GoogleMap map}.
     * It can be called once.
     */
    private void loadData() {
        if(user != null && userAvatar != null) return;

        new UserUtil.DownloadAuthUser(user -> {
            if (user == null) return;
            this.user = user;
            new ImageUtil.DownloadImage(avatar -> {
                if (avatar == null) return;
                this.userAvatar = Bitmap.createScaledBitmap(avatar, USER_MARKER_SIZE.getLeft(), USER_MARKER_SIZE.getRight(), false);
            }).execute(user.avatar);
        }).execute();
    }
}
