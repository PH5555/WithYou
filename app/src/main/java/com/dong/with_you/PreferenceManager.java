package com.dong.with_you;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferenceManager {
    public static final String PREFERENCES_NAME = "rebuild_preference";
    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static void setString(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        String value = prefs.getString(key, DEFAULT_VALUE_STRING);
        return value;
    }

    public static void setArrayList(Context context, String key, List<Item> value){
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(value);

        editor.putString(key, json);
        editor.commit();
    }

    public static List<Item> getArrayList(Context context, String key){
        SharedPreferences prefs = getPreferences(context);
        Gson gson = new Gson();

        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Item>>(){}.getType();
        List<Item> result = gson.fromJson(json, type);

        if(result == null){
            List<Item> item = new ArrayList<>();
            return item;
        }
        else {
            return result;
        }
    }
}
