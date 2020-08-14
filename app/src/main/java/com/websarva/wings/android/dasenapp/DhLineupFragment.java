package com.websarva.wings.android.dasenapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class DhLineupFragment extends Fragment {

    private TextView[] names = new TextView[10];
    private TextView[] positions = new TextView[9];
    private Button[] number_buttons = new Button[10];

    public static DhLineupFragment newInstance() {
        return new DhLineupFragment();
    }
    // レイアウト紐付け
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_dh_lineup, container, false);
        bindLayout(v);
        setLayout();

        return v;
    }


    private void bindLayout(View v) {

        names[0] = v.findViewById(R.id.name1_dh);
        names[1] = v.findViewById(R.id.name2_dh);
        names[2] = v.findViewById(R.id.name3_dh);
        names[3] = v.findViewById(R.id.name4_dh);
        names[4] = v.findViewById(R.id.name5_dh);
        names[5] = v.findViewById(R.id.name6_dh);
        names[6] = v.findViewById(R.id.name7_dh);
        names[7] = v.findViewById(R.id.name8_dh);
        names[8] = v.findViewById(R.id.name9_dh);
        names[9] = v.findViewById(R.id.nameP_dh);


        positions[0] = v.findViewById(R.id.position1_dh);
        positions[1] = v.findViewById(R.id.position2_dh);
        positions[2] = v.findViewById(R.id.position3_dh);
        positions[3] = v.findViewById(R.id.position4_dh);
        positions[4] = v.findViewById(R.id.position5_dh);
        positions[5] = v.findViewById(R.id.position6_dh);
        positions[6] = v.findViewById(R.id.position7_dh);
        positions[7] = v.findViewById(R.id.position8_dh);
        positions[8] = v.findViewById(R.id.position9_dh);

        number_buttons[0] = v.findViewById(R.id.btn1_dh);
        number_buttons[1] = v.findViewById(R.id.btn2_dh);
        number_buttons[2] = v.findViewById(R.id.btn3_dh);
        number_buttons[3] = v.findViewById(R.id.btn4_dh);
        number_buttons[4] = v.findViewById(R.id.btn5_dh);
        number_buttons[5] = v.findViewById(R.id.btn6_dh);
        number_buttons[6] = v.findViewById(R.id.btn7_dh);
        number_buttons[7] = v.findViewById(R.id.btn8_dh);
        number_buttons[8] = v.findViewById(R.id.btn9_dh);
        number_buttons[9] = v.findViewById(R.id.btnP_dh);
    }

    private void setLayout() {
        for (int i = 0; i < 10; i++) {
            names[i].setText(CachedPlayerNamesInfo.instance.getNameDh(i));
            changeTextSize(names[i]);
            if (i == 9) return;
            positions[i].setText(CachedPlayerPositionsInfo.instance.getPositionDh(i));
        }
    }

    public void changeData(int num, String name, String position) {
        names[num].setText(name);
        changeTextSize(names[num]);
        if (num == 9) return;
        positions[num].setText(position);
    }

    public void changeButtonColor(int num) {
        number_buttons[num].setTextColor(Color.parseColor("#FF0000"));
    }

    public void setButtonDefault(int num) {
        number_buttons[num].setTextColor(Color.parseColor("#000000"));
    }

    public void setPitcherButtonEnable(boolean enable) {
        number_buttons[9].setEnabled(enable);
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
}
