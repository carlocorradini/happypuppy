package com.unitn.disi.lpsmt.happypuppy.ui.fragment.helper;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.AnimalPlace;
import com.unitn.disi.lpsmt.happypuppy.api.service.AnimalPlaceService;
import com.unitn.disi.lpsmt.happypuppy.util.ImageUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link GoogleMap Map} helper class
 *
 * @author Carlo Corradini
 */
public class MapHelper {
    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = MapHelper.class.getName();

    /**
     * Distance radius in meters of the Bounding Box from lastLocationPivot
     * used for filtering API resources that are near the current location
     */
    private static final int DISTANCE_RADIUS = 4000;

    /**
     * {@link AnimalPlace} {@link Marker} size
     */
    private static final Pair<Integer, Integer> ANIMAL_PLACE_MARKER_SIZE = Pair.of(96, 96);

    /**
     * The {@link GoogleMap} map instance
     */
    private final GoogleMap map;

    /**
     * {@link Map} that store the {@link Marker markers} of {@link AnimalPlace Animal Places}
     * on the {@link GoogleMap map }divided by {@link AnimalPlace.Type}.
     */
    private final Map<AnimalPlace.Type, List<Marker>> animalPlaces = new HashMap<AnimalPlace.Type, List<Marker>>() {
        {
            put(AnimalPlace.Type.PARK, new ArrayList<>());
            put(AnimalPlace.Type.SHOP, new ArrayList<>());
            put(AnimalPlace.Type.VETERINARY, new ArrayList<>());
            put(AnimalPlace.Type.GROOMING, new ArrayList<>());
        }
    };

    /**
     * Construct a new MapHelper class
     *
     * @param map {@link GoogleMap map} instance
     */
    public MapHelper(final GoogleMap map) {
        this.map = map;
    }

    /**
     * Load {@link AnimalPlace} that are at a maximum radius of DISTANCE_RADIUS from the given latLng location
     *
     * @param latLng The {@link LatLng} location used as pivot for calculating the Bounding Box
     */
    public void loadAnimalPlaces(LatLng latLng) {
        if (latLng == null) return;

        Call<API.Response<List<AnimalPlace>>> call = API.getInstance().getClient().create(AnimalPlaceService.class).findByRadius(latLng.latitude, latLng.longitude, DISTANCE_RADIUS);
        call.enqueue(new Callback<API.Response<List<AnimalPlace>>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<List<AnimalPlace>>> call, @NotNull Response<API.Response<List<AnimalPlace>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "Successful loaded Animal Places");
                    drawAnimalPlaces(response.body().data);
                } else {
                    Log.e(TAG, "Unable to load Animal Places due to unsuccessful response or empty body, received " + response.code());
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<List<AnimalPlace>>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to load Animal Places due to " + t.getMessage(), t);
            }
        });
    }

    /**
     * Load {@link AnimalPlace} that are at a maximum radius of DISTANCE_RADIUS from the given location
     *
     * @param location The {@link Location location} used as pivot for calculating the Bounding Box
     */
    public void loadAnimalPlaces(Location location) {
        if (location == null) return;
        loadAnimalPlaces(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    /**
     * Draw the given animalPlaceList onto the map as {@link Marker markers} and store them into animalPlaces.
     * The old {@link Marker markers} stored into animalPlaces and onto the map will be removed.
     *
     * @param animalPlaceList The {@link List} of {@link AnimalPlace} to draw on the map as {@link Marker}
     */
    private void drawAnimalPlaces(List<AnimalPlace> animalPlaceList) {
        if (animalPlaceList == null) return;

        // Clear Animal Place Markers from map
        for (Map.Entry<AnimalPlace.Type, List<Marker>> typePlaces : animalPlaces.entrySet()) {
            // Clear Markers
            for (Marker marker : typePlaces.getValue()) {
                marker.remove();
            }
            // Clear Marker List
            typePlaces.getValue().clear();
        }

        // Draw new Animal Place Markers
        for (AnimalPlace animalPlace : animalPlaceList) {
            if (animalPlace.type == null || !animalPlaces.containsKey(animalPlace.type)) continue;

            Bitmap icon = null;

            switch (animalPlace.type) {
                case PARK: {
                    icon = ImageUtil.fromDrawable(R.drawable.ic_map_marker_animal_place_park, ANIMAL_PLACE_MARKER_SIZE.getLeft(), ANIMAL_PLACE_MARKER_SIZE.getRight());
                    break;
                }
                case SHOP: {
                    icon = ImageUtil.fromDrawable(R.drawable.ic_map_marker_animal_place_shop, ANIMAL_PLACE_MARKER_SIZE.getLeft(), ANIMAL_PLACE_MARKER_SIZE.getRight());
                    break;
                }
                case GROOMING: {
                    icon = ImageUtil.fromDrawable(R.drawable.ic_map_marker_animal_place_grooming, ANIMAL_PLACE_MARKER_SIZE.getLeft(), ANIMAL_PLACE_MARKER_SIZE.getRight());
                    break;
                }
                case VETERINARY: {
                    icon = ImageUtil.fromDrawable(R.drawable.ic_map_marker_animal_place_veterinary, ANIMAL_PLACE_MARKER_SIZE.getLeft(), ANIMAL_PLACE_MARKER_SIZE.getRight());
                    break;
                }
            }

            Objects.requireNonNull(animalPlaces.get(animalPlace.type)).add(
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(animalPlace.latitude, animalPlace.longitude))
                            .title(animalPlace.name)
                            .snippet(StringUtils.capitalize(animalPlace.type.getValue()))
                            .icon(BitmapDescriptorFactory.fromBitmap(icon))));
        }

        Log.i(TAG, "Drawn " + animalPlaceList.size() + " Animal Places");
    }
}
