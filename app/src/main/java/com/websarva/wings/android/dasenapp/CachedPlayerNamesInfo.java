package com.websarva.wings.android.dasenapp;

public class CachedPlayerNamesInfo {

    public static CachedPlayerNamesInfo instance = new CachedPlayerNamesInfo();

    private String[] namesOfNormal = new String[9];
    private String[] namesOfDh = new String[10];
    private String[] namesOfAll10 = new String[10];
    private String[] namesOfAll11 = new String[11];
    private String[] namesOfAll12 = new String[12];
    private String[] namesOfAll13 = new String[13];
    private String[] namesOfAll14 = new String[14];
    private String[] namesOfAll15 = new String[15];

    // setter
    public void setNameNormal(int i, String name) {
        namesOfNormal[i] = name;
    }

    public void setNameDh(int i, String name) {
        namesOfDh[i] = name;
    }

    public void setNameAll10(int i, String name) {
        namesOfAll10[i] = name;
    }

    public void setNameAll11(int i, String name) {
        namesOfAll11[i] = name;
    }

    public void setNameAll12(int i, String name) {
        namesOfAll12[i] = name;
    }

    public void setNameAll13(int i, String name) {
        namesOfAll13[i] = name;
    }

    public void setNameAll14(int i, String name) {
        namesOfAll14[i] = name;
    }

    public void setNameAll15(int i, String name) {
        namesOfAll15[i] = name;
    }


    // getter
    public String getNameNormal(int i) {
        return namesOfNormal[i];
    }

    public String getNameDh(int i) {
        return namesOfDh[i];
    }

    public String getNameAll10(int i) {
        return namesOfAll10[i];
    }

    public String getNameAll11(int i) {
        return namesOfAll11[i];
    }

    public String getNameAll12(int i) {
        return namesOfAll12[i];
    }

    public String getNameAll13(int i) {
        return namesOfAll13[i];
    }

    public String getNameAll14(int i) {
        return namesOfAll14[i];
    }

    public String getNameAll15(int i) {
        return namesOfAll15[i];
    }

    public String getAppropriateName(int i) {
        switch (CurrentOrderVersion.instance.getCurrentVersion()) {
            case FixedWords.DEFAULT:
                return getNameNormal(i);
            case FixedWords.DH:
                return getNameDh(i);
            case FixedWords.ALL10:
                return getNameAll10(i);
            case FixedWords.ALL11:
                return getNameAll11(i);
            case FixedWords.ALL12:
                return getNameAll12(i);
            case FixedWords.ALL13:
                return getNameAll13(i);
            case FixedWords.ALL14:
                return getNameAll14(i);
            case FixedWords.ALL15:
                return getNameAll15(i);
        }
        return FixedWords.EMPTY;
    }

}
