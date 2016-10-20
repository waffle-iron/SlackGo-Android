package com.scv.slackgo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.scv.slackgo.R;
import com.scv.slackgo.helpers.Constants;
import com.scv.slackgo.helpers.Preferences;
import com.scv.slackgo.models.Location;
import com.scv.slackgo.services.SlackApiService;

import java.util.ArrayList;


/**
 * Created by ayelen@scvsoft.com.
 */
public class LocationsListActivity extends MapActivity {

    SlackApiService slackService;
    ListView listView;
    ArrayList<Location> locationsList = new ArrayList<Location>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().destroyLoader(0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        locationsList = Preferences.getLocationsList(this);

        setListView();
    }

    private void setListView() {
        listView = (ListView) findViewById(R.id.list);

        listView.setAdapter(getAdapter());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Location locationClicked = locationsList.get(position);
                Gson gson = new Gson();
                String locationJSON = gson.toJson(locationClicked);
                String locationsListJSON = gson.toJson(locationsList);


                Intent locationIntent = new Intent(LocationsListActivity.this, LocationActivity.class);
                locationIntent.putExtra(Constants.INTENT_LOCATION_CLICKED, locationJSON);
                locationIntent.putExtra(Constants.INTENT_LOCATION_LIST, locationsListJSON);

                startActivity(locationIntent);
            }
        });
    }

    private ArrayAdapter<String> getAdapter() {
        ArrayList<String> locations = setupLocations();
        return new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, locations);
    }

    private ArrayList<String> setupLocations() {
        if (!Preferences.isLocationsListEmpty(this)) {
            ArrayList<String> locationNameList = new ArrayList<String>();
            for (Location location : locationsList) {
                locationNameList.add(location.getName());
            }
            return locationNameList;
        } else {
            return new ArrayList<String>();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_locations_list;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setCompassEnabled(true);

        Location officeLocation = Location.getSCVLocation();

        this.setMarker(officeLocation);
    }
}
