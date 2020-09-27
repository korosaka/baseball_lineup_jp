package com.websarva.wings.android.dasenapp;

import androidx.appcompat.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

public class TopActivity extends BaseActivity {

    private Button normalOrderButton;
    private Button dhOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        normalOrderButton = findViewById(R.id.normal_order_button);
        dhOrderButton = findViewById(R.id.dh_order_button);
        CachedPlayersInfo.instance.initCachedArray();
        if (!PrivacyPolicyFragment.isPolicyAgreed(this)) showPrivacyPolicy();
    }

    public void onClickNonDH(View view) {
        startOrderActivity(FixedWords.NORMAL_ORDER);
    }

    public void onClickDH(View view) {
        startOrderActivity(FixedWords.DH_ORDER);
    }

    public void onClickSpecial(View view) {
        startOrderActivity(FixedWords.SPECIAL_ORDER);
    }

    private void startOrderActivity(int orderType) {
        showProgress();
        Intent intent = new Intent(TopActivity.this, MakingOrderActivity.class);
        intent.putExtra(FixedWords.ORDER_TYPE, orderType);
        startActivity(intent);
    }

    private void showProgress() {
        normalOrderButton.setEnabled(false);
        dhOrderButton.setEnabled(false);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                normalOrderButton.setEnabled(true);
                dhOrderButton.setEnabled(true);
                progressDialog.dismiss();
            }
        }, 2000);
    }

    private void showPrivacyPolicy() {
        PrivacyPolicyFragment policyFragment = PrivacyPolicyFragment.newInstance(FixedWords.AGREE);
        policyFragment.show(getSupportFragmentManager(), FixedWords.PRIVACY_POLICY);
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