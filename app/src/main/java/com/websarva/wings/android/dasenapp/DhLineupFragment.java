package com.websarva.wings.android.dasenapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

public class DhLineupFragment extends LineupParentFragment {

    public static DhLineupFragment newInstance() {
        DhLineupFragment fragment = new DhLineupFragment();
        Bundle args = new Bundle();
        args.putInt(FixedWords.NUMBER_OF_PLAYER, 10);
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


    // TODO write in MainActivity ?
    public void setPitcherButtonEnable(boolean enable) {
        ((MainActivity) Objects.requireNonNull(getActivity())).getDhPitcherButton().setEnabled(enable);
    }

    @Override
    String getPositionFromCache(int index) {
        return CachedPlayerPositionsInfo.instance.getPositionDh(index);
    }

    @Override
    String getNameFromCache(int index) {
        return CachedPlayerNamesInfo.instance.getNameDh(index);
    }

}
