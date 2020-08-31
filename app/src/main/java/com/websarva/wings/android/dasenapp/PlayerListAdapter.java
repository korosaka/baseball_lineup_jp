package com.websarva.wings.android.dasenapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class PlayerListAdapter extends ArrayAdapter<PlayerListItemData> {

    private List<PlayerListItemData> playerItems;
    private int mResource;
    private LayoutInflater mInflater;
    private PlayerListAdapterListener mListener;

    public PlayerListAdapter(Context context, int resource, List<PlayerListItemData> items, PlayerListAdapterListener listener) {
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

        PlayerListItemData playerItem = playerItems.get(position);
        int orderNum = playerItem.getItemOrderNumber();
        if (orderNum == 10) {
            orderButton.setText("P");
            DhLineupFragment.pitcherButton = orderButton;
        } else {
            orderButton.setText(orderNum + "番");
        }
        orderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onClickOrderNum(playerItem.getItemOrderNumber(), orderButton);
            }
        });

        positionText.setText(playerItem.getItemPosition());
        nameText.setText(customNameSpace(playerItem.getItemName()));
        changeTextSize(nameText);

        return view;
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

interface PlayerListAdapterListener {
    void onClickOrderNum(int orderNum, Button numButton);
}
