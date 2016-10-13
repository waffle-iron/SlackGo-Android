package com.scv.slackgo.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scv.slackgo.helpers.Constants;
import com.scv.slackgo.R;

import static com.scv.slackgo.R.id.channel_map;

/**
 * Created by kado on 10/11/16.
 */

public class DetailRegionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String slackCode;
    private SeekBar regionSeekBar;
    private TextView regionValue;
    private Circle circle;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_region);

        if (getIntent().getData() != null) {
            slackCode = getIntent().getData().getQueryParameters("code").get(0);
        }
        else {
            slackCode = getIntent().getStringExtra(Constants.SLACK_CODE);
        }

        //queue = Volley.newRequestQueue(this);
        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(channel_map, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);

        regionSeekBar = (SeekBar)findViewById(R.id.region_seek_bar);
        regionValue = (TextView)findViewById(R.id.region_value);

        regionSeekBar.setMax(100);
        regionValue.setText(String.valueOf(10 * 10));

        //regionSeekBar.setSecondaryProgress(10);

        regionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress >= 0 && progress <= regionSeekBar.getMax()) {

                        String progressString = String.valueOf(progress * 10);
                        regionValue.setText(progressString);
                        seekBar.setSecondaryProgress(progress);
                        circle.setRadius(progress * 10);
                    }
                }

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);


        LatLng officePosition = new LatLng(Constants.SCV_OFFICE_LAT, Constants.SCV_OFFICE_LONG);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(officePosition);
        markerOptions.draggable(false);
        markerOptions.title(Constants.OFFICE);

        circle = googleMap.addCircle(new CircleOptions().center(officePosition)
                                                        .radius(100).strokeColor(Color.argb(200, 255, 0, 255))
                                                        .fillColor(Color.argb(25, 255, 0, 255)));

        googleMap.addMarker(markerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(officePosition, 15.0f));
    }
}
