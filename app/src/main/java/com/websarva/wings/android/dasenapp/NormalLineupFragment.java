package com.websarva.wings.android.dasenapp;

import android.os.Bundle;

// TODO DHと全て共通化できる(Cache class)
public class NormalLineupFragment extends LineupParentFragment {

    public static NormalLineupFragment newInstance() {
        NormalLineupFragment fragment = new NormalLineupFragment();
        Bundle args = new Bundle();
        args.putInt(FixedWords.NUMBER_OF_PLAYER, FixedWords.NUMBER_OF_LINEUP_NORMAL);
        fragment.setArguments(args);
        return fragment;
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
