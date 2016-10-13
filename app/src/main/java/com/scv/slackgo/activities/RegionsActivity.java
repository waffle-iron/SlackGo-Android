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
import com.scv.slackgo.services.SlackApiService;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ayelen@scvsoft.com.
 */
public class RegionsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ArrayAdapter channelsAdapter;
    SlackApiService slackService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regions_list);

        slackService = new SlackApiService(this);

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
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
        Set<String> channels = setupChannels();

        Integer[] imageId = {
                R.drawable.arrow

        };

        channelsAdapter = new CustomList(this, channels.toArray(new String[channels.size()]), imageId);

        getLoaderManager().initLoader(0, null, this);

        TextView titleview = new TextView(this);
        titleview.setText(R.string.regions_title);
        titleview.setTextSize(30f);
        titleview.setTextColor(Color.parseColor("#FFFFFF"));
        titleview.setBackgroundColor(Color.parseColor("#1db08f"));


        getListView().addHeaderView(titleview);

        setListAdapter(channelsAdapter);
    }

    private Set<String> setupChannels() {
        SharedPreferences channels = this.getSharedPreferences(
                getString(R.string.channels_list), Context.MODE_PRIVATE);

        if (channels == null || channels.getAll().size() == 0) {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            Set<String> channelsList = new HashSet<String>();
            channelsList.add(Constants.OFFICE_CHANNEL);


            editor.putStringSet(getString(R.string.channels_list), channelsList);
            editor.putFloat(Constants.OFFICE_CHANNEL + getString(R.string.channel_lat_sufix), Constants.SCV_OFFICE_LAT);
            editor.putFloat(Constants.OFFICE_CHANNEL + getString(R.string.channel_long_sufix), Constants.SCV_OFFICE_LONG);
            editor.commit();

            return channelsList;
        }

        return this.getPreferences(Context.MODE_PRIVATE).getStringSet(getString(R.string.channels_list), new HashSet<String>());
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

        String channelName = l.getItemAtPosition(position).toString();

        Intent channelDetailsIntent = new Intent(this, DetailRegionActivity.class);
        channelDetailsIntent.putExtra(Constants.SELECTED_CHANNEL, channelName);

        startActivity(channelDetailsIntent);
    }
}
