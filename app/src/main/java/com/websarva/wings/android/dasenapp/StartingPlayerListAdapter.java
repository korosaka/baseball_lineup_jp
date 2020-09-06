package com.websarva.wings.android.dasenapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class StartingPlayerListAdapter extends ArrayAdapter<StartingPlayerListItemData> {

    private List<StartingPlayerListItemData> playerItems;
    private int mResource;
    private LayoutInflater mInflater;
    private StartingPlayerListAdapterListener mListener;

    public StartingPlayerListAdapter(Context context, int resource, List<StartingPlayerListItemData> items, StartingPlayerListAdapterListener listener) {
        super(context, resource, items);

        playerItems = items;
        mResource = resource;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate(mResource, null);
        }

        Button orderButton = view.findViewById(R.id.order_button);
        TextView positionText = view.findViewById(R.id.position_text);
        TextView nameText = view.findViewById(R.id.name_text);

        preparePlayerItemView(playerItems.get(position), orderButton, positionText, nameText);

        return view;
    }

    private void preparePlayerItemView(
            StartingPlayerListItemData playerItem, Button orderButton, TextView positionText, TextView nameText) {
        int orderNum = playerItem.getItemOrderNumber();

        if (orderNum == FixedWords.DH_PITCHER_ORDER) {
            orderButton.setText(FixedWords.PITCHER_INITIAL);
            mListener.setDhPitcherButton(orderButton);
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

        positionText.setText(playerItem.getItemPosition());
        nameText.setText(customNameSpace(playerItem.getItemName()));
        changeTextSize(nameText);
    }

    private void changeTextSize(TextView textView) {
        int lengthOfText = textView.length();
        int textSize;
        switch (lengthOfText) {
            case 6:
                textSize = 24;
                break;
            case 7:
                textSize = 20;
                break;
            default:
                textSize = 28;
                break;
        }
        textView.setTextSize(textSize);
    }

    private String customNameSpace(String playerName) {
        switch (playerName.length()) {
            case 2:
                return playerName.charAt(0) + FixedWords.SPACE + FixedWords.SPACE + FixedWords.SPACE + playerName.charAt(1);
            case 3:
                return playerName.charAt(0) + FixedWords.SPACE + playerName.charAt(1) + FixedWords.SPACE + playerName.charAt(2);
            default:
                return playerName;
        }
    }

}

interface StartingPlayerListAdapterListener {
    void onClickStartingOrderNum(int orderNum, Button numButton);
    void setDhPitcherButton(Button pitcherButton);
}
