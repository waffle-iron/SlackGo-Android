package com.scv.slackgo.helpers;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.scv.slackgo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayelen on 10/26/16.
 */

class ChannelsListAdapter extends ArrayAdapter<String>
{
    // boolean array for storing
    //the state of each CheckBox
    boolean[] checkBoxState;
    String[] channels;

    private Activity context;


    ViewHolder viewHolder;

    public ChannelsListAdapter(Activity context, int textViewResourceId,
                         List<String> channels) {

        //let android do the initializing :)
        super(context, textViewResourceId, channels);

        this.context = context;

        this.channels = channels.toArray(new String[0]);

        //create the boolean array with
        //initial state as false
        checkBoxState=new boolean[channels.size()];
    }


    //class for caching the views in a row
    private class ViewHolder
    {
        TextView channel;
        CheckBox checkBox;
    }

    public Integer[] getItemsChecked() {
        List<Integer> itemsChecked = new ArrayList<Integer>();

        for(int i = 0; i < checkBoxState.length; i++) {
            if(checkBoxState[i]) {
                itemsChecked.add(i);
            }
        }

        return itemsChecked.toArray(new Integer[0]);
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView==null)
        {
            convertView= context.getLayoutInflater().inflate(R.layout.list_layout, null, true);
            viewHolder=new ViewHolder();

            viewHolder.channel=(TextView) convertView.findViewById(R.id.channel);
            viewHolder.checkBox=(CheckBox) convertView.findViewById(R.id.checkBox);


            convertView.setTag(viewHolder);
        }
        else
            viewHolder=(ViewHolder) convertView.getTag();


        viewHolder.checkBox.setChecked(checkBoxState[position]);
        viewHolder.channel.setText(channels[position]);

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(((CheckBox)v).isChecked())
                    checkBoxState[position]=true;
                else
                    checkBoxState[position]=false;

            }
        });

        //return the view to be displayed
        return convertView;
    }

}