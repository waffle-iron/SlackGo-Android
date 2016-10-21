package com.scv.slackgo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.scv.slackgo.R;
import com.scv.slackgo.helpers.Constants;
import com.scv.slackgo.helpers.ErrorUtils;
import com.scv.slackgo.helpers.GsonUtils;
import com.scv.slackgo.helpers.Preferences;
import com.scv.slackgo.models.Location;
import com.scv.slackgo.services.SlackApiService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by kado on 10/11/16.
 */

public class LocationActivity extends MapActivity implements Observer {

    private SeekBar locationSeekBar;
    private TextView locationRadiusValue;
    private EditText locationName;
    private Button saveLocationButton;
    private Button delLocationButton;
    private Location location;
    private ArrayList<Location> locationsList;
    private ArrayList<String> channelsList;
    private String toastMsg;
    SlackApiService slackService;
    PlaceAutocompleteFragment autocompleteFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeVariables();

        setDetailValues();

        addLocationBarListener();

        addCRUDButtonsListener();

        addSearchListener();

        addLocationNameEnterListener();

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

        location = GsonUtils.getObjectFromJson(locationJSON, Location.class);
        locationsList = GsonUtils.getListFromJson(locationsListJSON, Location[].class);

        slackService = new SlackApiService(this);
        //Get resources
        locationSeekBar = (SeekBar) findViewById(R.id.location_radius_seek_bar);
        locationRadiusValue = (TextView) findViewById(R.id.location_radius_value);
        locationName = (EditText) findViewById(R.id.location_name);
        saveLocationButton = (Button) findViewById(R.id.save_location_button);
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
                ErrorUtils.showErrorAlert(LocationActivity.this);
            }
        });
    }


    private void addCRUDButtonsListener() {
        delLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.deleteLocationFromList(LocationActivity.this, location, locationsList);
                goToLocationActivity();
            }
        });

        saveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (location != null) {
                    editLocation();
                } else {
                    addNewLocation();
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
                        locationRadiusValue.setText(progressString);
                        seekBar.setProgress(progress);
                        circle.setRadius(progress * 10);
                    }
                }
            }
        });
    }


    private void addLocationNameEnterListener() {
        locationName.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });
    }

    private void setDetailValues() {
        if (location != null) {
            locationName.setText(location.getName());
            locationSeekBar.setProgress(new BigDecimal(location.getRadius() / 10).intValue());
            locationRadiusValue.setText(String.valueOf(location.getRadius() * 10));
            delLocationButton.setVisibility(View.VISIBLE);
        } else {
            locationSeekBar.setProgress(0);
            locationRadiusValue.setText(String.valueOf(0));
            delLocationButton.setVisibility(View.GONE);
        }
    }

    private void goToLocationActivity() {
        Intent locationsIntent = new Intent(LocationActivity.this, LocationsListActivity.class);
        startActivity(locationsIntent);
        finish();
    }


    private void addNewLocation() {
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
            ErrorUtils.toastError(this, toastMsg, Toast.LENGTH_SHORT);
        }
    }

    private void editLocation() {

        int locationPosition = 0;
        for (Location editLocation : locationsList) {
            if (editLocation.equals(location)) {
                break;
            }
            locationPosition++;
        }

        Location mocked = Location.getSCVLocation();
        ArrayList<String> channels = new ArrayList<String>();
        Location newLocation = new Location(locationName.getText().toString(), mocked.getLatitude(),
                mocked.getLongitude(), mocked.getRadius(),
                mocked.getCameraZoom(), channels);

        if (isValidLocation(newLocation)) {
            locationsList.get(locationPosition).setName(locationName.getText().toString());
            Preferences.removeDataFromSharedPreferences(this, Constants.INTENT_LOCATION_LIST);
            Preferences.addLocationsListToSharedPreferences(this, locationsList);
            goToLocationActivity();
        } else {
            ErrorUtils.toastError(this, toastMsg, Toast.LENGTH_SHORT);
        }
    }


    private boolean isValidLocation(Location location) {
        boolean isValid = true;
        if (location.getName().isEmpty()) {
            isValid = false;
            toastMsg = getString(R.string.empty_location_name);
        } else {
            if (locationsList != null) {
                for (Location locationInList : locationsList) {
                    if (location.getName().equals(locationInList.getName())) {
                        isValid = false;
                        toastMsg = getString(R.string.invalid_location_name);
                    }
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
