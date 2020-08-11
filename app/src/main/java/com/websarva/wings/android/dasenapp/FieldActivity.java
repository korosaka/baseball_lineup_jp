package com.websarva.wings.android.dasenapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class FieldActivity extends BaseBannerActivity {
    //各ポジションのテキスト
    private TextView position1;
    private TextView position2;
    private TextView position3;
    private TextView position4;
    private TextView position5;
    private TextView position6;
    private TextView position7;
    private TextView position8;
    private TextView position9;
    private TextView[] dh = new TextView[6];

    private int playerNumber = 0;
    private int maxDh = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_field);
        setAdView(findViewById(R.id.ad_view_container_on_field));
        super.onCreate(savedInstanceState);

        bindLayout();
        setPlayerCount();
        hideDh();
        setPlayers();
    }

    //戻るボタン
    public void onClickBack(View view) {
        finish();
    }

    private void bindLayout() {
        //ポジション紐付け
        position1 = findViewById(R.id.pitcher);
        position2 = findViewById(R.id.catcher);
        position3 = findViewById(R.id.first);
        position4 = findViewById(R.id.second);
        position5 = findViewById(R.id.third);
        position6 = findViewById(R.id.shortStop);
        position7 = findViewById(R.id.left);
        position8 = findViewById(R.id.center);
        position9 = findViewById(R.id.right);
        dh[0] = findViewById(R.id.dh1);
        dh[1] = findViewById(R.id.dh2);
        dh[2] = findViewById(R.id.dh3);
        dh[3] = findViewById(R.id.dh4);
        dh[4] = findViewById(R.id.dh5);
        dh[5] = findViewById(R.id.dh6);
    }

    private void hideDh() {
        for (int i = 5; i >= maxDh; i--) {
            dh[i].setVisibility(View.INVISIBLE);
        }
    }

    private void setPlayerCount() {
        switch (CurrentOrderVersion.instance.getCurrentVersion()) {
            case FixedWords.DEFAULT:
                playerNumber = 9;
                break;
            case FixedWords.DH:
                playerNumber = 10;
                maxDh = 1;
                break;
            case FixedWords.ALL10:
                playerNumber = 10;
                maxDh = 1;
                break;
            case FixedWords.ALL11:
                playerNumber = 11;
                maxDh = 2;
                break;
            case FixedWords.ALL12:
                playerNumber = 12;
                maxDh = 3;
                break;
            case FixedWords.ALL13:
                playerNumber = 13;
                maxDh = 4;
                break;
            case FixedWords.ALL14:
                playerNumber = 14;
                maxDh = 5;
                break;
            case FixedWords.ALL15:
                playerNumber = 15;
                maxDh = 6;
                break;
        }
    }

    private void setPlayers() {
        int dhCount = 0;
        //ある打順の守備位置dataがどこかのポジションと合致すれば、その打順登録名を守備フィールドに
        for (int i = 0; i < playerNumber; i++) {
            switch (CachedPlayerPositionsInfo.instance.getAppropriatePosition(i)) {
                case "(投)":
                    if (CurrentOrderVersion.instance.getCurrentVersion() == FixedWords.DH)
                        setText(position1, i, true);
                    else
                        setText(position1, i, false);
                    break;
                case "(捕)":
                    setText(position2, i, false);
                    break;
                case "(一)":
                    setText(position3, i, false);
                    break;
                case "(二)":
                    setText(position4, i, false);
                    break;
                case "(三)":
                    setText(position5, i, false);
                    break;
                case "(遊)":
                    setText(position6, i, false);
                    break;
                case "(左)":
                    setText(position7, i, false);
                    break;
                case "(中)":
                    setText(position8, i, false);
                    break;
                case "(右)":
                    setText(position9, i, false);
                    break;
                case "(DH)":
                    if (dhCount >= maxDh) dhCount = 0;
                    setText(dh[dhCount], i, false);
                    dhCount++;
                    break;
                default:
                    break;
            }
        }
    }

    private void setText(TextView textView, int num, boolean dhPitcher) {
        String playerName = CachedPlayerNamesInfo.instance.getAppropriateName(num);

        if (dhPitcher) textView.setText(playerName + " (P)");
        else textView.setText(playerName + " (" + (num + 1) + ")");

        if (playerName.length() > 5) textView.setTextSize(14);
        else textView.setTextSize(16);
    }
}
