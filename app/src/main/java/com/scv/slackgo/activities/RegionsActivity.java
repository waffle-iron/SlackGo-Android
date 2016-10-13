package com.scv.slackgo.activities;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scv.slackgo.R;
import com.scv.slackgo.helpers.Constants;
import com.scv.slackgo.helpers.SlackGoApplication;
import com.scv.slackgo.models.Region;
import com.scv.slackgo.services.SlackApiService;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ayelen@scvsoft.com.
 */
public class RegionsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    ArrayAdapter regionsAdapter;
    SlackApiService slackService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regions_list);
        slackService = new SlackApiService(this);

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        setAdapter();

        String slackCode = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getString(Constants.SLACK_TOKEN, null);

        if (slackCode == null) {

            String code = getIntent().getData().getQueryParameters("code").get(0);
            slackService.getSlackToken(String.format(getString(R.string.slack_token_link),
                    getString(R.string.client_id), getString(R.string.client_secret), code, getString(R.string.redirect_oauth)));

        }

        getLoaderManager().destroyLoader(0);
    }

    private void setAdapter() {
        Set<String> regions = setupRegions();

        Integer[] imageId = {R.drawable.arrow

        };

        regionsAdapter = new CustomList(this, regions.toArray(new String[regions.size()]), imageId);


        getLoaderManager().initLoader(0, null, this);

        TextView titleview = new TextView(this);
        titleview.setText(R.string.regions_title);
        titleview.setTextSize(30f);
        titleview.setTextColor(Color.parseColor("#FFFFFF"));
        titleview.setBackgroundColor(Color.parseColor("#1db08f"));

        getListView().addHeaderView(titleview);
        setListAdapter(regionsAdapter);
    }

    private Set<String> setupRegions() {
        SharedPreferences regions = this.getSharedPreferences(getString(R.string.preferences_regions_list), Context.MODE_PRIVATE);

        //TODO this has to be changed later. Now is just to have a region for the list.
        if (regions == null || regions.getAll().size() == 0) {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            //Saving in context the mock region
            Region mockedRegion = Region.getMockRegion();
            SlackGoApplication app = (SlackGoApplication) getApplicationContext();
            app.setRegion(mockedRegion);

            Set<String> preferencesRegionsList = new HashSet<String>();
            preferencesRegionsList.add(mockedRegion.getName());
            editor.putStringSet(getString(R.string.preferences_regions_list), preferencesRegionsList);
            editor.putFloat(mockedRegion.getName() + getString(R.string.region_lat_sufix), mockedRegion.getLatitude());
            editor.putFloat(mockedRegion.getName() + getString(R.string.region_long_sufix), mockedRegion.getLongitude());
            editor.commit();

            return preferencesRegionsList;
        }
        return this.getPreferences(Context.MODE_PRIVATE).getStringSet(getString(R.string.preferences_regions_list), new HashSet<String>());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        String regionName = l.getItemAtPosition(position).toString();

        Intent regionDetailsIntent = new Intent(this, DetailRegionActivity.class);
        regionDetailsIntent.putExtra(Constants.SELECTED_CHANNEL, regionName);

        startActivity(regionDetailsIntent);
    }
}
