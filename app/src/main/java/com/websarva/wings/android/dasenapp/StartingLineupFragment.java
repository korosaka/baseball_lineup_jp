package com.websarva.wings.android.dasenapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

// TODO remake like sub ? at least make parent class
public class StartingLineupFragment extends Fragment {

    protected ListView playerList;
    protected StartingPlayerListAdapter listAdapter;
    protected List<StartingPlayerListItemData> players;
    // TODO after ++ -- (for special rule)
    protected int numberOfPlayer;
    protected int orderType;

    public static StartingLineupFragment newInstance(int orderType) {
        StartingLineupFragment fragment = new StartingLineupFragment();
        Bundle args = new Bundle();
        args.putInt(FixedWords.ORDER_TYPE, orderType);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) orderType = getArguments().getInt(FixedWords.ORDER_TYPE);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_starting_lineup, container, false);
        playerList = view.findViewById(R.id.starting_player_list);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        players = new ArrayList<>();
        for (int oderNumber = 1; oderNumber <= numberOfPlayer; oderNumber++) {
            StartingPlayerListItemData playerItem =
                    new StartingPlayerListItemData(
                            oderNumber,
                            CachedPlayersInfo.instance.getPositionFromCache(orderType, oderNumber),
                            CachedPlayersInfo.instance.getNameFromCache(orderType, oderNumber));
            players.add(playerItem);
        }
        listAdapter =
                new StartingPlayerListAdapter(
                        getContext(),
                        R.layout.starting_player_list_item,
                        players,
                        (MakingOrderActivity) getActivity());
        playerList.setAdapter(listAdapter);
        setListViewHeightBasedOnChildren(playerList);
    }

    public void updatePlayerListView(int orderNum, String name, String position) {
        StartingPlayerListItemData newPlayerItem =
                new StartingPlayerListItemData(orderNum, position, name);
        players.set(convertOrderNumToListIndex(orderNum), newPlayerItem);
        listAdapter.notifyDataSetChanged();
    }

    private int convertOrderNumToListIndex(int orderNum) {
        return orderNum - 1;
    }

    /**
     * when using listView in a scrollView, only one item would be showed.
     * So, this function is needed to show every item.
     * reference
     * https://www.it-swarm.dev/ja/android/listview%E3%82%92%E6%8A%98%E3%82%8A%E3%81%9F%E3%81%9F%E3%81%BE%E3%81%9A%E3%81%ABscrollview%E3%81%AB%E9%85%8D%E7%BD%AE%E3%81%99%E3%82%8B%E6%96%B9%E6%B3%95/969129306/
     */
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}