package com.scv.slackgo.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scv.slackgo.models.Location;

import static com.scv.slackgo.R.id.channel_map;

/**
 * Created by kado on 10/14/16.
 */

public abstract class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap googleMap;
    Circle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(channel_map, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);

    }

    public abstract int getLayoutId();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void setMarker(Location location) {
        LatLng officePosition = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(officePosition);
        markerOptions.title(location.getName());

        circle = this.googleMap.addCircle(new CircleOptions().center(officePosition)
                .radius(location.getRadius())
                .strokeColor(Color.argb(200, 255, 0, 255))
                .fillColor(Color.argb(25, 255, 0, 255)));

        this.googleMap.addMarker(markerOptions);
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(officePosition, location.getCameraZoom()));
    }
}
