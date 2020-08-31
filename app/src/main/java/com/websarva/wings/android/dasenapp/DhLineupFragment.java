package com.websarva.wings.android.dasenapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class DhLineupFragment extends Fragment {

    private ListView playerList;
    private PlayerListAdapter listAdapter;
    private List<PlayerListItemData> players;
    public static Button pitcherButton;

    public static DhLineupFragment newInstance() {
        return new DhLineupFragment();
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        players = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int oderNumber = i + 1;
            PlayerListItemData playerItem = new PlayerListItemData(
                    oderNumber,
                    CachedPlayerPositionsInfo.instance.getPositionDh(i),
                    CachedPlayerNamesInfo.instance.getNameDh(i));
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


    public void changeData(int num, String name, String position) {
        PlayerListItemData newPlayerItem =
                new PlayerListItemData(num + 1, position, name);
        players.set(num, newPlayerItem);
        listAdapter.notifyDataSetChanged();
    }

    public void changeButtonColor(Button button) {
        button.setTextColor(Color.parseColor("#FF0000"));
    }

    public void setButtonDefault(Button button) {
        button.setTextColor(Color.parseColor("#000000"));
    }

    public void setPitcherButtonEnable(boolean enable) {
        pitcherButton.setEnabled(enable);
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
