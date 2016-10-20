package com.scv.slackgo.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scv.slackgo.R;
import com.scv.slackgo.helpers.Constants;
import com.scv.slackgo.helpers.Preferences;
import com.scv.slackgo.models.Location;
import com.scv.slackgo.services.SlackApiService;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by kado on 10/11/16.
 */

public class LocationActivity extends MapActivity implements Observer {

    private String slackCode;
    private SeekBar locationSeekBar;
    private TextView locationValue;
    private EditText locationName;
    private Button addLocationButton;
    private Button delLocationButton;
    private Location location;
    private ArrayList<Location> locationsList;
    private ArrayList<String> channelsList;
    SlackApiService slackService;
    PlaceAutocompleteFragment autocompleteFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();


        initializeVariables();

        setDetailVallues();

        addLocationBarListener();

        addCRUDButtonsListener();

        addSearchListener();

        String slackCode = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getString(Constants.SLACK_TOKEN, null);

        if (slackCode == null) {
            String code = getIntent().getData().getQueryParameters("code").get(0);
            slackService.getSlackToken(String.format(getString(R.string.slack_token_url),
                    getString(R.string.client_id), getString(R.string.client_secret), code, getString(R.string.redirect_oauth)));

            slackService.getAvailableChannels();
        }
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

        this.googleMap.clear();
        if (location != null) {
            this.setMarker(location);

        } else {
            Location officeLocation = Location.getSCVLocation();
            this.setMarker(officeLocation);
        }
    }

    private void initializeVariables() {
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
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
    }

    private void addSearchListener() {
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                setMarker(new Location(place.getLatLng()));
            }

            @Override
            public void onError(Status status) {
                showErrorAlert();
            }
        });
    }

    private void showErrorAlert() {
        final AlertDialog alertDialog = new AlertDialog.Builder(LocationActivity.this).create();
        alertDialog.setTitle(getText(R.string.error_title));
        alertDialog.setMessage(getText(R.string.error_msg));
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getText(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.hide();
            } });
        alertDialog.show();
    }

    private void addCRUDButtonsListener() {
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
                channels.add(getString(R.string.channel_office));
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
    }

    private void addLocationBarListener() {
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
    }

    private void setDetailVallues() {
        if (location != null) {
            locationName.setText(location.getName());
            locationSeekBar.setProgress(new BigDecimal(location.getRadius() / 10).intValue());
            locationValue.setText(String.valueOf(location.getRadius() * 10));
            delLocationButton.setVisibility(View.VISIBLE);
        } else {
            locationSeekBar.setProgress(0);
            locationValue.setText(String.valueOf(0));
            delLocationButton.setVisibility(View.GONE);
        }
    }

    private void goToLocationActivity() {
        Intent locationsIntent = new Intent(LocationActivity.this, LocationsListActivity.class);
        startActivity(locationsIntent);
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

    @Override
    public void update(Observable observable, Object data) {
        if (data != null) {
            channelsList = (ArrayList<String>) data;
        }
    }
}
