package com.scv.slackgo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.scv.slackgo.R;
import com.scv.slackgo.helpers.Constants;
import com.scv.slackgo.helpers.Preferences;
import com.scv.slackgo.models.Region;
import com.scv.slackgo.services.SlackApiService;

import java.util.ArrayList;


/**
 * Created by ayelen@scvsoft.com.
 */
public class RegionsActivity extends MapActivity {

    SlackApiService slackService;
    ListView listView;
    ArrayList<Region> regionsList = new ArrayList<Region>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().destroyLoader(0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        regionsList = Preferences.getRegionsList(this);

        setListView();
    }

    private void setListView() {
        listView = (ListView) findViewById(R.id.list);

        listView.setAdapter(getAdapter());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Region regionClicked = regionsList.get(position);
                Gson gson = new Gson();
                String regionJson = gson.toJson(regionClicked);
                String regionsListJson = gson.toJson(regionsList);


                Intent regionDetailsIntent = new Intent(RegionsActivity.this, DetailRegionActivity.class);
                regionDetailsIntent.putExtra(Constants.INTENT_REGION_CLICKED, regionJson);
                regionDetailsIntent.putExtra(Constants.INTENT_REGIONS_LIST, regionsListJson);

                startActivity(regionDetailsIntent);
            }
        });
    }

    private ArrayAdapter<String> getAdapter() {
        ArrayList<String> regions = setupRegions();
        return new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, regions);
    }

    private ArrayList<String> setupRegions() {
        if (!Preferences.areRegionsEmpty(this)) {
            ArrayList<String> regionNameList = new ArrayList<String>();
            for (Region region : regionsList) {
                regionNameList.add(region.getName());
            }
            return regionNameList;
        } else {
            return new ArrayList<String>();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_regions_list;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setCompassEnabled(true);

        Region officeRegion = Region.getMockRegion();

        this.setMarker(officeRegion);
    }
}
