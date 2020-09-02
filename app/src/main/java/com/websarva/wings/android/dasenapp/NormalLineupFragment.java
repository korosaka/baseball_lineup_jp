package com.websarva.wings.android.dasenapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NormalLineupFragment extends LineupParentFragment {

    public static NormalLineupFragment newInstance() {
        NormalLineupFragment fragment = new NormalLineupFragment();
        Bundle args = new Bundle();
        args.putInt(FixedWords.NUMBER_OF_PLAYER, FixedWords.NUMBER_OF_LINEUP_NORMAL);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_normal_lineup, container, false);
        playerList = view.findViewById(R.id.player_list_normal);
        return view;
    }

    @Override
    String getPositionFromCache(int orderNum) {
        return CachedPlayerPositionsInfo.instance.getPositionNormal(orderNum);
    }

    @Override
    String getNameFromCache(int orderNum) {
        return CachedPlayerNamesInfo.instance.getNameNormal(orderNum);
    }

}
