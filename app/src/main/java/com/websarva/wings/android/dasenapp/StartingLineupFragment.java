package com.websarva.wings.android.dasenapp;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StartingLineupFragment extends BaseMembersFragment {

    // TODO after ++ -- (for special rule)
    private int numberOfPlayer;

    public static StartingLineupFragment newInstance(int orderType) {
        return (StartingLineupFragment) bundleOrderType(new StartingLineupFragment(), orderType);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNumberOfPlayer();
    }


    private void setNumberOfPlayer() {
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                numberOfPlayer = FixedWords.NUMBER_OF_LINEUP_NORMAL;
                break;
            case FixedWords.DH_ORDER:
                numberOfPlayer = FixedWords.NUMBER_OF_LINEUP_DH;
                break;
        }
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
                        (MakingOrderActivity) getActivity());
    }

}