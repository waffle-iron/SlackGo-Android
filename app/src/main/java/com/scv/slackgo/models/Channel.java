package com.scv.slackgo.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ayelen@scvsoft.com on 10/20/16.
 */

public class Channel {

    String name;
    boolean isArchived;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Channel(String name, boolean isArchived, String id){
        this.name = name;
        this.isArchived = isArchived;
        this.id = id;
    }
    public Channel(){
    }

    public static Channel fromJson(JSONObject json) throws JSONException {
        return new Channel(json.getString("name"), json.getBoolean("is_archived"), json.getString("id"));
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
