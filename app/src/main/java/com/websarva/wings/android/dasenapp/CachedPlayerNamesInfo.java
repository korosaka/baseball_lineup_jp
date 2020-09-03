package com.websarva.wings.android.dasenapp;

// TODO refactor
public class CachedPlayerNamesInfo {

    public static CachedPlayerNamesInfo instance = new CachedPlayerNamesInfo();

    // TODO refactor
    private String[] namesOfNormal = new String[9];
    private String[] namesOfDh = new String[10];

    // TODO setAppropriateName
    // setter
    public void setNameNormal(int orderNum, String name) {
        namesOfNormal[convertOrderNumToIndexNum(orderNum)] = name;
    }

    public void setNameDh(int orderNum, String name) {
        namesOfDh[convertOrderNumToIndexNum(orderNum)] = name;
    }


    // getter
    public String getNameNormal(int orderNum) {
        return namesOfNormal[convertOrderNumToIndexNum(orderNum)];
    }

    public String getNameDh(int orderNum) {
        return namesOfDh[convertOrderNumToIndexNum(orderNum)];
    }


    public String getAppropriateName(int orderType, int orderNum) {
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                return getNameNormal(orderNum);
            case FixedWords.DH_ORDER:
                return getNameDh(orderNum);
        }
        return FixedWords.EMPTY;
    }

    private int convertOrderNumToIndexNum(int orderNum) {
        return orderNum - 1;
    }

}
