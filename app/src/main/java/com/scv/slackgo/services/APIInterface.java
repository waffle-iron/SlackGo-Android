package com.scv.slackgo.services;

import com.android.volley.Response;

/**
 * Created by ayelen@scvsoft.com on 10/12/16.
 */

public interface APIInterface {

    public Response authenticate();

    public Response joinChannel(String channel);

    public Response leaveChannel(String channel);
}
