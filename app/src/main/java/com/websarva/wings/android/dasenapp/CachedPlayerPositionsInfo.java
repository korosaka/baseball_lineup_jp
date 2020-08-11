package com.websarva.wings.android.dasenapp;

public class CachedPlayerPositionsInfo {

    public static CachedPlayerPositionsInfo instance = new CachedPlayerPositionsInfo();

    private String[] positionsOfNormal = new String[9];
    private String[] positionsOfDh = new String[10];
    private String[] positionsOfAll10 = new String[10];
    private String[] positionsOfAll11 = new String[11];
    private String[] positionsOfAll12 = new String[12];
    private String[] positionsOfAll13 = new String[13];
    private String[] positionsOfAll14 = new String[14];
    private String[] positionsOfAll15 = new String[15];

    // setter
    public void setPositionNormal(int i, String name) {
        positionsOfNormal[i] = name;
    }

    public void setPositionDh(int i, String name) {
        positionsOfDh[i] = name;
        if (i == 9) positionsOfDh[i] = FixedWords.PITCHER;
    }

    public void setPositionAll10(int i, String name) {
        positionsOfAll10[i] = name;
    }

    public void setPositionAll11(int i, String name) {
        positionsOfAll11[i] = name;
    }

    public void setPositionAll12(int i, String name) {
        positionsOfAll12[i] = name;
    }

    public void setPositionAll13(int i, String name) {
        positionsOfAll13[i] = name;
    }

    public void setPositionAll14(int i, String name) {
        positionsOfAll14[i] = name;
    }

    public void setPositionAll15(int i, String name) {
        positionsOfAll15[i] = name;
    }


    // getter
    public String getPositionNormal(int i) {
        return positionsOfNormal[i];
    }

    public String getPositionDh(int i) {
        return positionsOfDh[i];
    }

    public String getPositionAll10(int i) {
        return positionsOfAll10[i];
    }

    public String getPositionAll11(int i) {
        return positionsOfAll11[i];
    }

    public String getPositionAll12(int i) {
        return positionsOfAll12[i];
    }

    public String getPositionAll13(int i) {
        return positionsOfAll13[i];
    }

    public String getPositionAll14(int i) {
        return positionsOfAll14[i];
    }

    public String getPositionAll15(int i) {
        return positionsOfAll15[i];
    }

    public String getAppropriatePosition(int i) {
        switch (CurrentOrderVersion.instance.getCurrentVersion()) {
            case FixedWords.DEFAULT:
                return getPositionNormal(i);
            case FixedWords.DH:
                return getPositionDh(i);
            case FixedWords.ALL10:
                return getPositionAll10(i);
            case FixedWords.ALL11:
                return getPositionAll11(i);
            case FixedWords.ALL12:
                return getPositionAll12(i);
            case FixedWords.ALL13:
                return getPositionAll13(i);
            case FixedWords.ALL14:
                return getPositionAll14(i);
            case FixedWords.ALL15:
                return getPositionAll15(i);
        }
        return FixedWords.EMPTY;
    }


}
