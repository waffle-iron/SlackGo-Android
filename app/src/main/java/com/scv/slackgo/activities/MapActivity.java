package com.scv.slackgo.activities;


import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scv.slackgo.R;
import com.scv.slackgo.helpers.Constants;
import com.scv.slackgo.helpers.GeofenceErrorMessages;
import com.scv.slackgo.models.Region;
import com.scv.slackgo.services.GeofenceTransitionsIntentService;

import java.util.ArrayList;
import java.util.List;

import static com.scv.slackgo.R.id.channel_map;

/**
 * Created by ayelen@scvsoft.com .
 */

public class MapActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    protected static final String TAG = "MapActivity";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    //private String apiToken;
    private String slackCode;

    //private PendingIntent mGeofenceRequestIntent;
    private PendingIntent mGeofencePendingIntent;
    private SharedPreferences mSharedPreferences;
    private GoogleMap map;

    List<Geofence> mGeofenceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this,
                                              new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                                              Constants.RC_ASK_PERMISSIONS);
        }


        mGeofenceList = new ArrayList<Geofence>();
        mGeofencePendingIntent = null;
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        slackCode = mSharedPreferences.getString(Constants.SLACK_TOKEN, null);

        populateGeofenceList();
        buildGoogleApiClient();

        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(channel_map, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);

        Button back = (Button)findViewById(R.id.back_button);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient =
                new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(), getGeofencePendingIntent()).setResultCallback(this);
        }
        Log.i(TAG, "\n\n\nConnected to GoogleApiClient\n\n\n");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this.getBaseContext(), "Google Maps API Connection Failed: " + connectionResult, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "\n\n\n\n Connection Suspended\n\n\n\n");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Region mockRegion = Region.getMockRegion();

        LatLng officePosition = new LatLng(mockRegion.getLatitude(), mockRegion.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(officePosition);
        markerOptions.draggable(false);
        markerOptions.title(mockRegion.getName());

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(officePosition);
        circleOptions.radius(mockRegion.getRadius());
        circleOptions.strokeColor(Color.argb(200, 255, 0, 255));
        circleOptions.fillColor(Color.argb(25, 255, 0, 255));

        googleMap.addMarker(markerOptions);
        googleMap.addCircle(circleOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(officePosition, mockRegion.getCameraZoom()));
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            googleMap.setMyLocationEnabled(true);
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }


    public void addGeofencesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }


    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                   "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            Toast.makeText(this, "Result success", Toast.LENGTH_SHORT).show();
            /*
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.apply();

            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
            setButtonsEnabledState();

            Toast.makeText(
                    this,
                    getString(mGeofencesAdded ? R.string.geofences_added :
                              R.string.geofences_removed),
                    Toast.LENGTH_SHORT
            ).show();
            */
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                                                                       status.getStatusCode());
            Log.e(TAG, errorMessage);
        }


    }

    private PendingIntent getGeofencePendingIntent() {

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void populateGeofenceList() {
        mGeofenceList.add(new Geofence.Builder()
                                  // Set the request ID of the geofence. This is a string to identify this
                                  // geofence.
                                  .setRequestId(Constants.SCV_ID)

                                  // Set the circular region of this geofence.
                                  .setCircularRegion(Constants.SCV_OFFICE_LAT, Constants.SCV_OFFICE_LONG, Constants.RADIUS_METERS)

                                  // Set the expiration duration of the geofence. This geofence gets automatically
                                  // removed after this period of time.
                                  .setExpirationDuration(Geofence.NEVER_EXPIRE)

                                  // Set the transition types of interest. Alerts are only generated for these
                                  // transition. We track entry and exit transitions in this sample.
                                  .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)

                                  // Create the geofence.
                                  .build());
    }
    public void backToList(View view) {
        Intent listActivity = new Intent(this, RegionsActivity.class);
        listActivity.putExtra(Constants.SLACK_TOKEN, slackCode);
        startActivity(listActivity);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (String permisson: permissions) {
            switch (permisson) {
                case Manifest.permission.ACCESS_FINE_LOCATION: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(), getGeofencePendingIntent()).setResultCallback(this);
                    }
                    break;
                }
                case Manifest.permission.ACCESS_COARSE_LOCATION: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        map.setMyLocationEnabled(true);
                    }
                    break;
                }

            }
        }

    }
}
