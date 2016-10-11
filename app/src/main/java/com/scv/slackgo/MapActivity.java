package com.scv.slackgo;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.wearable.Channel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayelen@scvsoft.com .
 */

public class MapActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String apiToken;
    private String slackCode;
    RequestQueue queue;

    private PendingIntent mGeofenceRequestIntent;
    List<Geofence> mGeofenceList;
    SimpleGeofenceStore mGeofenceStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

        if(getIntent().getData() != null) {
            slackCode = getIntent().getData().getQueryParameters("code").get(0);
        } else {
            slackCode = getIntent().getStringExtra(Constants.SLACK_CODE);
        }

        queue = Volley.newRequestQueue(this);

        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.office_map, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);

        // Instantiate a new geofence storage area.
        mGeofenceStorage = new SimpleGeofenceStore(this);
        // Instantiate the current List of geofences.
        mGeofenceList = new ArrayList<Geofence>();

        Button back = (Button)findViewById(R.id.back_button);

        createGeofences();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng officePosition = new LatLng(Constants.SCV_OFFICE_LAT, Constants.SCV_OFFICE_LONG);

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(officePosition);
        markerOptions.draggable(false);
        markerOptions.title(Constants.OFFICE);

        CircleOptions circleOptions = new CircleOptions();

        circleOptions.center(officePosition);
        circleOptions.radius(100);
        circleOptions.strokeColor(Color.argb(200, 255, 0, 255));
        circleOptions.fillColor(Color.argb(25, 255, 0, 255));

        googleMap.addMarker(markerOptions);
        googleMap.addCircle(circleOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(officePosition, 10.0f));

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    // Trigger new location updates at interval
 /*   protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.RC_ASK_PERMISSIONS);
        }

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(Constants.UPDATE_INTERVAL)
                .setFastestInterval(Constants.FASTEST_INTERVAL);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            if (mLastLocation.getLatitude() == Constants.SCV_OFFICE_LAT && mLastLocation.getLongitude() == Constants.SCV_OFFICE_LONG) {
                //TODO In the office, join the user to the channel
                TextView status = (TextView) findViewById(R.id.status);
                status.setText(R.string.in_the_office);

             //   joinToChannel(getString(R.string.channel_office));
            }
        }
    }*/

    /*private void joinToChannel(String channel) {
        String url = String.format("https://slack.com/api/channels.join?token=%1$s&name=%2$s", apiToken, channel);

        StringRequest channelsReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        JSONObject responseToJson = null;
                        try {
                            responseToJson = new JSONObject(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HttpClient", "error: " + error.toString());
                    }
                });
        queue.add(channelsReq);
    }
    */

    private void setAccessToken() {

        final String tokenLink = String.format(getString(R.string.slack_token_link), getString(R.string.client_id),
                getString(R.string.client_secret), slackCode, getString(R.string.redirect_oauth));
        StringRequest channelsReq = new StringRequest(Request.Method.GET, tokenLink,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            apiToken = (new JSONObject(response)).getString("access_token");
                            setupGeofence();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HttpClient", "error: " + error.toString());
                    }
                });
        queue.add(channelsReq);
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(Constants.UPDATE_INTERVAL)
                .setFastestInterval(Constants.FASTEST_INTERVAL);
        setAccessToken();
    }

    private void setupGeofence() {
        if(apiToken != null) {
            mGeofenceRequestIntent = getGeofenceTransitionPendingIntent(apiToken);
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mGeofenceList,
                    mGeofenceRequestIntent);

//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
//                    mLocationRequest, mGeofenceRequestIntent);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (null != mGeofenceRequestIntent) {
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, mGeofenceRequestIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (String.valueOf(requestCode) == android.Manifest.permission.ACCESS_FINE_LOCATION &&
                grantResults[0] != PackageManager.PERMISSION_GRANTED) {
          //  startLocationUpdates();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this.getBaseContext(),
                "Google Maps API Connection Failed: " + connectionResult, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLocationChanged(Location location) {

    }


    public PendingIntent getGeofenceTransitionPendingIntent(String apiToken) {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        intent.putExtra(Constants.API_TOKEN, apiToken);
       // startService(intent);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void createGeofences() {
        // Create internal "flattened" objects containing the geofence data.
        SimpleGeofence mAndroidBuildingGeofence = new SimpleGeofence(
                Constants.SCV_ID,
                Constants.SCV_OFFICE_LAT,
                Constants.SCV_OFFICE_LAT,
                Constants.RADIUS_METERS,
                Geofence.NEVER_EXPIRE,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT
        );

        // Store these flat versions in SharedPreferences and add them to the geofence list.
        mGeofenceStorage.setGeofence(Constants.SCV_ID, mAndroidBuildingGeofence);
        mGeofenceList.add(mAndroidBuildingGeofence.toGeofence());
    }

    public void backToList(View view) {
        Intent listActivity = new Intent(this, ChannelsActivity.class);
        listActivity.putExtra(Constants.SLACK_CODE, slackCode);
        startActivity(listActivity);

    }
}

