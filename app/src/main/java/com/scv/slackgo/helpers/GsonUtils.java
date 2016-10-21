package com.scv.slackgo.helpers;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ayelen@scvsoft.com
 */

public class GsonUtils {

    public static <C> C getObjectFromJson(String json, Class<C> clazz) {
        Gson gson = new Gson();
        C object = gson.fromJson(json, clazz);
        return object;

    }

    public static <C> ArrayList<C> getListFromJson(String json, Class<C[]> clazz) {
        if (json == null) {
            return null;
        }
        C[] arr = new Gson().fromJson(json, clazz);
        return new ArrayList<C>(Arrays.asList(arr));
    }

    public static <C> String getJsonFromObject(C object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static <C> C setObject(final C object, JSONObject json) {

        Map<String, Object> values = null;
        try {
            values = toMap(json);

            final Class<?> finalClazz = object.getClass();
            for (Map.Entry<String, Object> input : values.entrySet()) {
                try {
                    Field field = finalClazz.getDeclaredField(input.getKey());
                    field.setAccessible(true);
                    field.set(object, input.getValue());
                } catch (NoSuchFieldException e) {
                    return null;
                } catch (IllegalAccessException e) {
                    return null;
                }
            }
            return object;
        } catch (JSONException e) {
            return null;
        }
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
