package com.scv.slackgo.helpers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayelen@scvsoft.com
 */

public class GsonUtils {

    public static <C> C getObjectFromJson(String json, Class<C> clazz) {
        Gson gson = new Gson();
        C object = gson.fromJson(json, clazz);
        return object;

    }

    public static <C> ArrayList<C> getListFromJson(String json, Class<C> clazz) {
        Gson gson = new Gson();
        Type typeOfLocations = new TypeToken<List<C>>() {
        }.getType();
        return gson.fromJson(json, typeOfLocations);
    }

    public static <C> String getJsonFromObject(C object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }
}
