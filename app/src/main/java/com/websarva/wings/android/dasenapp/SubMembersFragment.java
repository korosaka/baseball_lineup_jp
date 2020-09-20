package com.websarva.wings.android.dasenapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SubMembersFragment extends BaseMembersFragment {


    public static SubMembersFragment newInstance(int orderType) {
        return (SubMembersFragment) bundleOrderType(new SubMembersFragment(), orderType);
    }

    @Override
    View inflateView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_sub_members, container, false);
        playerList = view.findViewById(R.id.sub_player_list);
        return view;
    }

    @Override
    void setListAdapter() {
        listAdapter =
                new SubPlayerListAdapter(
                        getContext(),
                        R.layout.sub_player_list_item,
                        CachedPlayersInfo.instance.getSubMembers(orderType),
                        (MakingOrderActivity) getActivity()
                );
    }

    @Override
    public void updatePlayerListView() {
        super.updatePlayerListView();
        setListViewHeightBasedOnChildren(playerList);
    }

}