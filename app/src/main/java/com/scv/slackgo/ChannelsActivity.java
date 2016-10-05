package com.scv.slackgo;


import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ayelen@scvsoft.com.
 */
public class ChannelsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter channelsAdapter;

    // These are the Contacts rows that we will retrieve
    static final String[] PROJECTION = new String[] {"Channel"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);

        try {

            new FileOutputStream(getString(R.string.channels_file), true).close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream inputreader = openFileInput(getString(R.string.channels_file));

        }catch(FileNotFoundException e) {

        }

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);


        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        String[] fromColumns = {"Office"};

        int[] toViews = {android.R.id.text1};

        channelsAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null , fromColumns, toViews, 0);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);

        TextView titleview = new TextView(this);
        titleview.setText(R.string.channels_title);

        getListView().addHeaderView(titleview);

        setListAdapter(channelsAdapter);

        Button addButton = (Button) findViewById(R.id.addItem);
        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //v.findViewById()fromColumns.add("lalala");
                channelsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        channelsAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        channelsAdapter.swapCursor(null);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        int itemPosition     = position;

        String  itemValue    = (String) l.getItemAtPosition(position);

    }
}
