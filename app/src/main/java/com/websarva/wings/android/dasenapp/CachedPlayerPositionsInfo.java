package com.websarva.wings.android.dasenapp;

// TODO refactor
public class CachedPlayerPositionsInfo {

    public static CachedPlayerPositionsInfo instance = new CachedPlayerPositionsInfo();

    private String[] positionsOfNormal = new String[9];
    private String[] positionsOfDh = new String[10];

    // setter
    public void setPositionNormal(int orderNum, String name) {
        positionsOfNormal[convertOrderNumToIndexNum(orderNum)] = name;
    }

    public void setPositionDh(int orderNum, String name) {
        positionsOfDh[convertOrderNumToIndexNum(orderNum)] = name;
        if (orderNum == FixedWords.DH_PITCHER_ORDER)
            positionsOfDh[convertOrderNumToIndexNum(orderNum)] = FixedWords.PITCHER;
    }


    // getter
    public String getPositionNormal(int orderNum) {
        return positionsOfNormal[convertOrderNumToIndexNum(orderNum)];
    }

    public String getPositionDh(int orderNum) {
        return positionsOfDh[convertOrderNumToIndexNum(orderNum)];
    }


    public String getAppropriatePosition(int orderNum) {
        switch (CurrentOrderVersion.instance.getCurrentVersion()) {
            case FixedWords.NORMAL_ORDER:
                return getPositionNormal(orderNum);
            case FixedWords.DH_ORDER:
                return getPositionDh(orderNum);
        }
        return FixedWords.EMPTY;
    }

    private int convertOrderNumToIndexNum(int orderNum) {
        return orderNum - 1;
    }

}
