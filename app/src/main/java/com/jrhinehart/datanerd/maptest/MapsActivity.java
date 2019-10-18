package com.jrhinehart.datanerd.maptest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

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

        // Check for access to device location and request if not enabled
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        } else {
            // Show rationale and request permission.
            Toast.makeText(this,"Y U NO GRANT PERMISSIONS??",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

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

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("lat",location.getLatitude());
        attributes.put("lng",location.getLongitude());
        attributes.put("markerName", "Current Location");
        NewRelic.recordCustomEvent("Mobile_Custom", "GeoLocation", attributes);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
}
