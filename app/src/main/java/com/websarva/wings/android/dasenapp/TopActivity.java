package com.websarva.wings.android.dasenapp;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

/**
 * about Google Play Billing
 * reference:
 * https://developer.android.com/google/play/billing/billing_library_overview#java
 * https://developer.android.com/google/play/billing/release-notes?hl=ja
 * https://qiita.com/takahirom/items/4d597b00f500efb3dc7f
 * https://qiita.com/watanaby0/items/deb60166753533fb00b1
 * https://qiita.com/takahirom/items/4d597b00f500efb3dc7f
 */
public class TopActivity extends BaseActivity
        implements PurchasesUpdatedListener, AcknowledgePurchaseResponseListener {

    private Button normalOrderButton;
    private Button dhOrderButton;
    private Button specialOrderButton;
    private Button purchaseButton;

    private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView();
        checkPurchaseStatement();
        CachedPlayersInfo.instance.initCachedArray();
        if (!PrivacyPolicyFragment.isPolicyAgreed(this)) showPrivacyPolicy();
    }

    private void prepareBillingClient() {
        billingClient = BillingClient.newBuilder(this).setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    // TODO
                    getItemDetail();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                // TODO
            }
        });
    }

    private void getItemDetail() {
        List<String> skuList = new ArrayList<>();
        skuList.add(FixedWords.ITEM_ID_ALL_HITTER);

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            for (SkuDetails skuDetails : skuDetailsList) {
                                launchBillingFlow(skuDetails);
                            }
                        }
                    }
                });
    }

    /**
     * after this method, onPurchasesUpdated will be called
     */
    private void launchBillingFlow(SkuDetails skuDetails) {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        billingClient.launchBillingFlow(this, flowParams);
    }

    private void bindView() {
        setContentView(R.layout.activity_top);
        normalOrderButton = findViewById(R.id.normal_order_button);
        dhOrderButton = findViewById(R.id.dh_order_button);
        specialOrderButton = findViewById(R.id.special_order_button);
        purchaseButton = findViewById(R.id.purchase_button);
    }

    private boolean isSpecialOrderPurchased() {
        return new MySharedPreferences(this).getBoolean(FixedWords.PURCHASE_SPECIAL_ORDER);
    }

    private void checkPurchaseStatement() {
        if (isSpecialOrderPurchased()) {
            purchaseButton.setVisibility(View.GONE);
        } else {
            specialOrderButton.setText(R.string.special_version_disable);
            specialOrderButton.setTextColor(Color.parseColor(FixedWords.COLOR_OFF_BLACK));
            specialOrderButton.setEnabled(false);
            specialOrderButton
                    .setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));
        }
    }

    public void onClickPurchase(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_CustomButtonDialog);
        builder.setMessage(getResources().getString(R.string.ask_purchase));
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // TODO
                prepareBillingClient();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), null);
        builder.show();
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
        if (isSpecialOrderPurchased()) specialOrderButton.setEnabled(false);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                normalOrderButton.setEnabled(true);
                dhOrderButton.setEnabled(true);
                if (isSpecialOrderPurchased()) specialOrderButton.setEnabled(true);
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

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                if (purchase.getSku().equals(FixedWords.ITEM_ID_ALL_HITTER)) {
                    new MySharedPreferences(this).storeBoolean(true, FixedWords.PURCHASE_SPECIAL_ORDER);
                }
                handlePurchase(purchase);
            }
        }
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, this);
            } else {
                Toast.makeText(this, "already acknowledged", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
        int responseCode = billingResult.getResponseCode();

        String message = "nothing";
        switch(responseCode){
            case BillingClient.BillingResponseCode.OK:
                message = "OK";
                break;
            case BillingClient.BillingResponseCode.USER_CANCELED:
                message = "USER_CANCELED";
                break;
            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                message = "SERVICE_UNAVAILABLE";
                break;
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                message = "BILLING_UNAVAILABLE";
                break;
            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                message = "ITEM_UNAVAILABLE";
                break;
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                message = "DEVELOPER_ERROR";
                break;
            case BillingClient.BillingResponseCode.ERROR:
                message = "ERROR";
                break;
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                message = "ITEM_ALREADY_OWNED";
                break;
            case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
                message = "ITEM_NOT_OWNED";
                break;
            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                message = "SERVICE_DISCONNECTED";
                break;
            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                message = "FEATURE_NOT_SUPPORTED";
                break;
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // TODO reload purchase history

}