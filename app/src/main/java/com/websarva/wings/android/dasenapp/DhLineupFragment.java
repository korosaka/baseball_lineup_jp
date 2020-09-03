package com.websarva.wings.android.dasenapp;

import android.os.Bundle;

// TODO DH無しと全て共通化できる(Cache class)
public class DhLineupFragment extends LineupParentFragment {

    public static DhLineupFragment newInstance() {
        DhLineupFragment fragment = new DhLineupFragment();
        Bundle args = new Bundle();
        args.putInt(FixedWords.NUMBER_OF_PLAYER, FixedWords.NUMBER_OF_LINEUP_DH);
        fragment.setArguments(args);
        return fragment;
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
