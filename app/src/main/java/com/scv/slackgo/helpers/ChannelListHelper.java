package com.scv.slackgo.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.scv.slackgo.R;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by SCV on 10/25/16.
 */

public class ChannelListHelper {

    public static void buildList(final Activity context, final List<String> values, ListView channelsListView) {

        channelsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        channelsListView.setItemsCanFocus(false);

        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Available Channels");

        final ChannelsListAdapter adapter = new ChannelsListAdapter(context,
                R.layout.list_layout, values);

        builderSingle.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = adapter.getItem(which);
            }
        });

        builderSingle.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Integer[] channelIds = adapter.getItemsChecked();

                        String concatChannels = "";

                        for (int i = 0; i < values.size(); i++) {
                            if (isIn(channelIds, i)) {
                                if (!concatChannels.equals("")) {
                                    concatChannels = concatChannels.concat(", ");
                                }
                                concatChannels = concatChannels.concat(values.get(i));
                            }
                        }
                        IterableUtils.forEach(values, new Closure<String>() {
                            @Override
                            public void execute(String input) {

                            }
                        });

                        ((TextView) context.findViewById(R.id.selected_channels)).setText(context.getText(R.string.channels_to_join) + concatChannels);
                    }
                });

        final AlertDialog channelsAlert = builderSingle.create();


        channelsAlert.show();
    }

    private static boolean isIn(Integer[] ids, final int id) {
        Integer value = IterableUtils.find(Arrays.asList(ids), new Predicate<Integer>() {
            @Override
            public boolean evaluate(Integer index) {
                return index == id;
            }
        });
        return value != null;
    }
}
