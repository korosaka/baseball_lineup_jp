package com.websarva.wings.android.dasenapp;

import java.util.ArrayList;

public class CachedPlayersInfo {

    public static CachedPlayersInfo instance = new CachedPlayersInfo();

    private ArrayList<StartingPlayerListItemData> startingMembersNormal = new ArrayList<>();
    private ArrayList<StartingPlayerListItemData> startingMembersDh = new ArrayList<>();
    private ArrayList<StartingPlayerListItemData> startingMembersSpecial = new ArrayList<>();
    private ArrayList<ArrayList<StartingPlayerListItemData>> startingMembersArray = new ArrayList<>();

    private ArrayList<SubPlayerListItemData> subMembersNormal = new ArrayList<>();
    private ArrayList<SubPlayerListItemData> subMembersDh = new ArrayList<>();
    private ArrayList<SubPlayerListItemData> subMembersSpecial = new ArrayList<>();
    private ArrayList<ArrayList<SubPlayerListItemData>> subMembersArray = new ArrayList<>();

    /**
     * index
     * 0: Normal Order
     * 1: DH Order
     * 2: Special Order
     */
    public void initCachedArray() {
        startingMembersArray.add(startingMembersNormal);
        startingMembersArray.add(startingMembersDh);
        startingMembersArray.add(startingMembersSpecial);
        subMembersArray.add(subMembersNormal);
        subMembersArray.add(subMembersDh);
        subMembersArray.add(subMembersSpecial);
    }

    // for special starting order
    private int currentNumOfSpecialLineupDB;

    public void setCurrentNumOfSpecialLineupDB(int currentNumOfSpecialLineupDB) {
        this.currentNumOfSpecialLineupDB = currentNumOfSpecialLineupDB;
    }

    public int getCurrentNumOfSpecialLineupDB() {
        return currentNumOfSpecialLineupDB;
    }

    public void addStartingMember(int orderType, StartingPlayerListItemData startingMember) {
        startingMembersArray.get(orderType).add(startingMember);
    }

    public void addSubMember(int orderType, SubPlayerListItemData subMember) {
        subMembersArray.get(orderType).add(subMember);
    }


    private boolean isStartingMemberInitialised(int orderType) {
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                return startingMembersNormal.size() == FixedWords.NUMBER_OF_LINEUP_NORMAL;
            case FixedWords.DH_ORDER:
                return startingMembersDh.size() == FixedWords.NUMBER_OF_LINEUP_DH;
            case FixedWords.SPECIAL_ORDER:
                return startingMembersSpecial.size() == currentNumOfSpecialLineupDB;
        }
        return false;
    }

    public void deleteStartingPlayerOnSpecial() {
        int lastIndex = startingMembersSpecial.size() - 1;
        startingMembersSpecial.remove(lastIndex);
    }

    public void deleteSubPlayer(int orderType, int listIndex) {
        subMembersArray.get(orderType).remove(listIndex);
    }

    public void clearStartingArray(int orderType) {
        startingMembersArray.get(orderType).clear();
    }

    public void clearSubArray(int orderType) {
        subMembersArray.get(orderType).clear();
    }

    public ArrayList<StartingPlayerListItemData> getStartingMembers(int orderType) {
        return startingMembersArray.get(orderType);
    }

    public StartingPlayerListItemData getStartingMember(int orderType, int orderNum) {
        return getStartingMembers(orderType).get(convertOrderNumToIndexNum(orderNum));
    }

    public ArrayList<SubPlayerListItemData> getSubMembers(int orderType) {
        return subMembersArray.get(orderType);
    }

    public void overwriteSubPlayer(
            int orderType,
            int listIndex,
            boolean rolePitcher,
            boolean roleBatter,
            boolean roleRunner,
            boolean roleFielder,
            String name) {
        SubPlayerListItemData currentPlayer = subMembersArray.get(orderType).get(listIndex);
        SubPlayerListItemData newPlayer = new SubPlayerListItemData(
                currentPlayer.getId(),
                rolePitcher,
                roleBatter,
                roleRunner,
                roleFielder,
                name);

        subMembersArray.get(orderType).set(listIndex, newPlayer);
    }

    public void setPlayerInfoToCache(int orderType, int orderNum, String position, String name) {
        StartingPlayerListItemData newPlayer =
                new StartingPlayerListItemData(orderNum, position, name);

        if (!isStartingMemberInitialised(orderType)) addStartingMember(orderType, newPlayer);
        else startingMembersArray.get(orderType).set(convertOrderNumToIndexNum(orderNum), newPlayer);
    }

    private int convertOrderNumToIndexNum(int orderNum) {
        return orderNum - 1;
    }

}
