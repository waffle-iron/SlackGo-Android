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
import com.scv.slackgo.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ayelen@scvsoft.com on 10/12/16.
 */

public class SlackApiService implements APIInterface {

    private RequestQueue queue;
    private Context context;

    public SlackApiService(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
    }

    @Override
    public Response authenticate() {
        return null;
    }

    @Override
    public Response joinChannel(String channel) {
        return null;
    }

    @Override
    public Response leaveChannel(String channel) {
        return null;
    }

    public void getSlackToken(String url) {

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseToJson = new JSONObject(response);
                            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, context.MODE_PRIVATE).edit();
                            editor.putString(Constants.SLACK_TOKEN, responseToJson.getString("access_token"));
                            editor.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HttpClient", "error: " + error.toString());
                    }
                });
        queue.add(request);

    }

    private void createStringRequest(int method, String url) {
        final JSONObject[] responseToJson = new JSONObject[1];
        StringRequest request = new StringRequest(method, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                         responseToJson[0] = null;
                        try {
                            responseToJson[0] = new JSONObject(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HttpClient", "error: " + error.toString());
                    }
                });
        queue.add(request);
    }

}
