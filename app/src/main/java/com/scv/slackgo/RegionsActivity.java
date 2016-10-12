package com.scv.slackgo;


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

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ayelen@scvsoft.com.
 */
public class RegionsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ArrayAdapter channelsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regions_list);

        Set<String> channels = setupChannels();

        Integer[] imageId = {
                R.drawable.arrow

        };

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        channelsAdapter = new CustomList(this, channels.toArray(new String[channels.size()]), imageId);

        getLoaderManager().initLoader(0, null, this);

        TextView titleview = new TextView(this);
        titleview.setText(R.string.regions_title);
        titleview.setTextSize(30f);
        titleview.setTextColor(Color.parseColor("#FFFFFF"));
        titleview.setBackgroundColor(Color.parseColor("#1db08f"));


        getListView().addHeaderView(titleview);

        setListAdapter(channelsAdapter);

        /*Button addButton = (Button) findViewById(R.id.addItem);
        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                channelsAdapter.notifyDataSetChanged();
            }
        });
        */

        getLoaderManager().destroyLoader(0);
    }

    private Set<String> setupChannels() {
        SharedPreferences channels = this.getSharedPreferences(
                getString(R.string.channels_list), Context.MODE_PRIVATE);

        if(channels == null || channels.getAll().size() == 0) {
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

        String  channelName = l.getItemAtPosition(position).toString();

        Intent channelDetailsIntent = new Intent(this, DetailRegionActivity.class);
        channelDetailsIntent.putExtra(Constants.SELECTED_CHANNEL, channelName);

        String slackCode = getIntent().getStringExtra(Constants.SLACK_CODE);

        if(slackCode != null) {
            channelDetailsIntent.putExtra(Constants.SLACK_CODE, slackCode);
        } else {
            channelDetailsIntent.putExtra(Constants.SLACK_CODE, getIntent().getData().getQueryParameters("code").get(0));
        }

        startActivity(channelDetailsIntent);
    }
}