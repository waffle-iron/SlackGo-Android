package com.scv.slackgo.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scv.slackgo.R;
import com.scv.slackgo.helpers.Constants;
import com.scv.slackgo.helpers.SlackGoApplication;
import com.scv.slackgo.models.Region;
import com.scv.slackgo.services.SlackApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ayelen@scvsoft.com.
 */
public class RegionsActivity extends MapActivity {


    ArrayAdapter regionsAdapter;
    SlackApiService slackService;
    private Circle circle;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        slackService = new SlackApiService(this);


        listView = (ListView) findViewById(R.id.list);

        listView.setAdapter(getAdapter());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String regionName = (String) listView.getItemAtPosition(position);
                Intent regionDetailsIntent = new Intent(RegionsActivity.this, DetailRegionActivity.class);
                regionDetailsIntent.putExtra(Constants.SELECTED_CHANNEL, regionName);
                startActivity(regionDetailsIntent);

            }
        });


        String slackCode = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getString(Constants.SLACK_TOKEN, null);

        if (slackCode == null) {

            String code = getIntent().getData().getQueryParameters("code").get(0);
            slackService.getSlackToken(String.format(getString(R.string.slack_token_link),
                    getString(R.string.client_id), getString(R.string.client_secret), code, getString(R.string.redirect_oauth)));

        }

        getLoaderManager().destroyLoader(0);
    }

    private ArrayAdapter<String> getAdapter() {
        ArrayList<String> regions = setupRegions();
        return new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, regions);
    }

    private ArrayList<String> setupRegions() {
        SharedPreferences regions = this.getSharedPreferences(getString(R.string.preferences_regions_list), Context.MODE_PRIVATE);

        //TODO this has to be changed later. Now is just to have a region for the list.
        if (regions == null || regions.getAll().size() == 0) {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            //Saving in context the mock region
            Region mockedRegion = Region.getMockRegion();
            SlackGoApplication app = (SlackGoApplication) getApplicationContext();
            app.setRegion(mockedRegion);

            ArrayList<String> preferencesRegionsList = new ArrayList<String>();
            preferencesRegionsList.add(mockedRegion.getName());
            return preferencesRegionsList;
        }

        Set<String> setOfRegions = this.getPreferences(Context.MODE_PRIVATE).getStringSet(getString(R.string.preferences_regions_list), new HashSet<String>());
        return (ArrayList<String>) Arrays.asList(setOfRegions.toArray(new String[0]));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_regions_list;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        Region region = Region.getMockRegion();

        LatLng officePosition = new LatLng(region.getLatitude(), region.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(officePosition);
        markerOptions.draggable(false);
        markerOptions.title(region.getName());

        circle = googleMap.addCircle(new CircleOptions().center(officePosition)
                .radius(region.getRadius())
                .strokeColor(Color.argb(200, 255, 0, 255))
                .fillColor(Color.argb(25, 255, 0, 255)));

        googleMap.addMarker(markerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(officePosition, region.getCameraZoom()));
    }
}
