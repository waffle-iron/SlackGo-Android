package com.scv.slackgo.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scv.slackgo.R;
import com.scv.slackgo.helpers.Constants;
import com.scv.slackgo.helpers.SlackGoApplication;
import com.scv.slackgo.models.Region;

/**
 * Created by kado on 10/11/16.
 */

public class DetailRegionActivity extends MapActivity {

    private String slackCode;
    private SeekBar regionSeekBar;
    private TextView regionValue;
    private EditText regionName;
    private Circle circle;
    private GoogleMap googleMap;
    private Region region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        slackCode = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getString(Constants.SLACK_TOKEN, null);
        SlackGoApplication app = (SlackGoApplication) getApplicationContext();
        region = app.getRegion();

        regionSeekBar = (SeekBar) findViewById(R.id.region_seek_bar);
        regionValue = (TextView) findViewById(R.id.region_radius_value);
        regionName = (EditText) findViewById(R.id.region_name);
        regionSeekBar.setMax(100);

        if (region != null) {
            regionName.setText(region.getName());
            regionSeekBar.setProgress((int) region.getRadius() / 10);
            regionValue.setText(String.valueOf(region.getRadius() * 10));
        } else {
            regionSeekBar.setProgress(10);
            regionValue.setText(10 * 10);
        }

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
                        seekBar.setProgress(progress);
                        circle.setRadius(progress * 10);
                    }
                }

            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_detail_region;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        LatLng officePosition = new LatLng(region.getLatitude(), region.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(officePosition);
        markerOptions.draggable(false);
        markerOptions.title(region.getName());

        circle = googleMap.addCircle(new CircleOptions().center(officePosition)
                .radius(region.getRadius())
                .strokeColor(Color.argb(200, 255, 0, 255))
                .fillColor(Color.argb(25, 255, 0, 255)));

        googleMap.addMarker(markerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(officePosition, region.getCameraZoom()));
    }
}
