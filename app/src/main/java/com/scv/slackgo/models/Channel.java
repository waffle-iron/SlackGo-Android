package com.scv.slackgo.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SCV on 10/20/16.
 */

public class Channel {

    String name;
    boolean isArchived;

    public Channel(String name, boolean isArchived){
        this.name = name;
        this.isArchived = isArchived;
    }

    public static Channel fromJson(JSONObject json) throws JSONException {
        return new Channel(json.getString("name"), json.getBoolean("is_archived"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }
}
