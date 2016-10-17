package com.scv.slackgo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scv.slackgo.R;
import com.scv.slackgo.helpers.Preferences;
import com.scv.slackgo.helpers.SlackGoApplication;
import com.scv.slackgo.models.Region;

import java.util.ArrayList;

/**
 * Created by ayelen@scvsoft.com.
 */
public class RegionsActivity extends MapActivity {

    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listView = (ListView) findViewById(R.id.list);

        listView.setAdapter(getAdapter());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO bring region from name clicked.
                SlackGoApplication app = (SlackGoApplication) getApplicationContext();
                Region mockedRegion = Region.getMockRegion();
                app.setRegion(mockedRegion);

                Intent regionDetailsIntent = new Intent(RegionsActivity.this, DetailRegionActivity.class);
                startActivity(regionDetailsIntent);
            }
        });

        getLoaderManager().destroyLoader(0);
    }

    private ArrayAdapter<String> getAdapter() {
        ArrayList<String> regions = setupRegions();
        return new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, regions);
    }

    private ArrayList<String> setupRegions() {
        return Preferences.getRegionsNamesLists(this, getString(R.string.preferences_regions_list));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_regions_list;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        Region officeRegion = Region.getMockRegion();

        LatLng officePosition = new LatLng(officeRegion.getLatitude(), officeRegion.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(officePosition);
        markerOptions.draggable(false);
        markerOptions.title(officeRegion.getName());

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(officePosition, officeRegion.getCameraZoom()));
    }
}
