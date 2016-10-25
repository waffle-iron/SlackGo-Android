package com.scv.slackgo.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by SCV on 10/25/16.
 */

public class ChannelListHelper {

    public static void buildList(Activity context, String[] values, ListView channelsListView) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Select One Name:-");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.select_dialog_multichoice, android.R.id.text1, values);

        final ListView finalChannelsListView = channelsListView;
        builderSingle.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finalChannelsListView.getCheckedItemPositions();
                    }
                });
        builderSingle.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = adapter.getItem(which);
            }
        });
    }
}
