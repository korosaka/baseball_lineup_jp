package com.websarva.wings.android.dasenapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentTransaction;

/**
 * To use this Activity's method in PlayerListAdapter, implementing PlayerListAdapterListener
 */
public class MainActivity extends BaseAdActivity implements PlayerListAdapterListener {
    //選択した打順
    TextView tvSelectNum;
    //入力欄
    public EditText etName;
    //登録ボタン
    Button record;
    //    キャンセルボタン
    Button cancel;
    // 入れ替えボタン
    Button replace;
    // 入れ替え中フラグ
    Boolean isReplacing = false;
    // １つ目入れ替え選択フラグ
    Boolean isFirstReplaceClicked = false;
    //スタメンタイトル
    TextView title;
    //グローバル変数i（データベースへの登録・検索で使う）
    int currentNum = 0;
    //スピナーオブジェクト
    Spinner spinner;
    //クリアボタン（現在上部に入力中のものを未入力状態に戻す（選択打順も））
    Button clear;

    int firstClickedOrderNum = -1;
    private Button firstClickedButton;
    private DatabaseUsing databaseUsing;
    private NormalLineupFragment normalLineupFragment;
    private DhLineupFragment dhLineupFragment;
    private Button dhPitcherButton;


    //ここからmain
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        setAdView(findViewById(R.id.ad_view_container_on_order));
        super.onCreate(savedInstanceState);

        databaseUsing = new DatabaseUsing(this);
        for (int orderType = FixedWords.NORMAL_ORDER; orderType <= FixedWords.DH_ORDER; orderType++) {
            databaseUsing.getPlayersInfo(orderType);
        }

        bindLayout();
        setEdit();
        setOrderFragment();
        if (!PrivacyPolicyFragment.isPolicyAgreed(this)) showPrivacyPolicy();
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
        normalLineupFragment = NormalLineupFragment.newInstance();
        dhLineupFragment = DhLineupFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.lineup_container, normalLineupFragment);
        transaction.add(R.id.lineup_container, dhLineupFragment);
        transaction.show(normalLineupFragment);
        transaction.hide(dhLineupFragment);
        transaction.commit();
    }

    private void showPrivacyPolicy() {
        PrivacyPolicyFragment policyFragment = PrivacyPolicyFragment.newInstance(FixedWords.AGREE);
        policyFragment.show(getSupportFragmentManager(), FixedWords.PRIVACY_POLICY);
    }

    private void replaceMethod(int orderNum, Button numButton) {
        // 入れ替え時
        if (!isFirstReplaceClicked) {
            // 1つめ選択時
            selectFirstReplacing(orderNum, numButton);
        } else {
            // 2つめ選択時
            if (orderNum == firstClickedOrderNum) {
                // 同じボタンがクリックされた →　元に戻す
                cancelFirstClick(numButton);
            } else {
                // 異なるボタン →入れ替え処理
                // DB/Layout内で入れ替え
                replacing2players(firstClickedOrderNum, orderNum);
                cancelReplacing();
                setLayoutDefault();
            }
        }
    }

    private void selectFirstReplacing(int orderNum, Button numButton) {
        firstClickedButton = numButton;
        changeButtonColor(numButton);
        firstClickedOrderNum = orderNum;
        isFirstReplaceClicked = true;
    }

    private void cancelFirstClick(Button numButton) {
        setButtonDefault(numButton);
        isFirstReplaceClicked = false;
        firstClickedOrderNum = -1;
    }

    public void replacing2players(int firstSelectedOrderNum, int secondSelectedOrderNum) {

        int firstIndexForCached = firstSelectedOrderNum - 1;
        int secondIndexForCached = secondSelectedOrderNum - 1;

        // 最初に選択した選手のところに後から選択した選手を上書き
        databaseUsing.registerInfo(firstSelectedOrderNum,
                CachedPlayerNamesInfo.instance.getAppropriateName(secondIndexForCached),
                CachedPlayerPositionsInfo.instance.getAppropriatePosition(secondIndexForCached));

        // 後に選択した選手の場所に最初の選手を登録
        databaseUsing.registerInfo(secondSelectedOrderNum,
                CachedPlayerNamesInfo.instance.getAppropriateName(firstIndexForCached),
                CachedPlayerPositionsInfo.instance.getAppropriatePosition(firstIndexForCached));

        // キャッシュデータもデータベースの内容に合わせる(入れ替え後のデータに更新する)
        databaseUsing.getDatabaseInfo(CurrentOrderVersion.instance.getCurrentVersion(), firstSelectedOrderNum);
        databaseUsing.getDatabaseInfo(CurrentOrderVersion.instance.getCurrentVersion(), secondSelectedOrderNum);

        // TextViewも更新
        changeText(firstSelectedOrderNum, secondSelectedOrderNum);
    }

    private void changeText(int firstSelectedOrderNum, int secondSelectedOrderNum) {

        int firstIndexForCached = firstSelectedOrderNum - 1;
        int secondIndexForCached = secondSelectedOrderNum - 1;

        switch (CurrentOrderVersion.instance.getCurrentVersion()) {
            case FixedWords.NORMAL_ORDER:
                normalLineupFragment.changeData(firstIndexForCached,
                        CachedPlayerNamesInfo.instance.getAppropriateName(firstIndexForCached),
                        CachedPlayerPositionsInfo.instance.getAppropriatePosition(firstIndexForCached));
                normalLineupFragment.changeData(secondIndexForCached,
                        CachedPlayerNamesInfo.instance.getAppropriateName(secondIndexForCached),
                        CachedPlayerPositionsInfo.instance.getAppropriatePosition(secondIndexForCached));
                break;
            case FixedWords.DH_ORDER:
                dhLineupFragment.changeData(firstIndexForCached,
                        CachedPlayerNamesInfo.instance.getAppropriateName(firstIndexForCached),
                        CachedPlayerPositionsInfo.instance.getAppropriatePosition(firstIndexForCached));
                dhLineupFragment.changeData(secondIndexForCached,
                        CachedPlayerNamesInfo.instance.getAppropriateName(secondIndexForCached),
                        CachedPlayerPositionsInfo.instance.getAppropriatePosition(secondIndexForCached));
                break;
        }

    }

    private void selectNum(int orderNum) {

        int orderIndex = orderNum - 1;
        readyInputtingName(orderNum,
                CachedPlayerPositionsInfo.instance.getAppropriatePosition(orderIndex),
                CachedPlayerNamesInfo.instance.getAppropriateName(orderIndex));
        currentNum = orderNum;
    }

    //文字列からスピナーをセットするメソッド（上記メソッドで使用）
    private void setSpinner(Spinner spinner, String position) {
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

    private void readyInputtingName(int orderNum, String position, String name) {
        spinner.setEnabled(true);
        tvSelectNum.setText((orderNum + FixedWords.JP_NUMBER));
        //下記メソッド使用
        setSpinner(spinner, position);
        etName.setText(name);
        if (etName.getText().toString().equals("-----")) etName.setText("");
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

        // DH制の投手の場合のみ対応
        if (CurrentOrderVersion.instance.getCurrentVersion() == FixedWords.DH_ORDER &&
                orderNum == FixedWords.DH_PITCHER_ORDER) {
            tvSelectNum.setText(FixedWords.PITCHER_INITIAL);
            setSpinner(spinner, FixedWords.HYPHEN_4);
            spinner.setEnabled(false);
        }
    }

    //登録ボタン押した処理
    public void onClickSave(View view) {
        //入力文字列取得
        String playerName = etName.getText().toString();
        if (playerName.equals(FixedWords.EMPTY)) playerName = FixedWords.HYPHEN_5;
        //ポジション取得
        String position = (String) spinner.getSelectedItem();

        databaseUsing.registerInfo(currentNum, playerName, position);

        int indexForCache = currentNum - 1;

        switch (CurrentOrderVersion.instance.getCurrentVersion()) {
            //画面のメンバー表に反映（１〜９番まで）
            case FixedWords.NORMAL_ORDER:
                CachedPlayerNamesInfo.instance.setNameNormal(indexForCache, playerName);
                CachedPlayerPositionsInfo.instance.setPositionNormal(indexForCache, position);
                normalLineupFragment.changeData(indexForCache, playerName, position);
                break;
            case FixedWords.DH_ORDER:
                if (currentNum == FixedWords.DH_PITCHER_ORDER) position = FixedWords.PITCHER;
                CachedPlayerNamesInfo.instance.setNameDh(indexForCache, playerName);
                CachedPlayerPositionsInfo.instance.setPositionDh(indexForCache, position);
                dhLineupFragment.changeData(indexForCache, playerName, position);
                break;
        }

        setLayoutDefault();
    }

    private void setLayoutDefault() {
        //それぞれ初期状態に戻す
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

    //クリアボタン処理
    public void onClickClear(View view) {
        //入力名をクリア状態に
        etName.setText("");
        //スピナー（守備位置）を未選択状態に戻す
        spinner.setSelection(0);
    }

    //    キャンセルボタン処理
    public void onClickCancel(View view) {

        // 入れ替えボタンクリック時のキャンセルor入力中のキャンセル？
        if (isReplacing) cancelReplacing();
        //それぞれ初期状態に戻す
        setLayoutDefault();
    }

    // 入れ替えボタン処理
    public void onClickReplace(View view) {

        if (CurrentOrderVersion.instance.getCurrentVersion() == FixedWords.DH_ORDER)
            setPitcherButtonEnable(false);
        // 入れ替えクリックされているフラグ
        isReplacing = true;
        // 入れ替えボタンはenable(false)に
        replace.setEnabled(false);
        replace.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        // キャンセルはできるように
        cancel.setEnabled(true);
        cancel.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.cancel_button_background, null));
        // タイトルが『２つボタンクリック』になる
        title.setText(R.string.replace_title);
        title.setTextColor(Color.parseColor(FixedWords.COLOR_EMPHASIZING));

    }

    public void onClickField(View view) {
        startActivity(new Intent(MainActivity.this, FieldActivity.class));

        setLayoutDefault();
        if (isReplacing) cancelReplacing();
    }

    public void onClickShareOrder(View view) {
        Sharing mSharing = new Sharing(getApplicationContext(), this, findViewById(R.id.lineup_container));
        mSharing.share();
    }


    //オプションメニュー追加
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //メニューインフレター取得
        MenuInflater inflater = getMenuInflater();
        //オプションメニュー用.xmlファイルをインフレート（メニュー部品をJavaオブジェクトに）
        inflater.inflate(R.menu.menu_options_menu_list, menu);
        //親クラスの同名メソッドで返却
        return super.onCreateOptionsMenu(menu);
    }

    //オプションメニューを選択した時の処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //IDのR値による処理分岐
        switch (item.getItemId()) {
            case R.id.oder:
                showOrder(FixedWords.NORMAL_ORDER);
                setSpinner(getResources().getStringArray(R.array.positions));
                break;
            //DHの場合
            case R.id.dh:
                showOrder(FixedWords.DH_ORDER);
                setSpinner(getResources().getStringArray(R.array.positions_dh));
                break;
            case R.id.policy:
                PrivacyPolicyFragment policyFragment = PrivacyPolicyFragment.newInstance(FixedWords.CLOSE);
                policyFragment.show(getSupportFragmentManager(), FixedWords.PRIVACY_POLICY);
                break;
        }
        setLayoutDefault();
        if (isReplacing) cancelReplacing();

        //親クラス同名メソッドで戻り値返却
        return super.onOptionsItemSelected(item);
    }

    /**
     * 入れ替え処理中ならリセット
     */
    private void cancelReplacing() {
        if (isFirstReplaceClicked) cancelFirstClick(firstClickedButton);
        isReplacing = false;
        title.setText(R.string.title);
        title.setTextColor(Color.parseColor(FixedWords.COLOR_WHITE));
        replace.setEnabled(true);
        replace.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.replace_button_background, null));
        cancel.setEnabled(false);
        cancel.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        if (CurrentOrderVersion.instance.getCurrentVersion() == FixedWords.DH_ORDER)
            setPitcherButtonEnable(true);
    }

    private void setPitcherButtonEnable(boolean enable) {
        dhPitcherButton.setEnabled(enable);
        int backgroundId = R.drawable.order_num_button_background;
        if (!enable) backgroundId = R.drawable.disable_button_background;
        dhPitcherButton.setBackground(ResourcesCompat.getDrawable(getResources(), backgroundId, null));
    }


    private void changeButtonColor(Button numButton) {
        switch (CurrentOrderVersion.instance.getCurrentVersion()) {
            case FixedWords.NORMAL_ORDER:
                normalLineupFragment.highLightButton(numButton);
                break;
            case FixedWords.DH_ORDER:
                dhLineupFragment.highLightButton(numButton);
                break;
        }
    }

    private void setButtonDefault(Button numButton) {
        switch (CurrentOrderVersion.instance.getCurrentVersion()) {
            case FixedWords.NORMAL_ORDER:
                normalLineupFragment.setButtonDefault(numButton);
                break;
            case FixedWords.DH_ORDER:
                dhLineupFragment.setButtonDefault(numButton);
                break;
        }
    }

    private void showOrder(int orderType) {

        CurrentOrderVersion.instance.setCurrentVersion(orderType);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                transaction.hide(dhLineupFragment);
                transaction.show(normalLineupFragment);
                break;
            case FixedWords.DH_ORDER:
                transaction.hide(normalLineupFragment);
                transaction.show(dhLineupFragment);
                break;
        }
        transaction.commit();
    }

    private void setSpinner(String[] spinnerResource) {
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
        finishApp();
    }

    private void finishApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_CustomButtonDialog);
        builder.setMessage(getResources().getString(R.string.ask_finish_app));
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), null);
        builder.show();
    }
}
