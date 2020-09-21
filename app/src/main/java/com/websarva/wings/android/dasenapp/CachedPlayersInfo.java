package com.websarva.wings.android.dasenapp;

import java.util.ArrayList;

public class CachedPlayersInfo {

    public static CachedPlayersInfo instance = new CachedPlayersInfo();

    private ArrayList<StartingPlayerListItemData> startingMembersNormal = new ArrayList<>();
    private ArrayList<StartingPlayerListItemData> startingMembersDh = new ArrayList<>();
    private ArrayList<StartingPlayerListItemData> startingMembersSpecial = new ArrayList<>();

    private ArrayList<SubPlayerListItemData> subMembersNormal = new ArrayList<>();
    private ArrayList<SubPlayerListItemData> subMembersDh = new ArrayList<>();
    private ArrayList<SubPlayerListItemData> subMembersSpecial = new ArrayList<>();

    // for special starting order
    private int currentNumOfSpecialLineupDB;
    public void setCurrentNumOfSpecialLineupDB(int currentNumOfSpecialLineupDB) {
        this.currentNumOfSpecialLineupDB = currentNumOfSpecialLineupDB;
    }

    public void addStartingMember(int orderType, StartingPlayerListItemData startingMember) {
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                startingMembersNormal.add(startingMember);
                break;
            case FixedWords.DH_ORDER:
                startingMembersDh.add(startingMember);
                break;
            case FixedWords.SPECIAL_ORDER:
                startingMembersSpecial.add(startingMember);
                break;
        }
    }

    public void addSubMember(int orderType, SubPlayerListItemData subMember) {
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                subMembersNormal.add(subMember);
                break;
            case FixedWords.DH_ORDER:
                subMembersDh.add(subMember);
                break;
            case FixedWords.SPECIAL_ORDER:
                subMembersSpecial.add(subMember);
                break;
        }
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
        if (!isStartingMemberInitialised(FixedWords.SPECIAL_ORDER)) return;
        int lastIndex = startingMembersSpecial.size() - 1;
        startingMembersSpecial.remove(lastIndex);
    }

    public void deleteSubPlayer(int orderType, int listIndex) {
        if (orderType == FixedWords.NORMAL_ORDER) subMembersNormal.remove(listIndex);
        else if(orderType == FixedWords.DH_ORDER) subMembersDh.remove(listIndex);
        else subMembersSpecial.remove(listIndex);
    }

    public void clearStartingArray(int orderType) {
        if (orderType == FixedWords.NORMAL_ORDER) startingMembersNormal.clear();
        else if(orderType == FixedWords.DH_ORDER) startingMembersDh.clear();
        else startingMembersSpecial.clear();
    }

    public void clearSubArray(int orderType) {
        if (orderType == FixedWords.NORMAL_ORDER) subMembersNormal.clear();
        else if(orderType == FixedWords.DH_ORDER) subMembersDh.clear();
        else subMembersSpecial.clear();
    }

    public ArrayList<StartingPlayerListItemData> getStartingMembers(int orderType) {
        if (orderType == FixedWords.NORMAL_ORDER) return startingMembersNormal;
        else if(orderType == FixedWords.DH_ORDER) return startingMembersDh;
        else return startingMembersSpecial;
    }

    public StartingPlayerListItemData getStartingMember(int orderType, int orderNum) {
        return getStartingMembers(orderType).get(convertOrderNumToIndexNum(orderNum));
    }

    public ArrayList<SubPlayerListItemData> getSubMembers(int orderType) {
        if (orderType == FixedWords.NORMAL_ORDER) return subMembersNormal;
        else if(orderType == FixedWords.DH_ORDER) return subMembersDh;
        else return subMembersSpecial;
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
        else if(orderType == FixedWords.DH_ORDER) subMembersDh.set(listIndex, newPlayer);
        else subMembersSpecial.set(listIndex, newPlayer);
    }

    public void setPlayerInfoToCache(int orderType, int orderNum, String position, String name) {
        StartingPlayerListItemData newPlayer =
                new StartingPlayerListItemData(orderNum, position, name);

        if (!isStartingMemberInitialised(orderType)) addStartingMember(orderType, newPlayer);

        if (orderType == FixedWords.NORMAL_ORDER)
            startingMembersNormal.set(convertOrderNumToIndexNum(orderNum), newPlayer);
        else if(orderType == FixedWords.DH_ORDER)
            startingMembersDh.set(convertOrderNumToIndexNum(orderNum), newPlayer);
        else
            startingMembersSpecial.set(convertOrderNumToIndexNum(orderNum), newPlayer);
    }

    private int convertOrderNumToIndexNum(int orderNum) {
        return orderNum - 1;
    }

}
