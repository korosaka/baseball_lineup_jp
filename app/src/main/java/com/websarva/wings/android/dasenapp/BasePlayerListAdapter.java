package com.websarva.wings.android.dasenapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

abstract class BasePlayerListAdapter extends ArrayAdapter<BasePlayerListItemData> {

    protected int mResource;
    protected LayoutInflater mInflater;

    public BasePlayerListAdapter(Context context, int resource, List<BasePlayerListItemData> items) {
        super(context, resource, items);

        mResource = resource;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    protected View inflateView(View convertView) {
        if (convertView != null) return convertView;
        return mInflater.inflate(mResource, null);
    }

    protected void changeTextSize(TextView textView) {
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

    protected String customNameSpace(String playerName) {
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
