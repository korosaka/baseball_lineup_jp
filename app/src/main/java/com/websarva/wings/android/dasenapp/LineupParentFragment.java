package com.websarva.wings.android.dasenapp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

abstract public class LineupParentFragment extends Fragment {

    protected ListView playerList;
    protected PlayerListAdapter listAdapter;
    protected List<PlayerListItemData> players;
    // TODO after ++ -- (for special rule)
    protected int numberOfPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            numberOfPlayer = getArguments().getInt(FixedWords.NUMBER_OF_PLAYER);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        players = new ArrayList<>();
        for (int index = 0; index < numberOfPlayer; index++) {
            int oderNumber = index + 1;
            PlayerListItemData playerItem =
                    new PlayerListItemData(
                            oderNumber,
                            getPositionFromCache(index),
                            getNameFromCache(index));
            players.add(playerItem);
        }
        listAdapter =
                new PlayerListAdapter(
                        getContext(),
                        R.layout.player_list_item,
                        players,
                        (MainActivity) getActivity());
        playerList.setAdapter(listAdapter);
        setListViewHeightBasedOnChildren(playerList);
    }

    abstract String getPositionFromCache(int index);

    abstract String getNameFromCache(int index);

    public void changeData(int index, String name, String position) {
        PlayerListItemData newPlayerItem =
                new PlayerListItemData(index + 1, position, name);
        players.set(index, newPlayerItem);
        listAdapter.notifyDataSetChanged();
    }

    public void highLightButton(Button button) {
        button.setTextColor(Color.parseColor(FixedWords.COLOR_WHITE));
        button.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.emphasized_button_background, null));
    }

    public void setButtonDefault(Button button) {
        button.setTextColor(Color.parseColor(FixedWords.COLOR_BLACK));
        button.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.order_num_button_background, null));
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