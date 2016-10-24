package com.scv.slackgo.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.scv.slackgo.helpers.Constants;

import java.util.ArrayList;


/**
 * Created by kado on 10/24/16.
 */

public class GeoFenceReceiver extends BroadcastReceiver {

    protected static final String TAG = "GeoFenceReceiver";
    protected GeoFenceService geofenceService;

    @Override
    public void onReceive(Context context, Intent intent) {


        Log.v(TAG, "GeoFenceReceiver");

        //TODO bring geofences from preferences
        ArrayList<Geofence> mGeofenceList = new ArrayList<Geofence>();
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(Constants.SCV_ID)
                .setCircularRegion(Constants.SCV_OFFICE_LAT, Constants.SCV_OFFICE_LONG, Constants.RADIUS_METERS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        geofenceService = new GeoFenceService(context, mGeofenceList);
    }


}

