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
import androidx.core.content.res.ResourcesCompat;

import java.util.List;

public class SubPlayerListAdapter extends ArrayAdapter<SubPlayerListItemData> {

    private Context mContext;
    private List<SubPlayerListItemData> playerItems;
    private int mResource;
    private LayoutInflater mInflater;
    private SubPlayerListAdapterListener mListener;

    public SubPlayerListAdapter(Context context, int resource, List<SubPlayerListItemData> items, SubPlayerListAdapterListener listener) {
        super(context, resource, items);

        mContext = context;
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

        Button orderButton = view.findViewById(R.id.sub_order_button);
        TextView pitcherLabel = view.findViewById(R.id.pitcher_label);
        TextView batterLabel = view.findViewById(R.id.batter_label);
        TextView runnerLabel = view.findViewById(R.id.runner_label);
        TextView fielderLabel = view.findViewById(R.id.fielder_label);
        TextView nameText = view.findViewById(R.id.sub_name_text);
        SubPlayerListItemData playerItem = playerItems.get(position);

        highLightRoleLabels(playerItem, pitcherLabel, batterLabel, runnerLabel, fielderLabel);
        orderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onClickSubOrderNum(position, playerItem, orderButton);
            }
        });

        nameText.setText(customNameSpace(playerItem.getName()));
        changeTextSize(nameText);

        return view;
    }

    private void highLightRoleLabels(
            SubPlayerListItemData playerItem,
            TextView pitcherLabel,
            TextView batterLabel,
            TextView runnerLabel,
            TextView fielderLabel) {

        if (playerItem.getPitcher()) setRoleOn(pitcherLabel, FixedWords.ROLE_PITCHER);
        else setRoleOff(pitcherLabel);
        if (playerItem.getBatter()) setRoleOn(batterLabel, FixedWords.ROLE_BATTER);
        else setRoleOff(batterLabel);
        if (playerItem.getRunner()) setRoleOn(runnerLabel, FixedWords.ROLE_RUNNER);
        else setRoleOff(runnerLabel);
        if (playerItem.getFielder()) setRoleOn(fielderLabel, FixedWords.ROLE_FIELDER);
        else setRoleOff(fielderLabel);
    }

    private void setRoleOn(TextView roleLabel, String role) {
        roleLabel.setTextColor(Color.parseColor(FixedWords.COLOR_BLACK));
        switch (role) {
            case FixedWords.ROLE_PITCHER:
                roleLabel.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.role_pitcher_on_background, null));
                break;
            case FixedWords.ROLE_BATTER:
                roleLabel.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.role_batter_on_background, null));
                break;
            case FixedWords.ROLE_RUNNER:
                roleLabel.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.role_runner_on_background, null));
                break;
            case FixedWords.ROLE_FIELDER:
                roleLabel.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.role_fielder_on_background, null));
                break;
        }
    }

    private void setRoleOff(TextView roleLabel) {
        roleLabel.setTextColor(Color.parseColor(FixedWords.COLOR_OFF_WHITE));
        roleLabel.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.role_off_background, null));
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

interface SubPlayerListAdapterListener {
    void onClickSubOrderNum(int listPosition, SubPlayerListItemData subMember, Button numButton);
}

