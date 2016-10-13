package com.scv.slackgo.models;

import com.scv.slackgo.helpers.Constants;

import java.util.ArrayList;

/**
 * Created by kado on 10/13/16.
 */

public class Region {

    private String name;
    private float latitude;
    private float longitude;
    private float radius;
    private float cameraZoom;
    private ArrayList<String> channels;


    public Region(String name, float latitude, float longitude, float radius, float cameraZoom, ArrayList<String> channels) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.cameraZoom = cameraZoom;
        this.channels = channels;
    }

    public static Region getMockRegion(){
        ArrayList<String> channels = new ArrayList<>();
        channels.add("oficina");
        return new Region(Constants.OFFICE, Constants.SCV_OFFICE_LAT , Constants.SCV_OFFICE_LONG, 100, 15.0f, channels);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getCameraZoom() {
        return cameraZoom;
    }

    public void setCameraZoom(float cameraZoom) {
        this.cameraZoom = cameraZoom;
    }

    public ArrayList<String> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<String> channels) {
        this.channels = channels;
    }
}
