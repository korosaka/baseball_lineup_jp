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
    private int orderType;

    public StartingPlayerListAdapter(
            Context context,
            int resource,
            List<? extends BasePlayerListItemData> items,
            StartingPlayerListAdapterListener listener,
            int orderType) {
        super(context, resource, (List<BasePlayerListItemData>) items);

        this.playerItems = (List<StartingPlayerListItemData>) items;
        this.mListener = listener;
        this.orderType = orderType;
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

        if (isDhPitcher(orderNum)) {
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

    private boolean isDhPitcher(int orderNum) {
        return (orderType == FixedWords.DH_ORDER) && (orderNum == FixedWords.DH_PITCHER_ORDER);
    }

}

interface StartingPlayerListAdapterListener {
    void onClickStartingOrderNum(int orderNum, Button numButton);
}
