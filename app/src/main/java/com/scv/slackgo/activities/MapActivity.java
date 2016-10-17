package com.scv.slackgo.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import static com.scv.slackgo.R.id.channel_map;

/**
 * Created by kado on 10/14/16.
 */

public abstract class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

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
    }
}
