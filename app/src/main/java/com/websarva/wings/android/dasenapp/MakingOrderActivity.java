package com.websarva.wings.android.dasenapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

/**
 * To use this Activity's method in PlayerListAdapter, implementing PlayerListAdapterListener
 */
public class MakingOrderActivity extends BaseAdActivity implements StartingPlayerListAdapterListener, SubPlayerListAdapterListener {
    private TextView tvSelectNum;
    private EditText etName;
    private LinearLayout rolesBox;
    private Button addSub;
    private Button deleteSub;
    private Button addStarting;
    private Button deleteStarting;
    private Button rolePitcher;
    private Button roleBatter;
    private Button roleRunner;
    private Button roleFielder;
    private TextView subLabel;
    private Button orderSwitch;
    private Button record;
    private Button cancel;
    private Button exchange;
    private Boolean isExchanging = false;
    private Boolean isDeleting = false;
    private Boolean isFirstExchangeClicked = false;
    private String firstExchangeClickedOrder;
    private TextView title;
    private int currentStartingNum;
    private int currentSubListIndex;
    private int currentSubId;
    private Spinner spinner;
    private Button clear;

    private int firstClickedNum;
    private Button firstClickedButton;
    private DatabaseUsing databaseUsing;
    private StartingLineupFragment lineupFragment;
    private SubMembersFragment subMembersFragment;
    private int orderType;
    private String showingOrder;

    private boolean isRolePitcher;
    private boolean isRoleBatter;
    private boolean isRoleRunner;
    private boolean isRoleFielder;

    private boolean isReviewRequested = false;

    //ここからmain
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_making_order);
        setAdView(findViewById(R.id.ad_view_container_on_order));
        super.onCreate(savedInstanceState);

        orderType = getIntent().getIntExtra(FixedWords.ORDER_TYPE, FixedWords.NORMAL_ORDER);
        databaseUsing = new DatabaseUsing(this);
        databaseUsing.getPlayersFromDB(orderType);
        bindLayout();
        setEdit();
        setOrderFragment();
        setPositionsSpinner();
        if (!isIntroductionRead()) showIntroductionDialog();
        countAppUse();
    }

    private void countAppUse() {
        int openCount = new MySharedPreferences(this).getInt(FixedWords.NUMBER_OF_OPEN_APP);
        if (openCount < 1) openCount = 0;
        new MySharedPreferences(this).storeInt(openCount + 1, FixedWords.NUMBER_OF_OPEN_APP);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadBanner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldRequestReview()) requestReview();
    }

    private boolean shouldRequestReview() {
        if (isReviewRequested) return false;
        int requestFrequencyUnder50 = 10;
        int requestFrequencyUnder100 = 20;
        int requestFrequencyUnder200 = 30;
        int requestFrequencyUnder500 = 50;
        int requestFrequencyOver500 = 70;
        int appUseCount = new MySharedPreferences(this).getInt(FixedWords.NUMBER_OF_OPEN_APP);
        if (appUseCount < 1) appUseCount = 1;
        if (appUseCount < 50) {
            return appUseCount % requestFrequencyUnder50 == 0;
        } else if (appUseCount < 100) {
            return appUseCount % requestFrequencyUnder100 == 0;
        } else if (appUseCount < 200) {
            return appUseCount % requestFrequencyUnder200 == 0;
        } else if (appUseCount < 500) {
            return appUseCount % requestFrequencyUnder500 == 0;
        } else {
            return appUseCount % requestFrequencyOver500 == 0;
        }
    }

    //ref: https://developer.android.com/guide/playcore/in-app-review/kotlin-java?hl=ja#java
    private void requestReview() {
        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(reviewInfoTask -> {
            if (reviewInfoTask.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = reviewInfoTask.getResult();
                Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(taskOfLaunch -> {
                    isReviewRequested = true;
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            }
        });
    }

    private void bindLayout() {
        tvSelectNum = findViewById(R.id.selectNum);
        etName = findViewById(R.id.etName);
        rolesBox = findViewById(R.id.role_box_for_sub);
        addSub = findViewById(R.id.add_sub_button);
        deleteSub = findViewById(R.id.delete_sub_button);
        addStarting = findViewById(R.id.add_special_button);
        deleteStarting = findViewById(R.id.delete_special_button);
        rolePitcher = findViewById(R.id.role_pitcher_button);
        roleBatter = findViewById(R.id.role_batter_button);
        roleRunner = findViewById(R.id.role_runner_button);
        roleFielder = findViewById(R.id.role_fielder_button);
        subLabel = findViewById(R.id.sub);
        orderSwitch = findViewById(R.id.order_switch_button);
        record = findViewById(R.id.record);
        cancel = findViewById(R.id.cancel);
        exchange = findViewById(R.id.exchange);
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
                // get focus → display keyboard
                // lose focus → dismiss keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (flag) imm.showSoftInput(view, 0);
                else imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private void setOrderFragment() {
        lineupFragment = StartingLineupFragment.newInstance(orderType);
        subMembersFragment = SubMembersFragment.newInstance(orderType);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.lineup_container, lineupFragment);
        transaction.add(R.id.lineup_container, subMembersFragment);
        transaction.commit();
        showStartingOrder();
    }

    private void setPositionsSpinner() {
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                setSpinnerResource(getResources().getStringArray(R.array.positions_normal));
                break;
            case FixedWords.DH_ORDER:
                setSpinnerResource(getResources().getStringArray(R.array.positions_dh));
                break;
            case FixedWords.SPECIAL_ORDER:
                setSpinnerForSpecial();
                break;
        }
    }

    private void setSpinnerForSpecial() {
        if (CachedPlayersInfo.instance.getStartingMembers(FixedWords.SPECIAL_ORDER).size() == FixedWords.NUMBER_OF_LINEUP_NORMAL) {
            setSpinnerResource(getResources().getStringArray(R.array.positions_normal));
            return;
        }
        setSpinnerResource(getResources().getStringArray(R.array.positions_all));
    }

    private void checkSelectedStartingPlayer(int orderNum, Button numButton) {
        if (!isFirstExchangeClicked) {
            // 1つめ選択時
            selectFirstExchanged(orderNum, numButton);
        } else {
            if (firstExchangeClickedOrder.equals(FixedWords.SUB_MEMBERS)) {
                exchangeStartingAndSubPlayers(firstClickedNum, orderNum);
            } else if (orderNum == firstClickedNum) {
                cancelFirstClick(numButton);
            } else {
                exchangeStartingPlayers(firstClickedNum, orderNum);
                cancelExchanging();
                setLayoutDefault();
            }
        }
    }

    private void checkSelectedSubPlayer(int clickedNum, Button clickedButton) {
        if (!isFirstExchangeClicked) {
            selectFirstExchanged(clickedNum, clickedButton);
        } else {
            if (firstExchangeClickedOrder.equals(FixedWords.Starting_ORDER)) {
                exchangeStartingAndSubPlayers(clickedNum, firstClickedNum);
            } else if (clickedNum == firstClickedNum) {
                cancelFirstClick(clickedButton);
            } else {
                exchangeSubPlayers(firstClickedNum, clickedNum);
                databaseUsing.putSubPlayersInCache(orderType);
                subMembersFragment.updatePlayerListView();
                cancelExchanging();
                setLayoutDefault();
            }
        }
    }

    private void exchangeStartingAndSubPlayers(int subNum, int startingNum) {
        StartingPlayerListItemData selectedStartingPlayer =
                CachedPlayersInfo.instance.getStartingMember(orderType, startingNum);
        SubPlayerListItemData selectedSubPlayer =
                CachedPlayersInfo.instance.getSubMembers(orderType).get(subNum);
        // into starting DB
        databaseUsing.registerStartingPlayer(
                startingNum,
                selectedSubPlayer.getName(),
                selectedStartingPlayer.getPosition(),
                orderType);
        // into sub DB
        databaseUsing.updateSubPlayer(
                orderType,
                selectedSubPlayer.getId(),
                selectedSubPlayer.getPitcher(),
                selectedSubPlayer.getBatter(),
                selectedSubPlayer.getRunner(),
                selectedSubPlayer.getFielder(),
                selectedStartingPlayer.getName()
        );

        databaseUsing.putStartingPlayersInCache(orderType, startingNum);
        lineupFragment.updatePlayerListView();
        databaseUsing.putSubPlayersInCache(orderType);
        subMembersFragment.updatePlayerListView();

        cancelExchanging();
        setLayoutDefault();
    }


    private void selectFirstExchanged(int orderNum, Button playerButton) {
        firstClickedNum = orderNum;
        firstClickedButton = playerButton;
        isFirstExchangeClicked = true;
        firstExchangeClickedOrder = showingOrder;
        highLightButton(playerButton);
    }

    private void highLightButton(Button button) {
        button.setTextColor(Color.parseColor(FixedWords.COLOR_WHITE));
        button.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.emphasized_button_background, null));
    }

    private void cancelFirstClick(Button numButton) {
        setButtonDefault(numButton);
        isFirstExchangeClicked = false;
        firstExchangeClickedOrder = null;
        firstClickedButton = null;
        firstClickedNum = FixedWords.NON_SELECTED;
    }

    private void setButtonDefault(Button button) {
        button.setTextColor(Color.parseColor(FixedWords.COLOR_BLACK));
        button.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.order_num_button_background, null));
    }

    private void exchangeSubPlayers(int firstSelectedOrderNum, int secondSelectedOrderNum) {
        databaseUsing.exchangeSubPlayers(
                orderType,
                CachedPlayersInfo.instance.getSubMembers(orderType).get(firstSelectedOrderNum),
                CachedPlayersInfo.instance.getSubMembers(orderType).get(secondSelectedOrderNum));
    }


    private void exchangeStartingPlayers(int firstSelectedOrderNum, int secondSelectedOrderNum) {

        StartingPlayerListItemData playerFirst =
                CachedPlayersInfo.instance.getStartingMember(orderType, firstSelectedOrderNum);
        StartingPlayerListItemData playerSecond =
                CachedPlayersInfo.instance.getStartingMember(orderType, secondSelectedOrderNum);

        if (isContainingDhPitcher(firstSelectedOrderNum, secondSelectedOrderNum)) {
            // only name will be exchanged
            databaseUsing.registerStartingPlayer(
                    firstSelectedOrderNum,
                    playerSecond.getName(),
                    playerFirst.getPosition(),
                    orderType);
            databaseUsing.registerStartingPlayer(
                    secondSelectedOrderNum,
                    playerFirst.getName(),
                    playerSecond.getPosition(),
                    orderType);
        } else {
            // 最初に選択した選手のところに後から選択した選手を上書き
            databaseUsing.registerStartingPlayer(
                    firstSelectedOrderNum,
                    playerSecond.getName(),
                    playerSecond.getPosition(),
                    orderType);
            // 後に選択した選手の場所に最初の選手を登録
            databaseUsing.registerStartingPlayer(
                    secondSelectedOrderNum,
                    playerFirst.getName(),
                    playerFirst.getPosition(),
                    orderType);
        }

        // キャッシュデータもデータベースの内容に合わせる(入れ替え後のデータに更新する)
        databaseUsing.putStartingPlayersInCache(orderType, firstSelectedOrderNum);
        databaseUsing.putStartingPlayersInCache(orderType, secondSelectedOrderNum);

        lineupFragment.updatePlayerListView();
    }

    private boolean isContainingDhPitcher(int num1, int num2) {
        return (orderType == FixedWords.DH_ORDER) &&
                (num1 == FixedWords.NUMBER_OF_LINEUP_DH || num2 == FixedWords.NUMBER_OF_LINEUP_DH);
    }

    private void selectNum(int orderNum) {
        StartingPlayerListItemData selectedPlayer =
                CachedPlayersInfo.instance.getStartingMember(orderType, orderNum);

        readyInputtingStartingPlayer(orderNum,
                selectedPlayer.getPosition(),
                selectedPlayer.getName()
        );
        currentStartingNum = orderNum;
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

    private void readyInputtingStartingPlayer(int orderNum, String position, String name) {
        requireInputtingPlayer(name);
        spinner.setEnabled(true);
        tvSelectNum.setText((orderNum + FixedWords.JP_NUMBER));
        selectSpinnerItem(spinner, position);
        if (orderType == FixedWords.DH_ORDER &&
                orderNum == FixedWords.DH_PITCHER_ORDER) {
            tvSelectNum.setText(FixedWords.PITCHER_INITIAL);
            selectSpinnerItem(spinner, FixedWords.EMPTY_POSITION);
            spinner.setEnabled(false);
        }
    }

    private void readyInputtingSubPlayer(SubPlayerListItemData subMember) {
        requireInputtingPlayer(subMember.getName());
        setRole(subMember.getPitcher(), FixedWords.ROLE_PITCHER);
        setRole(subMember.getBatter(), FixedWords.ROLE_BATTER);
        setRole(subMember.getRunner(), FixedWords.ROLE_RUNNER);
        setRole(subMember.getFielder(), FixedWords.ROLE_FIELDER);
    }

    private void requireInputtingPlayer(String name) {
        etName.setText(name);
        if (etName.getText().toString().equals(FixedWords.EMPTY_NAME))
            etName.setText(FixedWords.EMPTY);
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
        makeButtonDisable(exchange);
        makeButtonDisable(orderSwitch);
        if (orderType == FixedWords.SPECIAL_ORDER) {
            makeButtonDisable(addStarting);
            makeButtonDisable(deleteStarting);
        }
        if (showingOrder.equals(FixedWords.SUB_MEMBERS)) adjustViewForSub();
    }

    private void adjustViewForSub() {
        rolesBox.setVisibility(View.VISIBLE);
        makeButtonDisable(addSub);
        makeButtonDisable(deleteSub);
    }

    private void makeButtonDisable(Button button) {
        button.setEnabled(false);
        button.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
    }

    public void onClickSave(View view) {
        String playerName = etName.getText().toString();
        if (playerName.equals(FixedWords.EMPTY)) playerName = FixedWords.EMPTY_NAME;
        overWritePlayer(playerName);
        setLayoutDefault();
    }

    private boolean isDhPitcher() {
        return (orderType == FixedWords.DH_ORDER) && (currentStartingNum == FixedWords.DH_PITCHER_ORDER);
    }

    private void overWritePlayer(String playerName) {
        if (showingOrder.equals(FixedWords.Starting_ORDER)) overWriteStarting(playerName);
        else overWriteSub(playerName);
    }

    private void overWriteStarting(String playerName) {
        String position = (String) spinner.getSelectedItem();
        if (isDhPitcher()) position = FixedWords.PITCHER;
        databaseUsing.registerStartingPlayer(currentStartingNum, playerName, position, orderType);
        CachedPlayersInfo.instance.setPlayerInfoToCache(orderType, currentStartingNum, position, playerName);
        lineupFragment.updatePlayerListView();
    }

    private void overWriteSub(String playerName) {
        databaseUsing.updateSubPlayer(
                orderType, currentSubId, isRolePitcher, isRoleBatter, isRoleRunner, isRoleFielder, playerName);
        CachedPlayersInfo.instance.overwriteSubPlayer(
                orderType, currentSubListIndex, isRolePitcher, isRoleBatter, isRoleRunner, isRoleFielder, playerName);
        subMembersFragment.updatePlayerListView();
    }

    private void setLayoutDefault() {
        tvSelectNum.setText(getString(R.string.hyphen_4));
        etName.setText(FixedWords.EMPTY);
        spinner.setSelection(0);
        etName.setFocusable(false);
        etName.setFocusableInTouchMode(false);
        etName.setEnabled(false);
        makeButtonDisable(record);
        makeButtonDisable(cancel);
        makeButtonDisable(clear);
        exchange.setEnabled(true);
        exchange.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.exchange_button_background, null));
        orderSwitch.setEnabled(true);
        orderSwitch.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.switch_button_background, null));
        resetTitle();
        if (orderType == FixedWords.SPECIAL_ORDER) {
            addStarting.setEnabled(true);
            addStarting.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.add_member_button_background, null));
            deleteStarting.setEnabled(true);
            deleteStarting.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.delete_member_button_background, null));
        }

        setLayoutDefaultForSub();
    }

    private void setLayoutDefaultForSub() {
        resetRoles();
        rolesBox.setVisibility(View.GONE);
        deleteSub.setEnabled(true);
        deleteSub.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.delete_member_button_background, null));
        addSub.setEnabled(true);
        addSub.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.add_member_button_background, null));
        isDeleting = false;
    }

    public void onClickClear(View view) {
        etName.setText(FixedWords.EMPTY);
        spinner.setSelection(0);
        resetRoles();
    }

    public void onClickCancel(View view) {
        if (isExchanging) cancelExchanging();
        setLayoutDefault();
    }

    public void onClickExchange(View view) {
        isExchanging = true;
        cancel.setEnabled(true);
        cancel.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.cancel_button_background, null));
        title.setText(R.string.require_exchange_title);
        title.setTextColor(Color.parseColor(FixedWords.COLOR_EMPHASIZING));
        if (orderType == FixedWords.SPECIAL_ORDER) {
            makeButtonDisable(addStarting);
            makeButtonDisable(deleteStarting);
        }
        makeButtonDisable(exchange);
        makeButtonDisable(addSub);
        makeButtonDisable(deleteSub);
        resetRoles();
        Toast.makeText(this, R.string.require_exchange_title, Toast.LENGTH_SHORT).show();
    }

    public void onClickField(View view) {
        Intent intent = new Intent(MakingOrderActivity.this, FieldActivity.class);
        intent.putExtra(FixedWords.ORDER_TYPE, orderType);
        startActivity(intent);

        setLayoutDefault();
        if (isExchanging) cancelExchanging();
    }

    public void onClickBackToTop(View view) {
        finish();
    }

    public void onClickShareOrder(View view) {
        Toast.makeText(this, R.string.share_disabled, Toast.LENGTH_LONG).show();
//        Sharing mSharing = new Sharing(getApplicationContext(), this, findViewById(R.id.lineup_container));
//        mSharing.share();
    }


    private void cancelExchanging() {
        if (isFirstExchangeClicked) cancelFirstClick(firstClickedButton);
        isExchanging = false;
        resetTitle();
        exchange.setEnabled(true);
        exchange.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.exchange_button_background, null));
        makeButtonDisable(cancel);
    }

    public void onClickSwitchOrder(View view) {
        if (showingOrder.equals(FixedWords.Starting_ORDER)) showSubMembers();
        else showStartingOrder();
    }

    public void onClickRoleButton(View view) {
        int id = view.getId();
        if (id == R.id.role_pitcher_button) {
            setRole(!isRolePitcher, FixedWords.ROLE_PITCHER);
        } else if (id == R.id.role_batter_button) {
            setRole(!isRoleBatter, FixedWords.ROLE_BATTER);
        } else if (id == R.id.role_runner_button) {
            setRole(!isRoleRunner, FixedWords.ROLE_RUNNER);
        } else if (id == R.id.role_fielder_button) {
            setRole(!isRoleFielder, FixedWords.ROLE_FIELDER);
        }
    }

    private void resetRoles() {
        setRole(false, FixedWords.ROLE_PITCHER);
        setRole(false, FixedWords.ROLE_BATTER);
        setRole(false, FixedWords.ROLE_RUNNER);
        setRole(false, FixedWords.ROLE_FIELDER);
    }

    private void setRole(boolean isRole, String role) {
        switch (role) {
            case FixedWords.ROLE_PITCHER:
                isRolePitcher = isRole;
                if (isRolePitcher) setRoleOn(rolePitcher, role);
                else setRoleOff(rolePitcher);
                break;
            case FixedWords.ROLE_BATTER:
                isRoleBatter = isRole;
                if (isRoleBatter) setRoleOn(roleBatter, role);
                else setRoleOff(roleBatter);
                break;
            case FixedWords.ROLE_RUNNER:
                isRoleRunner = isRole;
                if (isRoleRunner) setRoleOn(roleRunner, role);
                else setRoleOff(roleRunner);
                break;
            case FixedWords.ROLE_FIELDER:
                isRoleFielder = isRole;
                if (isRoleFielder) setRoleOn(roleFielder, role);
                else setRoleOff(roleFielder);
                break;
        }
    }

    private void setRoleOn(Button roleButton, String role) {
        roleButton.setTextColor(Color.parseColor(FixedWords.COLOR_BLACK));
        switch (role) {
            case FixedWords.ROLE_PITCHER:
                rolePitcher.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.role_pitcher_on_background, null));
                break;
            case FixedWords.ROLE_BATTER:
                roleBatter.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.role_batter_on_background, null));
                break;
            case FixedWords.ROLE_RUNNER:
                roleRunner.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.role_runner_on_background, null));
                break;
            case FixedWords.ROLE_FIELDER:
                roleFielder.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.role_fielder_on_background, null));
                break;
        }
    }

    private void setRoleOff(Button roleButton) {
        roleButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.role_off_background, null));
        roleButton.setTextColor(Color.parseColor(FixedWords.COLOR_OFF_BLACK));
    }

    public void onClickAddSub(View view) {
        if (isSubLimit()) {
            Toast.makeText(this, R.string.announce_limit_sub, Toast.LENGTH_SHORT).show();
            return;
        }
        addSubMaximum();
    }

    private void addSubMaximum() {
        databaseUsing.registerSubPlayer(
                orderType, false, false, false, false, FixedWords.EMPTY_NAME);
        databaseUsing.putSubPlayersInCache(orderType);
        subMembersFragment.updatePlayerListView();
        resetTitle();
    }

    private boolean isSubLimit() {
        int limitSubPlayer = 16;
        if (orderType == FixedWords.DH_ORDER) limitSubPlayer = 15;
        return (CachedPlayersInfo.instance.getSubMembers(orderType).size() >= limitSubPlayer);
    }

    public void onClickDeleteSub(View view) {
        isDeleting = true;
        title.setText(R.string.require_delete_title);
        title.setTextColor(Color.parseColor(FixedWords.COLOR_EMPHASIZING));
        cancel.setEnabled(true);
        cancel.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.cancel_button_background, null));
        cancel.setFocusable(true);
        cancel.setFocusableInTouchMode(true);
        cancel.requestFocus();
        makeButtonDisable(addSub);
        makeButtonDisable(orderSwitch);
        makeButtonDisable(deleteSub);
        makeButtonDisable(exchange);
        Toast.makeText(this, R.string.require_delete_title, Toast.LENGTH_SHORT).show();
    }

    public void onClickAddStarting(View view) {
        if (CachedPlayersInfo.instance.getCurrentNumOfSpecialLineupDB() >= FixedWords.MAX_NUM_SPECIAL_PLAYER) {
            Toast.makeText(this, R.string.announce_max_limit_special, Toast.LENGTH_SHORT).show();
            return;
        }

        int addedOrderNum = CachedPlayersInfo.instance.getCurrentNumOfSpecialLineupDB() + 1;
        databaseUsing.registerStartingPlayer(addedOrderNum, FixedWords.EMPTY_NAME, FixedWords.EMPTY_POSITION, orderType);
        databaseUsing.countSpecialLineupPlayers();
        databaseUsing.putStartingPlayersInCache(orderType, addedOrderNum);
        lineupFragment.updatePlayerListView();
        setSpinnerForSpecial();
    }

    public void onClickDeleteStarting(View view) {
        if (CachedPlayersInfo.instance.getCurrentNumOfSpecialLineupDB() <= 9) {
            Toast.makeText(this, R.string.announce_min_limit_special, Toast.LENGTH_SHORT).show();
            return;
        }

        databaseUsing.deleteStartingPlayerOnSpecial(
                CachedPlayersInfo.instance.getCurrentNumOfSpecialLineupDB());
        databaseUsing.countSpecialLineupPlayers();
        CachedPlayersInfo.instance.deleteStartingPlayerOnSpecial();
        lineupFragment.updatePlayerListView();
        setSpinnerForSpecial();
    }

    private void showStartingOrder() {
        switchOrder(true);
        showingOrder = FixedWords.Starting_ORDER;
        tvSelectNum.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        subLabel.setVisibility(View.GONE);
        rolesBox.setVisibility(View.GONE);
        addSub.setVisibility(View.GONE);
        deleteSub.setVisibility(View.GONE);
        orderSwitch.setText(R.string.display_sub);
        if (!isExchanging) resetTitle();
        if (orderType == FixedWords.SPECIAL_ORDER) {
            addStarting.setVisibility(View.VISIBLE);
            deleteStarting.setVisibility(View.VISIBLE);
        }
    }

    private void showSubMembers() {
        switchOrder(false);
        showingOrder = FixedWords.SUB_MEMBERS;
        tvSelectNum.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        subLabel.setVisibility(View.VISIBLE);
        addSub.setVisibility(View.VISIBLE);
        deleteSub.setVisibility(View.VISIBLE);
        orderSwitch.setText(R.string.display_starting);
        if (!isExchanging) resetTitle();
        if (orderType == FixedWords.SPECIAL_ORDER) {
            addStarting.setVisibility(View.GONE);
            deleteStarting.setVisibility(View.GONE);
        }
    }

    private void resetTitle() {
        title.setTextColor(Color.parseColor(FixedWords.COLOR_WHITE));
        cancel.setFocusable(false);
        cancel.setFocusableInTouchMode(false);
        if (showingOrder.equals(FixedWords.Starting_ORDER)) title.setText(R.string.title);
        else {
            String subPlayerTitle =
                    getString(R.string.sub_title) + FixedWords.SPACE +
                            CachedPlayersInfo.instance.getSubMembers(orderType).size() + FixedWords.JP_PEOPLE;
            title.setText(subPlayerTitle);
        }
    }

    private void switchOrder(boolean isStartingLineup) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isStartingLineup) {
            transaction.hide(subMembersFragment);
            transaction.show(lineupFragment);
        } else {
            transaction.hide(lineupFragment);
            transaction.show(subMembersFragment);
        }
        transaction.commit();
    }

    private void setSpinnerResource(String[] spinnerResource) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerResource);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onClickStartingOrderNum(int orderNum, Button numButton) {
        if (isExchanging) {
            checkSelectedStartingPlayer(orderNum, numButton);
        } else {
            selectNum(orderNum);
        }
    }

    @Override
    public void onClickSubOrderNum(int listPosition, SubPlayerListItemData subMember, Button numButton) {
        if (isDeleting) deleteSubPlayer(listPosition, subMember);
        else if (isExchanging) checkSelectedSubPlayer(listPosition, numButton);
        else overWriteSubPlayer(listPosition, subMember);
    }

    private void deleteSubPlayer(int listPosition, SubPlayerListItemData subMember) {
        databaseUsing.deleteSubPlayer(orderType, subMember.getId());
        CachedPlayersInfo.instance.deleteSubPlayer(orderType, listPosition);
        subMembersFragment.updatePlayerListView();
        setLayoutDefault();
    }

    private void overWriteSubPlayer(int listPosition, SubPlayerListItemData subMember) {
        currentSubListIndex = listPosition;
        currentSubId = subMember.getId();
        readyInputtingSubPlayer(subMember);
    }

    private boolean isIntroductionRead() {
        return new MySharedPreferences(this).getBoolean(FixedWords.READ_INTRODUCTION);
    }

    private void doneReadingIntroduction() {
        new MySharedPreferences(this).storeBoolean(true, FixedWords.READ_INTRODUCTION);
    }

    private void showIntroductionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_CustomButtonDialog);
        builder.setMessage(getResources().getString(R.string.order_introduction));
        builder.setPositiveButton(getResources().getString(R.string.understand), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                doneReadingIntroduction();
            }
        });
        builder.show();
    }


    @Override
    void keyBackFunction() {
        finish();
    }

}
