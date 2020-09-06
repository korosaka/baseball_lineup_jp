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
    private Button replace;
    private Boolean isReplacing = false;
    private Boolean isDeleting = false;
    private Boolean isFirstReplaceClicked = false;
    private TextView title;
    private int currentStartingNum;
    // TODO should have as a player ?
    private int currentSubListIndex;
    private int currentSubId;
    private Spinner spinner;
    private Button clear;

    private int firstClickedOrderNum;
    private Button firstClickedButton;
    private DatabaseUsing databaseUsing;
    private StartingLineupFragment lineupFragment;
    private SubMembersFragment subMembersFragment;
    private Button dhPitcherButton;
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
        databaseUsing.registerStartingPlayer(firstSelectedOrderNum,
                CachedPlayersInfo.instance.getNameFromCache(orderType, secondSelectedOrderNum),
                CachedPlayersInfo.instance.getPositionFromCache(orderType, secondSelectedOrderNum),
                orderType);

        // 後に選択した選手の場所に最初の選手を登録
        databaseUsing.registerStartingPlayer(secondSelectedOrderNum,
                CachedPlayersInfo.instance.getNameFromCache(orderType, firstSelectedOrderNum),
                CachedPlayersInfo.instance.getPositionFromCache(orderType, firstSelectedOrderNum),
                orderType);

        // キャッシュデータもデータベースの内容に合わせる(入れ替え後のデータに更新する)
        databaseUsing.putStartingPlayersInCache(orderType, firstSelectedOrderNum);
        databaseUsing.putStartingPlayersInCache(orderType, secondSelectedOrderNum);

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
        replace.setEnabled(false);
        replace.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));

        // TODO for sub
        addSub.setEnabled(false);
        addSub.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        orderSwitch.setEnabled(false);
        orderSwitch.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        deleteSub.setEnabled(false);
        deleteSub.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
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
        record.setEnabled(false);
        record.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        cancel.setEnabled(false);
        cancel.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        clear.setEnabled(false);
        clear.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        replace.setEnabled(true);
        replace.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.replace_button_background, null));
        // TODO sub
        resetRoles();
        deleteSub.setEnabled(true);
        deleteSub.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.replace_button_background, null));
        addSub.setEnabled(true);
        addSub.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.replace_button_background, null));
        orderSwitch.setEnabled(true);
        orderSwitch.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.replace_button_background, null));
        title.setText(R.string.title);
        title.setTextColor(Color.parseColor(FixedWords.COLOR_WHITE));
        isDeleting = false;
    }

    public void onClickClear(View view) {
        etName.setText(FixedWords.EMPTY);
        spinner.setSelection(0);
        resetRoles();
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
                if (isRolePitcher)
                    rolePitcher.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.replace_button_background, null));
                else
                    rolePitcher.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
                break;
            case FixedWords.ROLE_BATTER:
                isRoleBatter = isRole;
                if (isRoleBatter)
                    roleBatter.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.replace_button_background, null));
                else
                    roleBatter.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
                break;
            case FixedWords.ROLE_RUNNER:
                isRoleRunner = isRole;
                if (isRoleRunner)
                    roleRunner.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.replace_button_background, null));
                else
                    roleRunner.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
                break;
            case FixedWords.ROLE_FIELDER:
                isRoleFielder = isRole;
                if (isRoleFielder)
                    roleFielder.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.replace_button_background, null));
                else
                    roleFielder.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
                break;
        }
    }


    public void onClickAddSub(View view) {
        currentSubListIndex = CachedPlayersInfo.instance.getSubMembers(orderType).size();
        requireInputtingPlayer(FixedWords.EMPTY);
    }

    public void onClickDeleteSub(View view) {
        isDeleting = true;
        title.setText("select delete player!");
        addSub.setEnabled(false);
        addSub.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        orderSwitch.setEnabled(false);
        orderSwitch.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        deleteSub.setEnabled(false);
        deleteSub.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        replace.setEnabled(false);
        replace.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        cancel.setEnabled(true);
        cancel.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.cancel_button_background, null));
        title.setTextColor(Color.parseColor(FixedWords.COLOR_EMPHASIZING));
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
        rolesBox.setVisibility(View.VISIBLE);
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
    public void onClickOrderNum(int orderNum, Button numButton) {
        if (isReplacing) {
            replaceMethod(orderNum, numButton);
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
        } else {
            // overwrite mode
            currentSubListIndex = listPosition;
            currentSubId = subMember.getId();
            readyInputtingSubPlayer(subMember);
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
