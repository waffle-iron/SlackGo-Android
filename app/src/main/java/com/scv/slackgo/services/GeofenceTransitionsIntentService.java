/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scv.slackgo.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.scv.slackgo.R;
import com.scv.slackgo.activities.LocationActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Listens for geofence transition changes.
 */
public class GeoFenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "GeofenceTransitionsIS";

    public GeoFenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            Log.e(TAG, "GeofencingEvent Error: " + event.getErrorCode());
            String description = getGeofenceTransitionDetails(event);
            sendNotification(description);
            return;
        }
        int transitionType = event.getGeofenceTransition();
        String msg = "lala";
        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
            msg = "entro";
        }
        if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
            msg = "salio";
        }

        sendNotification(msg);

    }

    private static String getGeofenceTransitionDetails(GeofencingEvent event) {
        String transitionString =
                GeofenceStatusCodes.getStatusCodeString(event.getGeofenceTransition());
        List triggeringIDs = new ArrayList();
        for (Geofence geofence : event.getTriggeringGeofences()) {
            triggeringIDs.add(geofence.getRequestId());
        }
        return String.format("%s: %s", transitionString, TextUtils.join(", ", triggeringIDs));
    }

    private void sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts MainActivity.
        Intent notificationIntent = new Intent(getApplicationContext(), LocationActivity.class);

        // Get a PendingIntent containing the entire back stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(LocationActivity.class).addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setColor(Color.RED)
                .setSmallIcon(R.drawable.common_ic_googleplayservices)
                .setContentTitle(notificationDetails)
                .setContentText("Click notification to return to App")
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);

        // Fire and notify the built Notification.
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

}
