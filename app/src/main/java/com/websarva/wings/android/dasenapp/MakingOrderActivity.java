package com.websarva.wings.android.dasenapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentTransaction;

/**
 * To use this Activity's method in PlayerListAdapter, implementing PlayerListAdapterListener
 */
public class MakingOrderActivity extends BaseAdActivity implements PlayerListAdapterListener {
    private TextView tvSelectNum;
    private EditText etName;
    private Button record;
    private Button cancel;
    private Button replace;
    private Boolean isReplacing = false;
    private Boolean isFirstReplaceClicked = false;
    private TextView title;
    private int currentNum = 0;
    private Spinner spinner;
    private Button clear;

    private int firstClickedOrderNum = -1;
    private Button firstClickedButton;
    private DatabaseUsing databaseUsing;
    private StartingLineupFragment lineupFragment;
    private Button dhPitcherButton;
    private int orderType;


    //ここからmain
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_making_order);
        setAdView(findViewById(R.id.ad_view_container_on_order));
        super.onCreate(savedInstanceState);

        orderType = getIntent().getIntExtra(FixedWords.ORDER_TYPE, FixedWords.NORMAL_ORDER);
        databaseUsing = new DatabaseUsing(this);
        databaseUsing.getPlayersInfo(orderType);
        bindLayout();
        setEdit();
        setOrderFragment();
        setPositionsSpinner();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadBanner();
    }


    private void bindLayout() {
        //上記のグローバルフィールド紐付け
        tvSelectNum = findViewById(R.id.selectNum);
        etName = findViewById(R.id.etName);
        record = findViewById(R.id.record);
        cancel = findViewById(R.id.cancel);
        replace = findViewById(R.id.replace);
        clear = findViewById(R.id.clear);
        spinner = findViewById(R.id.position);
        title = findViewById(R.id.title);
    }

    private void setEdit() {
        //EditText入力不可に
        etName.setFocusable(false);
        etName.setFocusableInTouchMode(false);
        etName.setEnabled(false);
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean flag) {
//                フォーカスを取得→キーボード表示
                // TODO refactor
                if (flag) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(view, 0);
                }
//                フォーカス外れる→キーボード非表示
                else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    }

    private void setOrderFragment() {
        lineupFragment = StartingLineupFragment.newInstance(orderType);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.lineup_container, lineupFragment);
        transaction.commit();
    }

    private void setPositionsSpinner() {
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                setSpinnerResource(getResources().getStringArray(R.array.positions));
                break;
            case FixedWords.DH_ORDER:
                setSpinnerResource(getResources().getStringArray(R.array.positions_dh));
                break;
        }
    }

    private void replaceMethod(int orderNum, Button numButton) {
        if (!isFirstReplaceClicked) {
            // 1つめ選択時
            selectFirstReplacing(orderNum, numButton);
        } else {
            // 2つめ選択時
            if (orderNum == firstClickedOrderNum) {
                cancelFirstClick(numButton);
            } else {
                replacing2players(firstClickedOrderNum, orderNum);
                cancelReplacing();
                setLayoutDefault();
            }
        }
    }

    private void selectFirstReplacing(int orderNum, Button numButton) {
        firstClickedButton = numButton;
        lineupFragment.highLightButton(numButton);
        firstClickedOrderNum = orderNum;
        isFirstReplaceClicked = true;
    }

    private void cancelFirstClick(Button numButton) {
        lineupFragment.setButtonDefault(numButton);
        isFirstReplaceClicked = false;
        firstClickedOrderNum = -1;
    }

    public void replacing2players(int firstSelectedOrderNum, int secondSelectedOrderNum) {

        // 最初に選択した選手のところに後から選択した選手を上書き
        databaseUsing.registerInfo(firstSelectedOrderNum,
                CachedPlayersInfo.instance.getNameFromCache(orderType, secondSelectedOrderNum),
                CachedPlayersInfo.instance.getPositionFromCache(orderType, secondSelectedOrderNum),
                orderType);

        // 後に選択した選手の場所に最初の選手を登録
        databaseUsing.registerInfo(secondSelectedOrderNum,
                CachedPlayersInfo.instance.getNameFromCache(orderType, firstSelectedOrderNum),
                CachedPlayersInfo.instance.getPositionFromCache(orderType, firstSelectedOrderNum),
                orderType);

        // キャッシュデータもデータベースの内容に合わせる(入れ替え後のデータに更新する)
        databaseUsing.getDatabaseInfo(orderType, firstSelectedOrderNum);
        databaseUsing.getDatabaseInfo(orderType, secondSelectedOrderNum);

        replaceListView(firstSelectedOrderNum, secondSelectedOrderNum);
    }

    private void replaceListView(int firstSelectedOrderNum, int secondSelectedOrderNum) {
        lineupFragment.updatePlayerListView(firstSelectedOrderNum,
                CachedPlayersInfo.instance.getNameFromCache(orderType, firstSelectedOrderNum),
                CachedPlayersInfo.instance.getPositionFromCache(orderType, firstSelectedOrderNum));
        lineupFragment.updatePlayerListView(secondSelectedOrderNum,
                CachedPlayersInfo.instance.getNameFromCache(orderType, secondSelectedOrderNum),
                CachedPlayersInfo.instance.getPositionFromCache(orderType, secondSelectedOrderNum));
    }

    private void selectNum(int orderNum) {
        readyInputtingName(orderNum,
                CachedPlayersInfo.instance.getPositionFromCache(orderType, orderNum),
                CachedPlayersInfo.instance.getNameFromCache(orderType, orderNum));
        currentNum = orderNum;
    }

    private void selectSpinnerItem(Spinner spinner, String position) {
        SpinnerAdapter adapter = spinner.getAdapter();
        int index = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(position)) {
                index = i;
                break;
            }
        }
        spinner.setSelection(index);
    }

    // TODO refactor ?
    private void readyInputtingName(int orderNum, String position, String name) {
        spinner.setEnabled(true);
        tvSelectNum.setText((orderNum + FixedWords.JP_NUMBER));
        selectSpinnerItem(spinner, position);
        etName.setText(name);
        if (etName.getText().toString().equals(FixedWords.HYPHEN_5)) etName.setText(FixedWords.EMPTY);
        etName.setEnabled(true);
        etName.setFocusable(true);
        etName.setFocusableInTouchMode(true);
        etName.requestFocus();
        record.setEnabled(true);
        record.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.record_button_background, null));
        cancel.setEnabled(true);
        cancel.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.cancel_button_background, null));
        clear.setEnabled(true);
        clear.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.clear_button_background, null));
        replace.setEnabled(false);
        replace.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));

        if (orderType == FixedWords.DH_ORDER &&
                orderNum == FixedWords.DH_PITCHER_ORDER) {
            tvSelectNum.setText(FixedWords.PITCHER_INITIAL);
            selectSpinnerItem(spinner, FixedWords.HYPHEN_4);
            spinner.setEnabled(false);
        }
    }

    public void onClickSave(View view) {
        String playerName = etName.getText().toString();
        if (playerName.equals(FixedWords.EMPTY)) playerName = FixedWords.HYPHEN_5;
        String position = (String) spinner.getSelectedItem();
        // TODO kakunin
        if (currentNum == FixedWords.DH_PITCHER_ORDER) position = FixedWords.PITCHER;
        databaseUsing.registerInfo(currentNum, playerName, position, orderType);
        CachedPlayersInfo.instance.setPlayerInfoToCache(orderType, currentNum, position, playerName);
        lineupFragment.updatePlayerListView(currentNum, playerName, position);
        setLayoutDefault();
    }

    private void setLayoutDefault() {
        tvSelectNum.setText(getString(R.string.hyphen_4));
        etName.setText(FixedWords.EMPTY);
        spinner.setSelection(0);
        etName.setFocusable(false);
        etName.setFocusableInTouchMode(false);
        etName.setEnabled(false);
        record.setEnabled(false);
        record.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        cancel.setEnabled(false);
        cancel.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        clear.setEnabled(false);
        clear.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        replace.setEnabled(true);
        replace.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.replace_button_background, null));
    }

    public void onClickClear(View view) {
        etName.setText(FixedWords.EMPTY);
        spinner.setSelection(0);
    }

    public void onClickCancel(View view) {
        if (isReplacing) cancelReplacing();
        setLayoutDefault();
    }

    public void onClickReplace(View view) {
        if (orderType == FixedWords.DH_ORDER) setPitcherButtonEnable(false);
        isReplacing = true;
        replace.setEnabled(false);
        replace.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        cancel.setEnabled(true);
        cancel.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.cancel_button_background, null));
        title.setText(R.string.replace_title);
        title.setTextColor(Color.parseColor(FixedWords.COLOR_EMPHASIZING));
    }

    public void onClickField(View view) {
        Intent intent = new Intent(MakingOrderActivity.this, FieldActivity.class);
        intent.putExtra(FixedWords.ORDER_TYPE, orderType);
        startActivity(intent);

        setLayoutDefault();
        if (isReplacing) cancelReplacing();
    }

    public void onClickBackToTop(View view) {
        finish();
    }

    public void onClickShareOrder(View view) {
        Sharing mSharing = new Sharing(getApplicationContext(), this, findViewById(R.id.lineup_container));
        mSharing.share();
    }


    private void cancelReplacing() {
        if (isFirstReplaceClicked) cancelFirstClick(firstClickedButton);
        isReplacing = false;
        title.setText(R.string.title);
        title.setTextColor(Color.parseColor(FixedWords.COLOR_WHITE));
        replace.setEnabled(true);
        replace.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.replace_button_background, null));
        cancel.setEnabled(false);
        cancel.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        if (orderType == FixedWords.DH_ORDER) setPitcherButtonEnable(true);
    }

    private void setPitcherButtonEnable(boolean enable) {
        dhPitcherButton.setEnabled(enable);
        int backgroundId = R.drawable.order_num_button_background;
        if (!enable) backgroundId = R.drawable.disable_button_background;
        dhPitcherButton.setBackground(ResourcesCompat.getDrawable(getResources(), backgroundId, null));
    }

    // TODO may be needed for function of sub members
//    private void showOrder(int orderType) {
//
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        switch (orderType) {
//            case FixedWords.NORMAL_ORDER:
//                transaction.hide(dhLineupFragment);
//                transaction.show(normalLineupFragment);
//                break;
//            case FixedWords.DH_ORDER:
//                transaction.hide(normalLineupFragment);
//                transaction.show(dhLineupFragment);
//                break;
//        }
//        transaction.commit();
//    }

    private void setSpinnerResource(String[] spinnerResource) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerResource);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onClickOrderNum(int orderNum, Button numButton) {
        if (isReplacing) {
            replaceMethod(orderNum, numButton);
        } else {
            selectNum(orderNum);
        }
    }

    /**
     * for replacing on DH order (disable pitcher button)
     */
    @Override
    public void setDhPitcherButton(Button pitcherButton) {
        this.dhPitcherButton = pitcherButton;
    }

    @Override
    void keyBackFunction() {
        finish();
    }

}
