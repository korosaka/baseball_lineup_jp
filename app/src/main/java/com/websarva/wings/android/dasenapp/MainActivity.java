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

    int firstClicked = -1;
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
        for (int version = 1; version < 3; version++) {
            databaseUsing.getPlayersInfo(version);
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

    private void replaceMethod(int j, Button numButton) {
        // 入れ替え時
        if (!isFirstReplaceClicked) {
            // 1つめ選択時
            selectFirstReplacing(j, numButton);
        } else {
            // 2つめ選択時
            if (j == firstClicked) {
                // 同じボタンがクリックされた →　元に戻す
                cancelFirstClick(numButton);
            } else {
                // 異なるボタン →入れ替え処理
                // DB/Layout内で入れ替え
                replacing2players(firstClicked, j);
                cancelReplacing();
                setLayoutDefault();
            }
        }
    }

    private void selectFirstReplacing(int num, Button numButton) {
        firstClickedButton = numButton;
        changeButtonColor(numButton);
        firstClicked = num;
        isFirstReplaceClicked = true;
    }

    private void cancelFirstClick(Button numButton) {
        setButtonDefault(numButton);
        isFirstReplaceClicked = false;
        firstClicked = -1;
    }

    public void replacing2players(int firstSelected, int secondSelected) {

        // 最初に選択した選手のところに後から選択した選手を上書き
        databaseUsing.setDatabaseInfo(firstSelected, CachedPlayerNamesInfo.instance.getAppropriateName(secondSelected)
                , CachedPlayerPositionsInfo.instance.getAppropriatePosition(secondSelected));

        // 後に選択した選手の場所に最初の選手を登録
        databaseUsing.setDatabaseInfo(secondSelected, CachedPlayerNamesInfo.instance.getAppropriateName(firstSelected)
                , CachedPlayerPositionsInfo.instance.getAppropriatePosition(firstSelected));

        // キャッシュデータもデータベースの内容に合わせる(入れ替え後のデータに更新する)
        databaseUsing.getDatabaseInfo(CurrentOrderVersion.instance.getCurrentVersion(), firstSelected);
        databaseUsing.getDatabaseInfo(CurrentOrderVersion.instance.getCurrentVersion(), secondSelected);

        // TextViewも更新
        changeText(firstSelected, secondSelected);
    }

    private void changeText(int firstSelected, int secondSelected) {
        switch (CurrentOrderVersion.instance.getCurrentVersion()) {
            case FixedWords.DEFAULT:
                normalLineupFragment.changeData(firstSelected, CachedPlayerNamesInfo.instance.getAppropriateName(firstSelected)
                        , CachedPlayerPositionsInfo.instance.getAppropriatePosition(firstSelected));
                normalLineupFragment.changeData(secondSelected, CachedPlayerNamesInfo.instance.getAppropriateName(secondSelected)
                        , CachedPlayerPositionsInfo.instance.getAppropriatePosition(secondSelected));
                break;
            case FixedWords.DH:
                dhLineupFragment.changeData(firstSelected, CachedPlayerNamesInfo.instance.getAppropriateName(firstSelected)
                        , CachedPlayerPositionsInfo.instance.getAppropriatePosition(firstSelected));
                dhLineupFragment.changeData(secondSelected, CachedPlayerNamesInfo.instance.getAppropriateName(secondSelected)
                        , CachedPlayerPositionsInfo.instance.getAppropriatePosition(secondSelected));
                break;
            case FixedWords.ALL10:
                break;
            case FixedWords.ALL11:
                break;
            case FixedWords.ALL12:
                break;
            case FixedWords.ALL13:
                break;
            case FixedWords.ALL14:
                break;
            case FixedWords.ALL15:
                break;
        }

    }

    private void selectNum(int num) {

        readyInputtingName(num, CachedPlayerPositionsInfo.instance.getAppropriatePosition(num)
                , CachedPlayerNamesInfo.instance.getAppropriateName(num));
        currentNum = num;
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

    private void readyInputtingName(int num, String position, String name) {
        spinner.setEnabled(true);
        //numbersは表示打順のためkを反映させない
        String number = String.valueOf(num + 1) + "番";
        tvSelectNum.setText(number);
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
        if (CurrentOrderVersion.instance.getCurrentVersion() == FixedWords.DH && num == 9) {
            tvSelectNum.setText("P");
            setSpinner(spinner, "----");
            spinner.setEnabled(false);
        }
    }

    //登録ボタン押した処理
    public void onClickSave(View view) {
        //入力文字列取得
        String playerName = etName.getText().toString();
        if (playerName.equals("")) playerName = "-----";
        //ポジション取得
        String position = (String) spinner.getSelectedItem();

        databaseUsing.setDatabaseInfo(currentNum, playerName, position);

        switch (CurrentOrderVersion.instance.getCurrentVersion()) {
            //画面のメンバー表に反映（１〜９番まで）
            case FixedWords.DEFAULT:
                CachedPlayerNamesInfo.instance.setNameNormal(currentNum, playerName);
                CachedPlayerPositionsInfo.instance.setPositionNormal(currentNum, position);
                normalLineupFragment.changeData(currentNum, playerName, position);
                break;
            case FixedWords.DH:
                if ((currentNum + 1) == FixedWords.DH_PITCHER_ORDER) position = FixedWords.PITCHER;
                CachedPlayerNamesInfo.instance.setNameDh(currentNum, playerName);
                CachedPlayerPositionsInfo.instance.setPositionDh(currentNum, position);
                dhLineupFragment.changeData(currentNum, playerName, position);
                break;
        }

        setLayoutDefault();
    }

    private void setLayoutDefault() {
        //それぞれ初期状態に戻す
        tvSelectNum.setText(getString(R.string.current_num));
        etName.setText("");
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

        if (CurrentOrderVersion.instance.getCurrentVersion() == FixedWords.DH)
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
                showOrder(FixedWords.DEFAULT);
                setSpinner(getResources().getStringArray(R.array.positions));
                break;
            //DHの場合
            case R.id.dh:
                showOrder(FixedWords.DH);
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
        if (CurrentOrderVersion.instance.getCurrentVersion() == FixedWords.DH)
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
            case FixedWords.DEFAULT:
                normalLineupFragment.changeButtonColor(numButton);
                break;
            case FixedWords.DH:
                dhLineupFragment.changeButtonColor(numButton);
                break;
            case FixedWords.ALL10:
                break;
            case FixedWords.ALL11:
                break;
            case FixedWords.ALL12:
                break;
            case FixedWords.ALL13:
                break;
            case FixedWords.ALL14:
                break;
            case FixedWords.ALL15:
                break;
        }
    }

    private void setButtonDefault(Button numButton) {
        switch (CurrentOrderVersion.instance.getCurrentVersion()) {
            case FixedWords.DEFAULT:
                normalLineupFragment.setButtonDefault(numButton);
                break;
            case FixedWords.DH:
                dhLineupFragment.setButtonDefault(numButton);
                break;
            case FixedWords.ALL10:
                break;
            case FixedWords.ALL11:
                break;
            case FixedWords.ALL12:
                break;
            case FixedWords.ALL13:
                break;
            case FixedWords.ALL14:
                break;
            case FixedWords.ALL15:
                break;
        }
    }

    private void showOrder(int orderVersion) {

        CurrentOrderVersion.instance.setCurrentVersion(orderVersion);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (orderVersion) {
            case FixedWords.DEFAULT:
                transaction.hide(dhLineupFragment);
                transaction.show(normalLineupFragment);
                break;
            case FixedWords.DH:
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
        int index = orderNum - 1;
        if (isReplacing) {
            replaceMethod(index, numButton);
        } else {
            selectNum(index);
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
