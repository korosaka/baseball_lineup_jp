package com.websarva.wings.android.dasenapp;

public class CurrentOrderVersion {


    public static CurrentOrderVersion instance = new CurrentOrderVersion();

    private int currentVersion = FixedWords.DEFAULT;

    public void setCurrentVersion(int version) {
        currentVersion = version;
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

}
