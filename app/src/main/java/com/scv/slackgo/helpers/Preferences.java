package com.scv.slackgo.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scv.slackgo.models.Region;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kado on 10/17/16.
 */

public class Preferences {

    public static void removeDataFromSharedPreferences(Activity activity, String keyToRemove) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(keyToRemove);
        editor.apply();
    }

    public static ArrayList<Region> getRegionsList(Activity activity) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String regionsJSON = sharedPreferences.getString(Constants.SHARED_PREFERENCES_REGIONS, "");

        Type listOfTestObject = new TypeToken<List<Region>>() {
        }.getType();
        ArrayList<Region> regions = gson.fromJson(regionsJSON, listOfTestObject);

        return regions;
    }

    public static Boolean areRegionsEmpty(Activity activity) {
        ArrayList<Region> regions = getRegionsList(activity);
        return ((regions == null) || (regions.size() == 0));
    }

    private static void addRegionsListToSharedPreferences(Activity activity, ArrayList<Region> regions) {
        Gson gson = new Gson();
        String regionsJSON = gson.toJson(regions);

        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString(Constants.SHARED_PREFERENCES_REGIONS, regionsJSON);
        editor.commit();
    }

    public static void addRegionToSharedPreferences(Activity activity, Region region) {
        ArrayList<Region> listOfRegions = new ArrayList<Region>();
        if (!areRegionsEmpty(activity)) {
            listOfRegions.addAll(getRegionsList(activity));
        }
        removeDataFromSharedPreferences(activity, Constants.SHARED_PREFERENCES_REGIONS);
        if (!listOfRegions.contains(region)) {
            listOfRegions.add(region);
        }
        addRegionsListToSharedPreferences(activity, listOfRegions);
    }

    public static void deleteRegionFromListByName(Activity activity, String regionName) {
        ArrayList<Region> listOfRegions = getRegionsList(activity);

        for (Region region : listOfRegions) {
            if (region.getName().equals(regionName)) {
                listOfRegions.remove(region);
            }
        }

        removeDataFromSharedPreferences(activity, Constants.SHARED_PREFERENCES_REGIONS);
        if (listOfRegions.size() > 0) {
            addRegionsListToSharedPreferences(activity, listOfRegions);
        }
    }

}
