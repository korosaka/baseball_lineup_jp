package com.websarva.wings.android.dasenapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class FieldActivity extends BaseAdActivity {
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
    private TextView orderPitcher;
    private TextView orderCatcher;
    private TextView orderFirst;
    private TextView orderSecond;
    private TextView orderThird;
    private TextView orderShort;
    private TextView orderLeft;
    private TextView orderCenter;
    private TextView orderRight;
    private TextView[] orderDh = new TextView[6];

    private int playerNumber = 0;
    private int maxDh = 0;
    private static int displayCount = 0;
    private static final int FIRST_TIME = 1;
    private static final int INTERSTITIAL_AD_FREQUENCY = 5;
    private int orderType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_field);
        setAdView(findViewById(R.id.ad_view_container_on_field));
        super.onCreate(savedInstanceState);

        orderType = getIntent().getIntExtra(FixedWords.ORDER_TYPE, FixedWords.NORMAL_ORDER);
        prepareInterstitialAd();
        bindLayout();
        setPlayerCount();
        hideDh();
        setPlayers();
    }

    private void prepareInterstitialAd() {
        displayCount++;
        if (shouldShowInterstitial()) loadInterstitialAd();
    }

    //戻るボタン
    public void onClickBack(View view) {
        backToOrder();
    }

    public void onClickShareField(View view) {
        Sharing mSharing = new Sharing(getApplicationContext(), this, findViewById(R.id.field_container));
        mSharing.share();
    }

    @Override
    void keyBackFunction() {
        backToOrder();
    }

    private void backToOrder() {
        if (shouldShowInterstitial()) showInterstitialAd();
        else finish();
    }

    private Boolean shouldShowInterstitial() {
        return (displayCount == FIRST_TIME) || (displayCount % INTERSTITIAL_AD_FREQUENCY == 0);
    }

    private void bindLayout() {
        //ポジション紐付け
        position1 = findViewById(R.id.pitcher);
        position2 = findViewById(R.id.catcher);
        position3 = findViewById(R.id.first);
        position4 = findViewById(R.id.second);
        position5 = findViewById(R.id.third);
        position6 = findViewById(R.id.short_stop);
        position7 = findViewById(R.id.left);
        position8 = findViewById(R.id.center);
        position9 = findViewById(R.id.right);
        dh[0] = findViewById(R.id.dh1);
        dh[1] = findViewById(R.id.dh2);
        dh[2] = findViewById(R.id.dh3);
        dh[3] = findViewById(R.id.dh4);
        dh[4] = findViewById(R.id.dh5);
        dh[5] = findViewById(R.id.dh6);
        orderPitcher = findViewById(R.id.pitcher_order);
        orderCatcher = findViewById(R.id.catcher_order);
        orderFirst = findViewById(R.id.first_order);
        orderSecond = findViewById(R.id.second_order);
        orderThird = findViewById(R.id.third_order);
        orderShort = findViewById(R.id.short_stop_order);
        orderLeft = findViewById(R.id.left_order);
        orderCenter = findViewById(R.id.center_order);
        orderRight = findViewById(R.id.right_order);
        orderDh[0] = findViewById(R.id.dh1_order);
        orderDh[1] = findViewById(R.id.dh2_order);
        orderDh[2] = findViewById(R.id.dh3_order);
        orderDh[3] = findViewById(R.id.dh4_order);
        orderDh[4] = findViewById(R.id.dh5_order);
        orderDh[5] = findViewById(R.id.dh6_order);
    }

    private void hideDh() {
        for (int i = 5; i >= maxDh; i--) {
            dh[i].setVisibility(View.INVISIBLE);
            orderDh[i].setVisibility(View.INVISIBLE);
        }
    }

    private void setPlayerCount() {
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                playerNumber = FixedWords.NUMBER_OF_LINEUP_NORMAL;
                break;
            case FixedWords.DH_ORDER:
                playerNumber = FixedWords.NUMBER_OF_LINEUP_DH;
                maxDh = 1;
                break;
            case FixedWords.SPECIAL_ORDER:
                playerNumber = CachedPlayersInfo.instance.getCurrentNumOfSpecialLineupDB();
                maxDh = playerNumber - FixedWords.MIN_NUM_SPECIAL_PLAYER;
                break;
        }
    }

    private void setPlayers() {
        int dhCount = 0;
        //ある打順の守備位置dataがどこかのポジションと合致すれば、その打順登録名を守備フィールドに
        for (int orderNum = 1; orderNum <= playerNumber; orderNum++) {
            switch (CachedPlayersInfo.instance.getStartingMember(orderType, orderNum).getPosition()) {
                case FixedWords.PITCHER:
                    if (orderType == FixedWords.DH_ORDER)
                        setText(position1, orderPitcher, orderNum, true);
                    else
                        setText(position1, orderPitcher, orderNum, false);
                    break;
                case FixedWords.CATCHER:
                    setText(position2, orderCatcher, orderNum, false);
                    break;
                case FixedWords.FIRST_BASE:
                    setText(position3, orderFirst, orderNum, false);
                    break;
                case FixedWords.SECOND_BASE:
                    setText(position4, orderSecond, orderNum, false);
                    break;
                case FixedWords.THIRD_BASE:
                    setText(position5, orderThird, orderNum, false);
                    break;
                case FixedWords.SHORT_STOP:
                    setText(position6, orderShort, orderNum, false);
                    break;
                case FixedWords.LEFT:
                    setText(position7, orderLeft, orderNum, false);
                    break;
                case FixedWords.CENTER:
                    setText(position8, orderCenter, orderNum, false);
                    break;
                case FixedWords.RIGHT:
                    setText(position9, orderRight, orderNum, false);
                    break;
                case FixedWords.DH:
                    if (dhCount >= maxDh) dhCount = 0;
                    setText(dh[dhCount], orderDh[dhCount], orderNum, false);
                    dhCount++;
                    break;
                default:
                    break;
            }
        }
    }

    private void setText(TextView name, TextView order, int orderNum, boolean dhPitcher) {
        String playerName =
                customNameSpace(CachedPlayersInfo.instance.getStartingMember(orderType, orderNum).getName());
        name.setText(playerName);
        if (dhPitcher) order.setText(("[" + FixedWords.PITCHER_INITIAL + "]"));
        else order.setText(("[" + orderNum + "]"));


        int textSize;
        switch (playerName.length()) {
            case 6:
                textSize = 14;
                break;
            case 7:
                textSize = 12;
                break;
            default:
                textSize = 16;
                break;
        }
        name.setTextSize(textSize);
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
