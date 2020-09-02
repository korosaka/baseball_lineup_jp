package com.websarva.wings.android.dasenapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DhLineupFragment extends LineupParentFragment {

    public static DhLineupFragment newInstance() {
        DhLineupFragment fragment = new DhLineupFragment();
        Bundle args = new Bundle();
        args.putInt(FixedWords.NUMBER_OF_PLAYER, FixedWords.NUMBER_OF_LINEUP_DH);
        fragment.setArguments(args);
        return fragment;
    }

    // レイアウト紐付け
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_dh_lineup, container, false);
        playerList = view.findViewById(R.id.player_list_dh);
        return view;
    }

    @Override
    String getPositionFromCache(int orderNum) {
        return CachedPlayerPositionsInfo.instance.getPositionDh(orderNum);
    }

    @Override
    String getNameFromCache(int orderNum) {
        return CachedPlayerNamesInfo.instance.getNameDh(orderNum);
    }

}
