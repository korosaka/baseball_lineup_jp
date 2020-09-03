package com.websarva.wings.android.dasenapp;

public class CachedPlayersInfo {

    public static CachedPlayersInfo instance = new CachedPlayersInfo();

    private String[] namesOfNormal = new String[FixedWords.NUMBER_OF_LINEUP_NORMAL];
    private String[] namesOfDh = new String[FixedWords.NUMBER_OF_LINEUP_DH];

    private String[] positionsOfNormal = new String[FixedWords.NUMBER_OF_LINEUP_NORMAL];
    private String[] positionsOfDh = new String[FixedWords.NUMBER_OF_LINEUP_DH];


    private void setNameNormal(int orderNum, String name) {
        namesOfNormal[convertOrderNumToIndexNum(orderNum)] = name;
    }

    private void setNameDh(int orderNum, String name) {
        namesOfDh[convertOrderNumToIndexNum(orderNum)] = name;
    }

    private void setNameToCache(int orderType, int orderNum, String name) {
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                setNameNormal(orderNum, name);
            case FixedWords.DH_ORDER:
                setNameDh(orderNum, name);
        }
    }

    private void setPositionNormal(int orderNum, String position) {
        positionsOfNormal[convertOrderNumToIndexNum(orderNum)] = position;
    }

    private void setPositionDh(int orderNum, String position) {
        positionsOfDh[convertOrderNumToIndexNum(orderNum)] = position;
    }

    private void setPositionToCache(int orderType, int orderNum, String position) {
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                setPositionNormal(orderNum, position);
            case FixedWords.DH_ORDER:
                setPositionDh(orderNum, position);
        }
    }

    public void setPlayerInfoToCache(int orderType, int orderNum, String position, String name) {
        setNameToCache(orderType, orderNum, name);
        setPositionToCache(orderType, orderNum, position);
    }


    private String getNameNormal(int orderNum) {
        return namesOfNormal[convertOrderNumToIndexNum(orderNum)];
    }

    private String getNameDh(int orderNum) {
        return namesOfDh[convertOrderNumToIndexNum(orderNum)];
    }

    public String getNameFromCache(int orderType, int orderNum) {
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                return getNameNormal(orderNum);
            case FixedWords.DH_ORDER:
                return getNameDh(orderNum);
        }
        return FixedWords.EMPTY;
    }

    private String getPositionNormal(int orderNum) {
        return positionsOfNormal[convertOrderNumToIndexNum(orderNum)];
    }

    private String getPositionDh(int orderNum) {
        return positionsOfDh[convertOrderNumToIndexNum(orderNum)];
    }

    public String getPositionFromCache(int orderType, int orderNum) {
        switch (orderType) {
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
