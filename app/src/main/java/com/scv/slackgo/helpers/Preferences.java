package com.scv.slackgo.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scv.slackgo.models.Location;

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
        editor.commit();
    }

    public static ArrayList<Location> getLocationsList(Context context) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String locationsJSON = sharedPreferences.getString(Constants.SHARED_PREFERENCES_LOCATIONS, "");

        Type listOfTestObject = new TypeToken<List<Location>>() {
        }.getType();
        ArrayList<Location> locations = gson.fromJson(locationsJSON, listOfTestObject);

        return locations;
    }

    public static Boolean isLocationsListEmpty(Activity activity) {
        ArrayList<Location> locations = getLocationsList(activity);
        return ((locations == null) || (locations.size() == 0));
    }

    public static void addLocationsListToSharedPreferences(Activity activity, List<Location> locations) {
        Gson gson = new Gson();
        String locationsJSON = gson.toJson(locations);

        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.SHARED_PREFERENCES_LOCATIONS, locationsJSON);
        editor.commit();
    }

    public static void addLocationToSharedPreferences(Activity activity, Location location) {
        ArrayList<Location> listOfLocations = new ArrayList<Location>();
        if (!isLocationsListEmpty(activity)) {
            listOfLocations.addAll(getLocationsList(activity));
        }
        removeDataFromSharedPreferences(activity, Constants.SHARED_PREFERENCES_LOCATIONS);
        if (!locationWithNameExistsInList(listOfLocations, location.getName())) {
            listOfLocations.add(location);
        }
        addLocationsListToSharedPreferences(activity, listOfLocations);
    }

    public static void deleteLocationFromList(Activity activity, Location location, List<Location> locationsList) {
        locationsList.remove(location);
        removeDataFromSharedPreferences(activity, Constants.SHARED_PREFERENCES_LOCATIONS);
        if (locationsList.size() > 0) {
            addLocationsListToSharedPreferences(activity, locationsList);
        }
    }


    private static boolean locationWithNameExistsInList(ArrayList<Location> locations, String locationName) {
        for (Location location : locations) {
            if (location.getName().equals(locationName)) {
                return true;
            }
        }
        return false;
    }
}
