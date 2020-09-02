package com.websarva.wings.android.dasenapp;

// TODO refactor
public class CachedPlayerNamesInfo {

    public static CachedPlayerNamesInfo instance = new CachedPlayerNamesInfo();

    private String[] namesOfNormal = new String[9];
    private String[] namesOfDh = new String[10];

    // setter
    public void setNameNormal(int i, String name) {
        namesOfNormal[i] = name;
    }

    public void setNameDh(int i, String name) {
        namesOfDh[i] = name;
    }


    // getter
    public String getNameNormal(int i) {
        return namesOfNormal[i];
    }

    public String getNameDh(int i) {
        return namesOfDh[i];
    }


    public String getAppropriateName(int i) {
        switch (CurrentOrderVersion.instance.getCurrentVersion()) {
            case FixedWords.NORMAL_ORDER:
                return getNameNormal(i);
            case FixedWords.DH_ORDER:
                return getNameDh(i);
        }
        return FixedWords.EMPTY;
    }

}
