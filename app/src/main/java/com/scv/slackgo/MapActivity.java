package com.scv.slackgo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ayelen@scvsoft.com .
 */
public class MapActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    final private double OFFICE_LAT = -34.6024;
    final private double OFFICE_LONG = -58.4543;
    private static final String OFFICE = "Office";
    final private int RC_ASK_PERMISSIONS = 123;
    private static final String TAG = "MapActivity";
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String apiToken;
    private String slackCode;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        slackCode = getIntent().getData().getQueryParameters("code").get(0);

        queue = Volley.newRequestQueue(this);

        setAccessToken();

        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.office_map, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng officePosition = new LatLng(OFFICE_LAT, OFFICE_LONG);

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(officePosition);
        markerOptions.draggable(false);
        markerOptions.title(OFFICE);

        CircleOptions circleOptions = new CircleOptions();

        circleOptions.center(officePosition);
        circleOptions.radius(200);
        circleOptions.strokeColor(Color.argb(200, 255, 0, 255));
        circleOptions.fillColor(Color.argb(25, 255, 0, 255));

        googleMap.addMarker(markerOptions);
        googleMap.addCircle(circleOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(officePosition, 15.0f));

    }

    @Override
    public void onLocationChanged(Location location) {
        startLocationUpdates();
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
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, RC_ASK_PERMISSIONS);
        }

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            if (mLastLocation.getLatitude() == OFFICE_LAT && mLastLocation.getLongitude() == OFFICE_LONG) {
                //TODO In the office, join the user to the channel
                TextView status = (TextView) findViewById(R.id.status);
                status.setText(R.string.in_the_office);

                joinToChannel(getString(R.string.channel_office));
            }
        } else {
            new AlertDialog.Builder(MapActivity.this)
                    .setTitle(getText(R.string.location_disabled_title))
                    .setMessage(getText(R.string.location_disabled_message))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .show();
        }
    }

    private void joinToChannel(String channel) {
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

    private void setAccessToken(){

        final String tokenLink = String.format(getString(R.string.slack_token_link), getString(R.string.client_id),
                getString(R.string.client_secret), slackCode, getString(R.string.redirect_oauth));
        StringRequest channelsReq = new StringRequest(Request.Method.GET, tokenLink,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            apiToken =(new JSONObject(response)).getString("access_token");

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
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //TODO IMPLEMENT ME
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (String.valueOf(requestCode) == android.Manifest.permission.ACCESS_FINE_LOCATION &&
                grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this.getBaseContext(),
                "Google Maps API Connection Failed: " + connectionResult, Toast.LENGTH_SHORT).show();
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000);
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return mLocationRequest;
    }


    /**
    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            // Get the result of the operation from the AccountManagerFuture.
            Bundle bundle = null;
            try {
                bundle = result.getResult();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AuthenticatorException e) {
                e.printStackTrace();
            }

            // The token is a named value in the bundle. The name of the value
            // is stored in the constant AccountManager.KEY_AUTHTOKEN.
            String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);

        }
    }
    **/

}
