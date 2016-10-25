package com.scv.slackgo.helpers;

import com.google.common.base.CaseFormat;
import com.google.gson.Gson;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.IterableUtils;
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
        final String regex = "([A-Z][a-z]+)";
        final String replacement = "$1_";

        try {
            final Map<String, Object> values = toMap(json);

            final Class<?> finalClazz = object.getClass();
            List<Field> fields = Arrays.asList(finalClazz.getDeclaredFields());
            IterableUtils.forEach(fields, new Closure<Field>() {
                @Override
                public void execute(Field input) {
                    try {
                        input.setAccessible(true);
                        Object value = values.get(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, input.getName()));
                        if(value != null) {
                            input.set(object, value);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
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
