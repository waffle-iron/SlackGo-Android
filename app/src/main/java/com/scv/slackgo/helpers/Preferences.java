package com.scv.slackgo.helpers;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by kado on 10/17/16.
 */

public class Preferences {

    public static ArrayList<String> getRegionsNamesLists(Activity activity, String preferencesKey) {
        Set<String> setOfRegions;
        setOfRegions = activity.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).
                getStringSet(preferencesKey, new HashSet<String>());

        return new ArrayList<String>(Arrays.asList(setOfRegions.toArray(new String[0])));
    }

    public static Boolean areRegionsEmpty(Activity activity, String preferencesKey) {
        Set<String> regions = activity.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getStringSet(preferencesKey, null);
        return (regions == null);
    }

}
