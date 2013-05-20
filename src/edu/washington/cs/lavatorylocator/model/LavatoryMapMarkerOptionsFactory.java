package edu.washington.cs.lavatorylocator.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Factory for creating {@code MarkerOptions} used when inserting
 * {@code Marker}s with the Google Maps SDK.
 * 
 * @author Chris Rovillos
 * 
 */
public class LavatoryMapMarkerOptionsFactory {

    // Prevent instantiation of this class
    private LavatoryMapMarkerOptionsFactory() {
    }

    /**
     * Creates a new {@code MarkerOptions} for the given {@link LavatoryData}.
     * 
     * @param lavatoryData
     *            the {@link LavatoryData} for which to create the
     *            {@code MarkerOptions}
     * 
     * @return a new {@code MarkerOptions} for the given {@link LavatoryData}
     */
    public static MarkerOptions createLavatoryMapMarkerOptions(
            LavatoryData lavatoryData) {
        String name = lavatoryData.getName();

        double latitude = lavatoryData.getLatitude();
        double longitude = lavatoryData.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        return new MarkerOptions().position(latLng).title(name);
    }
}
