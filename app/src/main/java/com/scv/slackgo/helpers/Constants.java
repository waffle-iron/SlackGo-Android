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

package com.scv.slackgo.helpers;

import android.net.Uri;

/** Constants used in companion app. */
public final class Constants {

    private Constants() {
    }

    public static final String PACKAGE_NAME = "com.scv.slackgo";


    // Request code to attempt to resolve Google Play services connection failures.
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // Timeout for making a connection to GoogleApiClient (in milliseconds).
    public static final long CONNECTION_TIME_OUT_MS = 100;


    // The constants below are less interesting than those above.

    // Path for the DataItem containing the last geofence id entered.
    public static final String GEOFENCE_DATA_ITEM_PATH = "/geofenceid";
    public static final Uri GEOFENCE_DATA_ITEM_URI =
            new Uri.Builder().scheme("wear").path(GEOFENCE_DATA_ITEM_PATH).build();
    public static final String KEY_GEOFENCE_ID = "geofence_id";

    // Keys for flattened geofences stored in SharedPreferences.
    public static final String KEY_LATITUDE = "KEY_LATITUDE";
    public static final String KEY_LONGITUDE = "KEY_LONGITUDE";
    public static final String KEY_RADIUS = "KEY_RADIUS";
    public static final String KEY_EXPIRATION_DURATION =
            "KEY_EXPIRATION_DURATION";
    public static final String KEY_TRANSITION_TYPE =
            "KEY_TRANSITION_TYPE";
    // The prefix for flattened geofence keys.
    public static final String KEY_PREFIX = "KEY";

    // Invalid values, used to test geofence storage when retrieving geofences.
    public static final long INVALID_LONG_VALUE = -999l;
    public static final float INVALID_FLOAT_VALUE = -999.0f;
    public static final int INVALID_INT_VALUE = -999;

    public static final float SCV_OFFICE_LAT = -34.6024f;
    public static final float SCV_OFFICE_LONG = -58.4543f;
    public static final String SCV_ID = "1";
    public static final long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    public static final long FASTEST_INTERVAL = 2000; /* 2 sec */
    public static final String OFFICE = "Office";
    public static final int RC_ASK_PERMISSIONS = 123;
    public static final String TAG = "GeoFenceActivity";
    public static final String OFFICE_CHANNEL= "oficina";
    public static final String ASK_LOCATION= "Please active your location";
    public static final float DEFAULT_RADIUS_METERS = 100.0f;
    public static final float DEFAULT_CAMERA_ZOOM = 15.0f;

    public static final String API_TOKEN= "api token";
    public static final String SELECTED_CHANNEL="selected channel";
    public static final String SLACK_TOKEN= "slack.token";

    public static final String SHARED_PREFERENCES_NAME = "slackgo.SHARED_PREFERENCES_NAME";

    public static final String SHARED_PREFERENCES_AVAILABLE_CHANNELS = "slackgo.SHARED_PREFERENCES_AVAILABLE_CHANNELS";


    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    public static final String SHARED_PREFERENCES_LOCATIONS = "LOCATIONS_SHARED";

    public static final String INTENT_LOCATION_CLICKED = "REGION_CLICKED";
    public static final String INTENT_LOCATION_LIST = "REGION_LIST";


}
