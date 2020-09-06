package com.websarva.wings.android.dasenapp;

import java.util.ArrayList;

public class CachedPlayersInfo {

    public static CachedPlayersInfo instance = new CachedPlayersInfo();

    // TODO should treat as starting players[] ??
    private String[] startingNamesOfNormal = new String[FixedWords.NUMBER_OF_LINEUP_NORMAL];
    private String[] startingNamesOfDh = new String[FixedWords.NUMBER_OF_LINEUP_DH];

    private String[] startingPositionsOfNormal = new String[FixedWords.NUMBER_OF_LINEUP_NORMAL];
    private String[] startingPositionsOfDh = new String[FixedWords.NUMBER_OF_LINEUP_DH];

    private ArrayList<SubPlayerListItemData> subMembersNormal = new ArrayList<>();
    private ArrayList<SubPlayerListItemData> subMembersDh = new ArrayList<>();


    public void addSubMember(int orderType, SubPlayerListItemData subMember) {
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                subMembersNormal.add(subMember);
                break;
            case FixedWords.DH_ORDER:
                subMembersDh.add(subMember);
                break;
        }
    }

    public void deleteSubPlayer(int orderType, int listIndex) {
        if (orderType == FixedWords.NORMAL_ORDER) subMembersNormal.remove(listIndex);
        else subMembersDh.remove(listIndex);
    }

    public void clearSubArray(int orderType) {
        if (orderType == FixedWords.NORMAL_ORDER) subMembersNormal.clear();
        else subMembersDh.clear();
    }

    public ArrayList<SubPlayerListItemData> getSubMembers(int orderType) {
        if (orderType == FixedWords.NORMAL_ORDER) return subMembersNormal;
        return subMembersDh;
    }

    public void overwriteSubPlayer(
            int orderType,
            int listIndex,
            boolean rolePitcher,
            boolean roleBatter,
            boolean roleRunner,
            boolean roleFielder,
            String name) {
        SubPlayerListItemData currentPlayer;
        if (orderType == FixedWords.NORMAL_ORDER) currentPlayer = subMembersNormal.get(listIndex);
        else currentPlayer = subMembersDh.get(listIndex);
        SubPlayerListItemData newPlayer = new SubPlayerListItemData(
                currentPlayer.getId(),
                rolePitcher,
                roleBatter,
                roleRunner,
                roleFielder,
                name);

        if (orderType == FixedWords.NORMAL_ORDER) subMembersNormal.set(listIndex, newPlayer);
        else subMembersDh.set(listIndex, newPlayer);
    }

//    public SubPlayerListItemData getSubMember(int orderType, int positionNum) {
//        if (orderType == FixedWords.NORMAL_ORDER) return subMembersNormal.get(positionNum);
//        return subMembersDh.get(positionNum);
//    }

    private void setNameNormal(int orderNum, String name) {
        startingNamesOfNormal[convertOrderNumToIndexNum(orderNum)] = name;
    }

    private void setNameDh(int orderNum, String name) {
        startingNamesOfDh[convertOrderNumToIndexNum(orderNum)] = name;
    }

    private void setNameToCache(int orderType, int orderNum, String name) {
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                setNameNormal(orderNum, name);
                break;
            case FixedWords.DH_ORDER:
                setNameDh(orderNum, name);
                break;
        }
    }

    private void setPositionNormal(int orderNum, String position) {
        startingPositionsOfNormal[convertOrderNumToIndexNum(orderNum)] = position;
    }

    private void setPositionDh(int orderNum, String position) {
        startingPositionsOfDh[convertOrderNumToIndexNum(orderNum)] = position;
    }

    private void setPositionToCache(int orderType, int orderNum, String position) {
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                setPositionNormal(orderNum, position);
                break;
            case FixedWords.DH_ORDER:
                setPositionDh(orderNum, position);
                break;
        }
    }

    public void setPlayerInfoToCache(int orderType, int orderNum, String position, String name) {
        setNameToCache(orderType, orderNum, name);
        setPositionToCache(orderType, orderNum, position);
    }


    private String getNameNormal(int orderNum) {
        return startingNamesOfNormal[convertOrderNumToIndexNum(orderNum)];
    }

    private String getNameDh(int orderNum) {
        return startingNamesOfDh[convertOrderNumToIndexNum(orderNum)];
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
        return startingPositionsOfNormal[convertOrderNumToIndexNum(orderNum)];
    }

    private String getPositionDh(int orderNum) {
        return startingPositionsOfDh[convertOrderNumToIndexNum(orderNum)];
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
