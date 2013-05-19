package edu.washington.cs.lavatorylocator.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class LavatoryMapMarkerOptionsFactory {
    // prevent instantiation of class
    private LavatoryMapMarkerOptionsFactory() {}
    
    public static MarkerOptions createLavatoryMapMarkerOptions(LavatoryData lavatoryData) {
        String name = lavatoryData.getName();
        
        double latitude = lavatoryData.getLatitude();
        double longitude = lavatoryData.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        
        return new MarkerOptions().position(latLng).title(name);
    }
}
