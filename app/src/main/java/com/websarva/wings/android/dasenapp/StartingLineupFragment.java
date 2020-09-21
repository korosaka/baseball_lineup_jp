package com.websarva.wings.android.dasenapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StartingLineupFragment extends BaseMembersFragment {

    public static StartingLineupFragment newInstance(int orderType) {
        return (StartingLineupFragment) bundleOrderType(new StartingLineupFragment(), orderType);
    }

    @Override
    View inflateView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_starting_lineup, container, false);
        playerList = view.findViewById(R.id.starting_player_list);
        return view;
    }


    @Override
    void setListAdapter() {
        listAdapter =
                new StartingPlayerListAdapter(
                        getContext(),
                        R.layout.starting_player_list_item,
                        CachedPlayersInfo.instance.getStartingMembers(orderType),
                        (MakingOrderActivity) getActivity(),
                        orderType);
    }

    @Override
    public void updatePlayerListView() {
        super.updatePlayerListView();
        if (orderType == FixedWords.SPECIAL_ORDER) setListViewHeightBasedOnChildren(playerList);
    }

}