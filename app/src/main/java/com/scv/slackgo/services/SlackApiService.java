package com.scv.slackgo.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.scv.slackgo.R;
import com.scv.slackgo.helpers.Constants;
import com.scv.slackgo.helpers.ErrorUtils;
import com.scv.slackgo.helpers.GsonUtils;
import com.scv.slackgo.models.Channel;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * Created by ayelen@scvsoft.com on 10/12/16.
 */

public class SlackApiService extends Observable implements APIInterface {

    protected static final String TAG = "GeofenceTransitionsIS";


    private RequestQueue queue;
    private Context context;
    private ArrayList<String> channelsName;

    public SlackApiService(Context context) {
        this.context = context;
        this.addObserver((Observer) context);
        queue = Volley.newRequestQueue(context);
    }

    @Override
    public Response authenticate() {
        return null;
    }

    @Override
    public void joinChannel(String channel) {

        String url = FromatUrl(R.string.slack_channel_join, true, channel);
        callToAPIWithoutResponse(url);
    }

    @Override
    public void leaveChannel(String channel) {

        String url = FromatUrl(R.string.slack_channel_leave, true, channel);
        callToAPIWithoutResponse(url);
    }

    private void callToAPIWithoutResponse(String url) {
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG,response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,error.getMessage());
                    }
                });
        queue.add(request);

    }

    private String FromatUrl(int urlIndex, boolean isTokenNeeded, String... params) {

        if (isTokenNeeded) {
            String slackToken = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, context.MODE_PRIVATE).getString(Constants.SLACK_TOKEN, null);
            return String.format(context.getString(urlIndex), slackToken, params[0]);
        }
        return String.format(context.getString(urlIndex), params[0]);
    }

    @Override
    public void getAvailableChannels() {

        String slackToken = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, context.MODE_PRIVATE).getString(Constants.SLACK_TOKEN, null);
        String url = String.format(context.getString(R.string.slack_channels_url), slackToken);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray listOfChannelsAsJSON = new JSONObject(response).getJSONArray("channels");
                            getChannelsName(listOfChannelsAsJSON);
                        } catch (JSONException e) {
                            ErrorUtils.showErrorAlert(context);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ErrorUtils.showErrorAlert(context);
                    }
                });
        queue.add(request);
    }

    public void getSlackToken(String url) {

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseToJson = new JSONObject(response);
                            addTokenToPreferences(responseToJson);
                        } catch (JSONException e) {
                            ErrorUtils.showErrorAlert(context);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ErrorUtils.showErrorAlert(context);
                    }
                });
        queue.add(request);

    }

    private void addTokenToPreferences(JSONObject responseToJson) throws JSONException {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, context.MODE_PRIVATE).edit();
        editor.putString(Constants.SLACK_TOKEN, responseToJson.getString("access_token"));
        editor.commit();
        setChanged();
        notifyObservers();
    }

    private void getChannelsName(JSONArray listOfChannelsAsJSON) throws JSONException {

        List<Channel> channels = new ArrayList<>();
        for (int i = 0; i < listOfChannelsAsJSON.length(); i++) {
            channels.add(GsonUtils.setObject(new Channel(), listOfChannelsAsJSON.getJSONObject(i)));
        }


        CollectionUtils.filter(channels, new Predicate<Channel>() {
            @Override
            public boolean evaluate(Channel channel) {
                return !channel.isArchived();
            }
        });

        channelsName = new ArrayList<>(CollectionUtils.collect(channels, new Transformer<Channel, String>() {
            public String transform(Channel channel) {
                return channel.getName();
            }
        }));
        setChanged();
        notifyObservers(channelsName);
    }
}
