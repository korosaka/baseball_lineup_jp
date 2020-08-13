package com.websarva.wings.android.dasenapp;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private SharedPreferences dataStore;

    public MySharedPreferences(Context context) {
        dataStore = context.getSharedPreferences(FixedWords.DATA_STORE, Context.MODE_PRIVATE);
    }

    public void storeBoolean(Boolean bool, String key) {
        SharedPreferences.Editor editor = dataStore.edit();
        editor.putBoolean(key, bool);
        editor.apply();
    }

    public Boolean getBoolean(String key) {
        return dataStore.getBoolean(key, false);
    }
}
