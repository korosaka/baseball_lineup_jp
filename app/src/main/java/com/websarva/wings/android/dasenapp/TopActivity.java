package com.websarva.wings.android.dasenapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TopActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        if (!PrivacyPolicyFragment.isPolicyAgreed(this)) showPrivacyPolicy();
    }

    public void onClickNonDH(View view) {
        startOrderActivity(FixedWords.NORMAL_ORDER);
    }

    public void onClickDH(View view) {
        startOrderActivity(FixedWords.DH_ORDER);
    }

    private void startOrderActivity(int orderType) {
        Intent intent = new Intent(TopActivity.this, MakingOrderActivity.class);
        intent.putExtra(FixedWords.ORDER_TYPE, orderType);
        startActivity(intent);
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