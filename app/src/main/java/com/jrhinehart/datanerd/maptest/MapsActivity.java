package com.jrhinehart.datanerd.maptest;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.newrelic.agent.android.NewRelic;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private UiSettings mUiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewRelic.withApplicationToken(
                "Use your key here"
        ).start(this.getApplication());
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);

        // Create Event Listeners
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        // Add a marker to a random location on the globe and center the camera
        LatLng randomLatLng = getRandomCoords();
        mMap.addMarker(new MarkerOptions().position(randomLatLng).title("Random Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(randomLatLng));
    }

    public LatLng getRandomCoords() {
        double minLat = -90.00;
        double maxLat = 90.00;
        double latitude = minLat + (Math.random() * ((maxLat - minLat) + 1));
        double minLon = 0.00;
        double maxLon = 180.00;
        double longitude = minLon + (Math.random() * ((maxLon - minLon) + 1));
        LatLng coords = new LatLng(latitude, longitude);

        return coords;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Let's send this marker's info to New Relic as a custom event!
        // First we create a Map to hold our custom attributes
        Map<String, Object> attributes = new HashMap<>();
        // Add our attributes as key:value pairs
        attributes.put("lat",marker.getPosition().latitude);
        attributes.put("lng",marker.getPosition().longitude);
        attributes.put("markerName", marker.getTitle());
        //Then we can pass our custom attributes to the Custom Event, and let the agent handle the rest!
        NewRelic.recordCustomEvent("Mobile_Custom", "GeoLocation", attributes);
        return false;  // Returning false here is just to allow the default method to center the camera
    }

    @Override
    public void onMapClick(LatLng mapPoint) {
        mMap.addMarker(new MarkerOptions().position(mapPoint).title("User-defined Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mapPoint));
    }
}
