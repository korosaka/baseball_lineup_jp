package com.websarva.wings.android.dasenapp;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class StartingPlayerListAdapter extends BasePlayerListAdapter {

    private List<StartingPlayerListItemData> playerItems;
    private StartingPlayerListAdapterListener mListener;

    public StartingPlayerListAdapter(Context context, int resource, List<? extends BasePlayerListItemData> items, StartingPlayerListAdapterListener listener) {
        super(context, resource, (List<BasePlayerListItemData>) items);

        playerItems = (List<StartingPlayerListItemData>) items;
        mListener = listener;
    }

    @Override
    void customView(int position, View view) {
        Button orderButton = view.findViewById(R.id.order_button);
        TextView positionText = view.findViewById(R.id.position_text);
        TextView nameText = view.findViewById(R.id.name_text);

        preparePlayerItemView(playerItems.get(position), orderButton, positionText, nameText);
    }

    private void preparePlayerItemView(
            StartingPlayerListItemData playerItem, Button orderButton, TextView positionText, TextView nameText) {
        int orderNum = playerItem.getOrderNum();

        if (orderNum == FixedWords.DH_PITCHER_ORDER) {
            orderButton.setText(FixedWords.PITCHER_INITIAL);
            positionText.setTextColor(Color.parseColor(FixedWords.COLOR_PITCHER_TEXT));
        } else {
            String orderNumJP = orderNum + FixedWords.JP_NUMBER;
            orderButton.setText(orderNumJP);
        }
        orderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onClickStartingOrderNum(orderNum, orderButton);
            }
        });

        positionText.setText(playerItem.getPosition());
        nameText.setText(customNameSpace(playerItem.getName()));
        changeTextSize(nameText);
    }

}

interface StartingPlayerListAdapterListener {
    void onClickStartingOrderNum(int orderNum, Button numButton);
}
