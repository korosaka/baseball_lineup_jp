package com.websarva.wings.android.dasenapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

abstract class BasePlayerListAdapter extends ArrayAdapter<BasePlayerListItemData> {

    protected int mResource;
    protected LayoutInflater mInflater;

    public BasePlayerListAdapter(Context context, int resource, List<BasePlayerListItemData> items) {
        super(context, resource, items);

        mResource = resource;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflateView(convertView);
        customView(position, view);
        return view;
    }

    abstract void customView(int position, View view);

    protected View inflateView(View convertView) {
        if (convertView != null) return convertView;
        return mInflater.inflate(mResource, null);
    }

    protected void changeTextSize(TextView textView) {
        final int lengthOfText = textView.length();
        final int defaultTextSize = 28;
        final int defaultTextLength = 5;
        int textSize = defaultTextSize;
        if (lengthOfText > defaultTextLength) {
            textSize = (defaultTextSize*defaultTextLength) / lengthOfText;
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
