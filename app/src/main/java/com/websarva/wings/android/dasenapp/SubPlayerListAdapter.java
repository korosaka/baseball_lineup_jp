package com.websarva.wings.android.dasenapp;

import android.content.Context;
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
        if (playerItem.getPitcher())
            pitcherLabel.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.pitcher_name_background, null));
        if (playerItem.getBatter())
            batterLabel.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.outfielder_name_background, null));
        if (playerItem.getRunner())
            runnerLabel.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.catcher_name_background, null));
        if (playerItem.getFielder())
            fielderLabel.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.infielder_name_background, null));

        orderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onClickSubOrderNum(playerItem, orderButton);
            }
        });

        nameText.setText(customNameSpace(playerItem.getName()));
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

interface SubPlayerListAdapterListener {
    void onClickSubOrderNum(SubPlayerListItemData subMember, Button numButton);
}

