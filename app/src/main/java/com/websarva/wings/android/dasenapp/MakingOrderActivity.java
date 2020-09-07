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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentTransaction;

/**
 * To use this Activity's method in PlayerListAdapter, implementing PlayerListAdapterListener
 */
public class MakingOrderActivity extends BaseAdActivity implements StartingPlayerListAdapterListener, SubPlayerListAdapterListener {
    private TextView tvSelectNum;
    private EditText etName;
    private LinearLayout rolesBox;
    private Button addSub;
    private Button deleteSub;
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
    // TODO should have as a player ?
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


    //ここからmain
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO debug
//        android.os.Debug.waitForDebugger();
        setContentView(R.layout.activity_making_order);
        setAdView(findViewById(R.id.ad_view_container_on_order));
        super.onCreate(savedInstanceState);

        orderType = getIntent().getIntExtra(FixedWords.ORDER_TYPE, FixedWords.NORMAL_ORDER);
        databaseUsing = new DatabaseUsing(this);
        databaseUsing.getPlayersInfo(orderType);
        databaseUsing.putSubPlayersInCache(orderType);
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
        tvSelectNum = findViewById(R.id.selectNum);
        etName = findViewById(R.id.etName);
        rolesBox = findViewById(R.id.role_box_for_sub);
        addSub = findViewById(R.id.add_sub_button);
        deleteSub = findViewById(R.id.delete_sub_button);
        rolePitcher = findViewById(R.id.use_pitcher_button);
        roleBatter = findViewById(R.id.use_batter_button);
        roleRunner = findViewById(R.id.use_runner_button);
        roleFielder = findViewById(R.id.use_fielder_button);
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
                setSpinnerResource(getResources().getStringArray(R.array.positions));
                break;
            case FixedWords.DH_ORDER:
                setSpinnerResource(getResources().getStringArray(R.array.positions_dh));
                break;
        }
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
        SubPlayerListItemData selectedSubPlayer =
                CachedPlayersInfo.instance.getSubMembers(orderType).get(subNum);
        // into starting DB
        databaseUsing.registerStartingPlayer(
                startingNum,
                selectedSubPlayer.getName(),
                CachedPlayersInfo.instance.getPositionFromCache(orderType, startingNum),
                orderType);
        // into sub DB
        databaseUsing.updateSubPlayer(
                orderType,
                selectedSubPlayer.getId(),
                selectedSubPlayer.getPitcher(),
                selectedSubPlayer.getBatter(),
                selectedSubPlayer.getRunner(),
                selectedSubPlayer.getFielder(),
                CachedPlayersInfo.instance.getNameFromCache(orderType, startingNum));

        databaseUsing.putStartingPlayersInCache(orderType, startingNum);
        lineupFragment.updatePlayerListView(
                startingNum,
                CachedPlayersInfo.instance.getNameFromCache(orderType, startingNum),
                CachedPlayersInfo.instance.getPositionFromCache(orderType, startingNum));
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

        if (isContainingDhPitcher(firstSelectedOrderNum, secondSelectedOrderNum)) {
            // only name will be exchanged
            databaseUsing.registerStartingPlayer(firstSelectedOrderNum,
                    CachedPlayersInfo.instance.getNameFromCache(orderType, secondSelectedOrderNum),
                    CachedPlayersInfo.instance.getPositionFromCache(orderType, firstSelectedOrderNum),
                    orderType);
            databaseUsing.registerStartingPlayer(secondSelectedOrderNum,
                    CachedPlayersInfo.instance.getNameFromCache(orderType, firstSelectedOrderNum),
                    CachedPlayersInfo.instance.getPositionFromCache(orderType, secondSelectedOrderNum),
                    orderType);
        } else {
            // 最初に選択した選手のところに後から選択した選手を上書き
            databaseUsing.registerStartingPlayer(firstSelectedOrderNum,
                    CachedPlayersInfo.instance.getNameFromCache(orderType, secondSelectedOrderNum),
                    CachedPlayersInfo.instance.getPositionFromCache(orderType, secondSelectedOrderNum),
                    orderType);
            // 後に選択した選手の場所に最初の選手を登録
            databaseUsing.registerStartingPlayer(secondSelectedOrderNum,
                    CachedPlayersInfo.instance.getNameFromCache(orderType, firstSelectedOrderNum),
                    CachedPlayersInfo.instance.getPositionFromCache(orderType, firstSelectedOrderNum),
                    orderType);
        }

        // キャッシュデータもデータベースの内容に合わせる(入れ替え後のデータに更新する)
        databaseUsing.putStartingPlayersInCache(orderType, firstSelectedOrderNum);
        databaseUsing.putStartingPlayersInCache(orderType, secondSelectedOrderNum);

        updateInListView(firstSelectedOrderNum, secondSelectedOrderNum);
    }

    private boolean isContainingDhPitcher(int num1, int num2) {
        return (num1 == FixedWords.NUMBER_OF_LINEUP_DH || num2 == FixedWords.NUMBER_OF_LINEUP_DH);
    }

    private void updateInListView(int firstSelectedOrderNum, int secondSelectedOrderNum) {
        lineupFragment.updatePlayerListView(firstSelectedOrderNum,
                CachedPlayersInfo.instance.getNameFromCache(orderType, firstSelectedOrderNum),
                CachedPlayersInfo.instance.getPositionFromCache(orderType, firstSelectedOrderNum));
        lineupFragment.updatePlayerListView(secondSelectedOrderNum,
                CachedPlayersInfo.instance.getNameFromCache(orderType, secondSelectedOrderNum),
                CachedPlayersInfo.instance.getPositionFromCache(orderType, secondSelectedOrderNum));
    }

    private void selectNum(int orderNum) {
        readyInputtingStartingPlayer(orderNum,
                CachedPlayersInfo.instance.getPositionFromCache(orderType, orderNum),
                CachedPlayersInfo.instance.getNameFromCache(orderType, orderNum));
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
            selectSpinnerItem(spinner, FixedWords.HYPHEN_4);
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
        if (etName.getText().toString().equals(FixedWords.HYPHEN_5))
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
        if (playerName.equals(FixedWords.EMPTY)) playerName = FixedWords.HYPHEN_5;
        if (showingOrder.equals(FixedWords.Starting_ORDER)) {
            String position = (String) spinner.getSelectedItem();
            if (currentStartingNum == FixedWords.DH_PITCHER_ORDER) position = FixedWords.PITCHER;
            databaseUsing.registerStartingPlayer(currentStartingNum, playerName, position, orderType);
            CachedPlayersInfo.instance.setPlayerInfoToCache(orderType, currentStartingNum, position, playerName);
            lineupFragment.updatePlayerListView(currentStartingNum, playerName, position);
        } else {
            if (isNewlyAddSub()) {
                databaseUsing.registerSubPlayer(
                        orderType, isRolePitcher, isRoleBatter, isRoleRunner, isRoleFielder, playerName);
                databaseUsing.putSubPlayersInCache(orderType);
            } else {
                // overwrite mode
                databaseUsing.updateSubPlayer(
                        orderType, currentSubId, isRolePitcher, isRoleBatter, isRoleRunner, isRoleFielder, playerName);
                CachedPlayersInfo.instance.overwriteSubPlayer(
                        orderType, currentSubListIndex, isRolePitcher, isRoleBatter, isRoleRunner, isRoleFielder, playerName);
            }
            subMembersFragment.updatePlayerListView();
        }
        setLayoutDefault();
    }

    private boolean isNewlyAddSub() {
        return currentSubListIndex == CachedPlayersInfo.instance.getSubMembers(orderType).size();
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
        orderSwitch.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.exchange_button_background, null));
        title.setText(R.string.title);
        title.setTextColor(Color.parseColor(FixedWords.COLOR_WHITE));

        // TODO sub
        resetRoles();
        rolesBox.setVisibility(View.GONE);
        deleteSub.setEnabled(true);
        deleteSub.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.exchange_button_background, null));
        addSub.setEnabled(true);
        addSub.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.exchange_button_background, null));
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
        makeButtonDisable(exchange);
        makeButtonDisable(addSub);
        makeButtonDisable(deleteSub);
        resetRoles();
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
        Sharing mSharing = new Sharing(getApplicationContext(), this, findViewById(R.id.lineup_container));
        mSharing.share();
    }


    private void cancelExchanging() {
        if (isFirstExchangeClicked) cancelFirstClick(firstClickedButton);
        isExchanging = false;
        title.setText(R.string.title);
        title.setTextColor(Color.parseColor(FixedWords.COLOR_WHITE));
        exchange.setEnabled(true);
        exchange.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.exchange_button_background, null));
        makeButtonDisable(cancel);
    }

    public void onClickSwitchOrder(View view) {
        if (showingOrder.equals(FixedWords.Starting_ORDER)) showSubMembers();
        else showStartingOrder();
    }

    // TODO refactor ? (one method and switch (button id))
    public void onClickRolePitcher(View view) {
        setRole(!isRolePitcher, FixedWords.ROLE_PITCHER);
    }

    public void onClickRoleBatter(View view) {
        setRole(!isRoleBatter, FixedWords.ROLE_BATTER);
    }

    public void onClickRoleRunner(View view) {
        setRole(!isRoleRunner, FixedWords.ROLE_RUNNER);
    }

    public void onClickRoleFielder(View view) {
        setRole(!isRoleFielder, FixedWords.ROLE_FIELDER);
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
        currentSubListIndex = CachedPlayersInfo.instance.getSubMembers(orderType).size();
        requireInputtingPlayer(FixedWords.EMPTY);
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
        makeButtonDisable(addSub);
        makeButtonDisable(orderSwitch);
        makeButtonDisable(deleteSub);
        makeButtonDisable(exchange);
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
        orderSwitch.setText("控え表示");
    }

    private void showSubMembers() {
        switchOrder(false);
        showingOrder = FixedWords.SUB_MEMBERS;
        tvSelectNum.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        subLabel.setVisibility(View.VISIBLE);
        addSub.setVisibility(View.VISIBLE);
        deleteSub.setVisibility(View.VISIBLE);
        orderSwitch.setText("先発表示");
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

    // TODO
    @Override
    public void onClickSubOrderNum(int listPosition, SubPlayerListItemData subMember, Button numButton) {
        if (isDeleting) {
            databaseUsing.deleteSubPlayer(orderType, subMember.getId());
            CachedPlayersInfo.instance.deleteSubPlayer(orderType, listPosition);
            subMembersFragment.updatePlayerListView();
            setLayoutDefault();
        } else if (isExchanging) {
            checkSelectedSubPlayer(listPosition, numButton);
        } else {
            // overwrite mode
            currentSubListIndex = listPosition;
            currentSubId = subMember.getId();
            readyInputtingSubPlayer(subMember);
        }
    }


    @Override
    void keyBackFunction() {
        finish();
    }

}
