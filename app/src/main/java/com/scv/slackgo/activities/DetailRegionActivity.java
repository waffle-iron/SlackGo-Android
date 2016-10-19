package com.scv.slackgo.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scv.slackgo.R;
import com.scv.slackgo.helpers.Constants;
import com.scv.slackgo.helpers.Preferences;
import com.scv.slackgo.models.Region;
import com.scv.slackgo.services.SlackApiService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kado on 10/11/16.
 */

public class DetailRegionActivity extends MapActivity {

    private String slackCode;
    private SeekBar regionSeekBar;
    private TextView regionValue;
    private EditText regionName;
    private Button addRegionButton;
    private Button delRegionButton;
    private Region region;
    private ArrayList<Region> regionsList;
    SlackApiService slackService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        slackService = new SlackApiService(this);

        Intent myIntent = getIntent();
        String regionJSON = myIntent.getStringExtra(Constants.INTENT_REGION_CLICKED);
        String regionsListJSON = myIntent.getStringExtra(Constants.INTENT_REGIONS_LIST);// will return "FirstKeyValue"

        Gson gson = new Gson();

        region = gson.fromJson(regionJSON, Region.class);

        Type listOfRegionsObject = new TypeToken<List<Region>>() {
        }.getType();
        regionsList = gson.fromJson(regionsListJSON, listOfRegionsObject);

        regionSeekBar = (SeekBar) findViewById(R.id.region_seek_bar);
        regionValue = (TextView) findViewById(R.id.region_radius_value);
        regionName = (EditText) findViewById(R.id.region_name);
        addRegionButton = (Button) findViewById(R.id.add_region_button);
        delRegionButton = (Button) findViewById(R.id.del_region_button);
        regionSeekBar.setMax(100);

        if (region != null) {
            regionName.setText(region.getName());
            regionSeekBar.setProgress((int) region.getRadius() / 10);
            regionValue.setText(String.valueOf(region.getRadius() * 10));
            delRegionButton.setVisibility(View.VISIBLE);
        } else {
            regionSeekBar.setProgress(0);
            regionValue.setText(String.valueOf(0));
            delRegionButton.setVisibility(View.GONE);
        }

        regionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

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


        delRegionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.deleteRegionFromListByName(DetailRegionActivity.this, regionName.getText().toString());
                goToRegionsActivity();
            }
        });

        addRegionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Region mocked = Region.getMockRegion();
                ArrayList<String> channels = new ArrayList<String>();
                channels.add("clubdelacomputadora");
                Region newRegion = new Region(regionName.getText().toString(), mocked.getLatitude(),
                        mocked.getLongitude(), mocked.getRadius(),
                        mocked.getCameraZoom(), channels);

                if (isValidRegion(newRegion)) {
                    Preferences.addRegionToSharedPreferences(DetailRegionActivity.this, newRegion);
                    goToRegionsActivity();
                } else {
                    Toast.makeText(DetailRegionActivity.this, "Region Incorrecta",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        String slackCode = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getString(Constants.SLACK_TOKEN, null);

        if (slackCode == null) {

            String code = getIntent().getData().getQueryParameters("code").get(0);
            slackService.getSlackToken(String.format(getString(R.string.slack_token_link),
                    getString(R.string.client_id), getString(R.string.client_secret), code, getString(R.string.redirect_oauth)));


            //Getting mock region;
            //Preferences.addRegionToSharedPreferences(this, Region.getMockRegion());
        }

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                setMarker(new Region(place.getLatLng()));
            }

            @Override
            public void onError(Status status) {
                Log.i("map", "An error occurred: " + status);
            }
        });

    }

    private void goToRegionsActivity() {
        Intent regionsIntent = new Intent(DetailRegionActivity.this, RegionsActivity.class);
        startActivity(regionsIntent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_detail_region;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
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

    private boolean isValidRegion(Region region) {

        if (!Preferences.areRegionsEmpty(this)) {
            //CASE first time logging in, intent has empty regionList
            if (regionsList == null) {
                return true;
            }
            else {
                for (Region regionInList : regionsList) {
                    if (region.getName().equals(regionInList.getName())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
