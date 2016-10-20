package com.scv.slackgo.services;

import com.android.volley.Response;

import java.util.List;

/**
 * Created by ayelen@scvsoft.com on 10/12/16.
 */

public interface APIInterface {

    public Response authenticate();

    public void joinChannel(String channel);

    public void leaveChannel(String channel);

    public void getAvailableChannels();
}
