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
import com.scv.slackgo.models.Location;
import com.scv.slackgo.services.SlackApiService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kado on 10/11/16.
 */

public class LocationActivity extends MapActivity {

    private String slackCode;
    private SeekBar locationSeekBar;
    private TextView locationValue;
    private EditText locationName;
    private Button addLocationButton;
    private Button delLocationButton;
    private Location location;
    private ArrayList<Location> locationsList;
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
        String locationJSON = myIntent.getStringExtra(Constants.INTENT_LOCATION_CLICKED);
        String locationsListJSON = myIntent.getStringExtra(Constants.INTENT_LOCATION_LIST);// will return "FirstKeyValue"

        Gson gson = new Gson();
        location = gson.fromJson(locationJSON, Location.class);
        Type typeOfLocations = new TypeToken<List<Location>>() {
        }.getType();
        locationsList = gson.fromJson(locationsListJSON, typeOfLocations);


        locationSeekBar = (SeekBar) findViewById(R.id.location_seek_bar);
        locationValue = (TextView) findViewById(R.id.location_radius_value);
        locationName = (EditText) findViewById(R.id.location_name);
        addLocationButton = (Button) findViewById(R.id.add_location_button);
        delLocationButton = (Button) findViewById(R.id.del_location_button);
        locationSeekBar.setMax(100);

        if (location != null) {
            locationName.setText(location.getName());
            locationSeekBar.setProgress((int) location.getRadius() / 10);
            locationValue.setText(String.valueOf(location.getRadius() * 10));
            delLocationButton.setVisibility(View.VISIBLE);
        } else {
            locationSeekBar.setProgress(0);
            locationValue.setText(String.valueOf(0));
            delLocationButton.setVisibility(View.GONE);
        }

        locationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress >= 0 && progress <= locationSeekBar.getMax()) {
                        String progressString = String.valueOf(progress * 10);
                        locationValue.setText(progressString);
                        seekBar.setProgress(progress);
                        circle.setRadius(progress * 10);
                    }
                }
            }
        });


        delLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.deleteLocationFromListByName(LocationActivity.this, locationName.getText().toString());
                goToLocationActivity();
            }
        });

        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location mocked = Location.getSCVLocation();
                ArrayList<String> channels = new ArrayList<String>();
                channels.add("clubdelacomputadora");
                Location newLocation = new Location(locationName.getText().toString(), mocked.getLatitude(),
                        mocked.getLongitude(), mocked.getRadius(),
                        mocked.getCameraZoom(), channels);

                if (isValidLocation(newLocation)) {
                    Preferences.addLocationToSharedPreferences(LocationActivity.this, newLocation);
                    goToLocationActivity();
                } else {
                    Toast.makeText(LocationActivity.this, "Location Incorrecta",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        String slackCode = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getString(Constants.SLACK_TOKEN, null);

        if (slackCode == null) {

            String code = getIntent().getData().getQueryParameters("code").get(0);
            slackService.getSlackToken(String.format(getString(R.string.slack_token_link),
                    getString(R.string.client_id), getString(R.string.client_secret), code, getString(R.string.redirect_oauth)));


            //Getting mock location;
            //Preferences.addLocationToSharedPreferences(this, Location.getSCVLocation());
        }

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                setMarker(new Location(place.getLatLng()));
            }

            @Override
            public void onError(Status status) {
                Log.i("map", "An error occurred: " + status);
            }
        });

    }

    private void goToLocationActivity() {
        Intent locationsIntent = new Intent(LocationActivity.this, LocationsListActivity.class);
        startActivity(locationsIntent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_location;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        if (location != null) {
            LatLng officePosition = new LatLng(location.getLatitude(), location.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(officePosition);
            markerOptions.draggable(false);
            markerOptions.title(location.getName());

            circle = googleMap.addCircle(new CircleOptions().center(officePosition)
                    .radius(location.getRadius())
                    .strokeColor(Color.argb(200, 255, 0, 255))
                    .fillColor(Color.argb(25, 255, 0, 255)));

            googleMap.addMarker(markerOptions);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(officePosition, location.getCameraZoom()));
        } else {
            Location officeLocation = Location.getSCVLocation();
            LatLng officePosition = new LatLng(officeLocation.getLatitude(), officeLocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(officePosition, officeLocation.getCameraZoom()));
        }
    }

    private boolean isValidLocation(Location location) {
        boolean isValid = true;
        if (locationsList != null) {
            for (Location locationInList : locationsList) {
                if (location.getName().equals(locationInList.getName())) {
                    isValid = false;
                }
            }
        }
        return isValid;
    }
}
