package com.scv.slackgo.helpers;

import android.app.Application;

import com.scv.slackgo.models.Region;

/**
 * Created by kado on 10/13/16.
 */

public class SlackGoApplication extends Application {

    private Region region;

    public Region getRegion() {return region;}
    public void setRegion(Region region){this.region = region;}

}
