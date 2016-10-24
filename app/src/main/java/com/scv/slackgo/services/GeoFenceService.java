package com.scv.slackgo.services;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.scv.slackgo.helpers.GeofenceErrorMessages;

import java.util.ArrayList;

/**
 * Created by kado on 10/24/16.
 */

public class GeoFenceService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    protected static final String TAG = "GeoFenceService";
    private Context context;
    protected ArrayList<Geofence> mGeofenceList;
    protected GoogleApiClient mGoogleApiClient;


    public GeoFenceService(Context context, ArrayList<Geofence> mGeofenceList) {
        Log.v(TAG, "GeoFenceService");

        this.context = context;
        this.mGeofenceList = mGeofenceList;

        buildGoogleApiClient();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            } else {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeofencingRequest(),
                        getGeofencePendingIntent()
                ).setResultCallback(this); // Result processed in onResult().
            }
        } else {
            Log.v(TAG, "No Permissions");
        }

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(context, GeoFenceTransitionsIntentService.class);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(@NonNull Status status) {
        String msg;
        if (status.isSuccess()) {
            msg = "Result success";
        } else {
            msg = GeofenceErrorMessages.getErrorString(context, status.getStatusCode());
        }

        Log.v(TAG, msg);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient =
                new GoogleApiClient.Builder(context)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
            Log.v(TAG, "GeoFences Added");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(TAG, "Google Maps API Connection Failed: " + connectionResult);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "Google Maps API Connection Suspended");
        mGoogleApiClient.connect();
    }


}
