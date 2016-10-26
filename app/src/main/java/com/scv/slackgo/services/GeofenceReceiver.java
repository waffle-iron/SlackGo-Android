package com.scv.slackgo.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.scv.slackgo.helpers.Preferences;
import com.scv.slackgo.models.Location;

import java.util.ArrayList;


/**
 * Created by kado on 10/24/16.
 */

public class GeofenceReceiver extends BroadcastReceiver {

    protected static final String TAG = "GeofenceReceiver";
    protected GeofenceService geofenceService;
    protected ArrayList<com.scv.slackgo.models.Location> locationsList;

    @Override
    public void onReceive(Context context, Intent intent) {


        Log.v(TAG, "GeofenceReceiver");
        
        locationsList = Preferences.getLocationsList(context);

        ArrayList<Geofence> mGeofenceList = new ArrayList<Geofence>();

        mGeofenceList = getGeofencesList();


        geofenceService = new GeofenceService(context, mGeofenceList);
    }

    //TODO this method is also in LocationActivity REFACTOR
    private ArrayList<Geofence> getGeofencesList() {
        ArrayList<Geofence> geofences = new ArrayList<Geofence>();
        if ((locationsList != null) && (locationsList.size() > 0)) {
            for (Location loc : locationsList) {
                Geofence locationGeofence = new Geofence.Builder()
                        .setRequestId(loc.getName())
                        .setCircularRegion(loc.getLatitude(), loc.getLongitude(), loc.getRadius())
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build();
                geofences.add(locationGeofence);
            }
        } else {
            locationsList.add(Location.getSCVLocation());
        }
        return geofences;
    }

}

