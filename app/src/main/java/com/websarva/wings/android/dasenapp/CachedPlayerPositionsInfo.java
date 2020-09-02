package com.websarva.wings.android.dasenapp;

// TODO refactor
public class CachedPlayerPositionsInfo {

    public static CachedPlayerPositionsInfo instance = new CachedPlayerPositionsInfo();

    private String[] positionsOfNormal = new String[9];
    private String[] positionsOfDh = new String[10];

    // setter
    public void setPositionNormal(int i, String name) {
        positionsOfNormal[i] = name;
    }

    public void setPositionDh(int i, String name) {
        positionsOfDh[i] = name;
        if (i == 9) positionsOfDh[i] = FixedWords.PITCHER;
    }


    // getter
    public String getPositionNormal(int i) {
        return positionsOfNormal[i];
    }

    public String getPositionDh(int i) {
        return positionsOfDh[i];
    }


    public String getAppropriatePosition(int i) {
        switch (CurrentOrderVersion.instance.getCurrentVersion()) {
            case FixedWords.NORMAL_ORDER:
                return getPositionNormal(i);
            case FixedWords.DH_ORDER:
                return getPositionDh(i);
        }
        return FixedWords.EMPTY;
    }


}
