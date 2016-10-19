package com.scv.slackgo.activities;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.internal.util.Predicate;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scv.slackgo.R;
import com.scv.slackgo.models.Region;

import java.util.ArrayList;
import java.util.List;

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

    public void setMarker(Region region) {
        this.googleMap.clear();
        LatLng officePosition = new LatLng(region.getLatitude(), region.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(officePosition);
        markerOptions.title(region.getName());

        circle = this.googleMap.addCircle(new CircleOptions().center(officePosition)
                .radius(region.getRadius())
                .strokeColor(Color.argb(200, 255, 0, 255))
                .fillColor(Color.argb(25, 255, 0, 255)));

        this.googleMap.addMarker(markerOptions);
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(officePosition, region.getCameraZoom()));
    }
}
