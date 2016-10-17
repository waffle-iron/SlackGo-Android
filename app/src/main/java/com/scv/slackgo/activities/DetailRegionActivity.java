package com.scv.slackgo.activities;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.scv.slackgo.services.SlackApiService;

import java.util.HashSet;

/**
 * Created by kado on 10/11/16.
 */

public class DetailRegionActivity extends MapActivity {

    private String slackCode;
    private SeekBar regionSeekBar;
    private TextView regionValue;
    private EditText regionName;
    private Circle circle;
    private Region region;
    SlackApiService slackService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slackService = new SlackApiService(this);

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
            regionSeekBar.setProgress(0);
            regionValue.setText(String.valueOf(0));
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

        String slackCode = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getString(Constants.SLACK_TOKEN, null);

        if (slackCode == null) {

            String code = getIntent().getData().getQueryParameters("code").get(0);
            slackService.getSlackToken(String.format(getString(R.string.slack_token_link),
                    getString(R.string.client_id), getString(R.string.client_secret), code, getString(R.string.redirect_oauth)));


            //Getting mock region
            Region mockedRegion = Region.getMockRegion();

            //TODO when save button added, replace this for the real region
            HashSet<String> regionsHashSet = new HashSet<String>();
            regionsHashSet.add(mockedRegion.getName());


            //TODO when saving the real region remove clear.
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.putStringSet(getString(R.string.preferences_regions_list), regionsHashSet);
            editor.commit();
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_detail_region;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        if (region != null) {
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
        } else {
            Region officeRegion = Region.getMockRegion();
            LatLng officePosition = new LatLng(officeRegion.getLatitude(), officeRegion.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(officePosition, officeRegion.getCameraZoom()));
        }
    }
}
