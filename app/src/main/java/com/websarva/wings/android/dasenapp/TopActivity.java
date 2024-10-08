package com.websarva.wings.android.dasenapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * about Google Play Billing
 * reference:
 * https://developer.android.com/google/play/billing/billing_library_overview#java
 * https://developer.android.com/google/play/billing/release-notes?hl=ja
 * https://qiita.com/takahirom/items/4d597b00f500efb3dc7f
 * https://qiita.com/watanaby0/items/deb60166753533fb00b1
 * https://qiita.com/takahirom/items/ed2cf675e91309b649c0
 */
public class TopActivity extends BaseAdActivity
        implements PurchasesUpdatedListener, AcknowledgePurchaseResponseListener {

    private Button normalOrderButton;
    private Button dhOrderButton;
    private Button specialOrderButton;
    private Button purchaseButton;
    private Button restoreButton;
    private TextView explanationText;
    private TextView checkInternetText;
    private MyProgressDialog myProgressDialog;
    private int cachedButtonType = FixedWords.INVALID_ORDER;

    private BillingClient billingClient;
    boolean billingClientConnected = false;
    boolean isPurchasingProcess = false;

    final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView();
        initProgressDialog();
        checkPurchaseStatement();
        //skip showing Interstitial when the initial use for good user experience and show it only after policy has been agreed
        checkPolicyAgreed();
    }

    private void checkPolicyAgreed() {
        if (PrivacyPolicyFragment.isPolicyAgreed(this)) loadInterstitialAd();
        else showPrivacyPolicy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCountOfOpeningApp();
    }

    private void checkCountOfOpeningApp() {
        int openCount = new MySharedPreferences(this).getInt(FixedWords.NUMBER_OF_OPEN_APP);
        final int FIRST_RECOMMENDATION_TIMING = 4;
        final int SECOND_RECOMMENDATION_TIMING = 20;
        final int THIRD_RECOMMENDATION_TIMING = 50;
        final int FOURTH_RECOMMENDATION_TIMING = 100;

        if (openCount == FIRST_RECOMMENDATION_TIMING
                || openCount == SECOND_RECOMMENDATION_TIMING
                || openCount == THIRD_RECOMMENDATION_TIMING
                || openCount == FOURTH_RECOMMENDATION_TIMING) {
            RecommendAppFragment recommendationFragment = RecommendAppFragment.newInstance();
            recommendationFragment.show(getSupportFragmentManager(), null);
        }
    }

    private void connectBillingClient() {
        if (!isOnline()) {
            myProgressDialog.dismiss();
            showToastMessage(getResources().getString(R.string.require_connection));
            return;
        }

        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    billingClientConnected = true;
                    reloadPurchaseHistory();
                } else {
                    myProgressDialog.dismiss();
                    if (isPurchasingProcess) {
                        isPurchasingProcess = false;
                        showToastMessage(getResources().getString(R.string.failed_play_store_connection));
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                billingClientConnected = false;
                myProgressDialog.dismiss();
            }
        });
    }

    private void startPurchaseFlow() {
        billingClient.queryProductDetailsAsync(
                createAllHitterParams(),
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult,
                                                         List<ProductDetails> productDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList != null) {
                            for (ProductDetails skuDetails : productDetailsList) {
                                launchBillingFlow(skuDetails);
                                // even if user has already purchased, this process is called => not sure on the current version(7.0.0)
                            }
                        }
                        myProgressDialog.dismiss();
                    }
                });
    }

    private QueryProductDetailsParams createAllHitterParams() {
        return QueryProductDetailsParams.newBuilder()
                .setProductList(
                        ImmutableList.of(
                                QueryProductDetailsParams.Product.newBuilder()
                                        .setProductId(FixedWords.ITEM_ID_ALL_HITTER)
                                        .setProductType(BillingClient.ProductType.INAPP)
                                        .build()))
                .build();
    }

    /**
     * after this method, onPurchasesUpdated will be called (if user purchase)
     */
    private void launchBillingFlow(ProductDetails skuDetails) {
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(skuDetails)
                                .build()
                );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();
        billingClient.launchBillingFlow(this, billingFlowParams);
    }

    private void bindView() {
        setContentView(R.layout.activity_top);
        normalOrderButton = findViewById(R.id.normal_order_button);
        dhOrderButton = findViewById(R.id.dh_order_button);
        specialOrderButton = findViewById(R.id.special_order_button);
        purchaseButton = findViewById(R.id.purchase_button);
        restoreButton = findViewById(R.id.restore_button);
        explanationText = findViewById(R.id.explanation_special);
        checkInternetText = findViewById(R.id.check_internet_text);
    }

    private boolean isSpecialOrderPurchased() {
        return new MySharedPreferences(this).getBoolean(FixedWords.PURCHASE_SPECIAL_ORDER);
    }

    private void checkPurchaseStatement() {
        if (isSpecialOrderPurchased()) {
            dismissPurchasingViews();
        } else {
            specialOrderButton.setText(R.string.special_version_disable);
            specialOrderButton.setTextColor(Color.parseColor(FixedWords.COLOR_OFF_BLACK));
            specialOrderButton.setEnabled(false);
            specialOrderButton
                    .setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disable_button_background, null));

            showProgressDialog();
            connectBillingClient();
        }
    }

    private void enableSpecialOrder() {
        dismissPurchasingViews();

        specialOrderButton.setText(R.string.special_version);
        specialOrderButton.setTextColor(Color.parseColor(FixedWords.COLOR_WHITE));
        specialOrderButton.setEnabled(true);
        specialOrderButton
                .setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.special_button_background, null));
    }

    private void dismissPurchasingViews() {
        purchaseButton.setVisibility(View.GONE);
        restoreButton.setVisibility(View.GONE);
        explanationText.setVisibility(View.GONE);
        checkInternetText.setVisibility(View.GONE);
    }

    public void onClickPurchase(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_CustomButtonDialog);
        builder.setMessage(getResources().getString(R.string.ask_purchase));
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (!isOnline()) {
                    showToastMessage(getResources().getString(R.string.require_connection));
                    return;
                }
                if (billingClientConnected) {
                    showProgressDialog();
                    startPurchaseFlow();
                } else {
                    isPurchasingProcess = true;
                    connectBillingClient();
                }

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), null);
        builder.show();
    }

    public void onClickRestore(View view) {
        showProgressDialog();
        connectBillingClient();
    }

    public void onClickNonDH(View view) {
        onClickOrderButton(FixedWords.NORMAL_ORDER);
    }

    public void onClickDH(View view) {
        onClickOrderButton(FixedWords.DH_ORDER);
    }

    public void onClickSpecial(View view) {
        onClickOrderButton(FixedWords.SPECIAL_ORDER);
    }

    private void onClickOrderButton(int orderType) {
        preventDoubleTap();
        if (shouldShowInterstitial()) {
            cachedButtonType = orderType;
            showInterstitialAd();
        } else {
            startOrderActivity(orderType);
        }
    }

    private void startOrderActivity(int orderType) {
        if (orderType == FixedWords.INVALID_ORDER) return;
        Intent intent = new Intent(TopActivity.this, MakingOrderActivity.class);
        intent.putExtra(FixedWords.ORDER_TYPE, orderType);
        startActivity(intent);
    }

    private boolean shouldShowInterstitial() {
        if (mInterstitialAd == null) {
            loadInterstitialAd();
            return false;
        }

        final long previousAdTime = new MySharedPreferences(this).getLong(FixedWords.SAVED_INTERSTITIAL_TIME);
        if (previousAdTime > 0) {
            final long currentTime = System.currentTimeMillis();
            final long timeDifference = currentTime - previousAdTime;
            final long ONE_HOUR_MILLIS = 3600000;
            return timeDifference > ONE_HOUR_MILLIS;
        } else return true;
    }

    private void initProgressDialog() {
        myProgressDialog = MyProgressDialog.newInstance(getResources().getString(R.string.in_progress));
    }

    private void showProgressDialog() {
        myProgressDialog.show(getSupportFragmentManager(), FixedWords.PROGRESS_DIALOG);
    }

    private void preventDoubleTap() {
        setEnableOfOrderButtons(false);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                setEnableOfOrderButtons(true);
            }
        }, 1000);
    }

    private void setEnableOfOrderButtons(boolean enabled) {
        normalOrderButton.setEnabled(enabled);
        dhOrderButton.setEnabled(enabled);
        if (isSpecialOrderPurchased()) specialOrderButton.setEnabled(enabled);
    }

    @Override
    protected void onPause() {
        if (myProgressDialog.isVisible()) myProgressDialog.dismiss();
        super.onPause();
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
            for (Purchase purchase : purchases) handlePurchase(purchase);
        }
    }

    /**
     * after acknowledgePurchase, onAcknowledgePurchaseResponse method will be called
     */
    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            for (String purchasedItemId : purchase.getProducts()) {
                if (purchasedItemId.equals(FixedWords.ITEM_ID_ALL_HITTER)) {
                    savePurchaseRecord();
                    enableSpecialOrder();
                    break;
                }
            }
            acknowledgePurchase(purchase);
        }
    }

    @Override
    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
        int responseCode = billingResult.getResponseCode();

        String message = "nothing";
        switch (responseCode) {
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
        Log.d("acknowledge result", message);
    }

    public void onClickExplanation(View view) {
        explainSpecial(this);
    }

    private void explainSpecial(Activity context) {
        if (billingClient == null) {
            showToastMessage(getResources().getString(R.string.failed_play_store_connection));
            connectBillingClient();
            return;
        }

        showProgressDialog();
        billingClient.queryProductDetailsAsync(
                createAllHitterParams(),
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult,
                                                         List<ProductDetails> productDetailsList) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                myProgressDialog.dismiss();
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    for (ProductDetails productDetails : productDetailsList) {
                                        if (!productDetails.getProductId().equals(FixedWords.ITEM_ID_ALL_HITTER))
                                            continue;
                                        ProductDetails.OneTimePurchaseOfferDetails offerDetails = productDetails.getOneTimePurchaseOfferDetails();
                                        if (offerDetails == null) continue;
                                        String descriptionMessage =
                                                getResources().getString(R.string.about_special_1)
                                                        + getString(R.string.line_break)
                                                        + productDetails.getDescription()
                                                        + getString(R.string.line_break)
                                                        + getString(R.string.line_break)
                                                        + getString(R.string.price)
                                                        + getString(R.string.line_break)
                                                        + offerDetails.getFormattedPrice()
                                                        + getString(R.string.line_break)
                                                        + getString(R.string.line_break)
                                                        + getResources().getString(R.string.about_special_2);

                                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_CustomButtonDialog);
                                        builder.setMessage(descriptionMessage);
                                        builder.setNegativeButton(getResources().getString(R.string.close), null);
                                        builder.show();
                                        break;
                                    }
                                } else {
                                    showToastMessage(getResources().getString(R.string.failed_play_store_connection));
                                }
                            }
                        });
                    }
                });
    }

    private void acknowledgePurchase(Purchase purchase) {
        if (!purchase.isAcknowledged() && billingClient != null) {
            AcknowledgePurchaseParams acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
            billingClient.acknowledgePurchase(acknowledgePurchaseParams, this);
        }
    }

    private void reloadPurchaseHistory() {
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                new PurchasesResponseListener() {
                    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> purchases) {
                        int responseCode = billingResult.getResponseCode();
                        if (responseCode == BillingClient.BillingResponseCode.OK) {
                            if (!purchases.isEmpty()) {
                                for (Purchase purchase : purchases) {
                                    if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED)
                                        continue;
                                    for (String purchasedItemId : purchase.getProducts()) {
                                        if (purchasedItemId.equals(FixedWords.ITEM_ID_ALL_HITTER)) {
                                            savePurchaseRecord();
                                            acknowledgePurchase(purchase);
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    myProgressDialog.dismiss();
                                                    enableSpecialOrder();
                                                    showToastMessage(getResources().getString(R.string.reloaded_purchase));
                                                    if (isPurchasingProcess)
                                                        isPurchasingProcess = false;
                                                }
                                            });
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                myProgressDialog.dismiss();
                                showToastMessage(getResources().getString(R.string.no_purchase_record));
                                if (isPurchasingProcess) {
                                    isPurchasingProcess = false;
                                    showProgressDialog();
                                    startPurchaseFlow();
                                }
                            }
                        });
                    }
                });
    }

    private void savePurchaseRecord() {
        new MySharedPreferences(this).storeBoolean(true, FixedWords.PURCHASE_SPECIAL_ORDER);
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * reference
     * https://developers.google.com/admob/android/interstitial
     */
    private void loadInterstitialAd() {
        setEnableOfOrderButtons(false);
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, INTERSTITIAL_AD_UNIT_ID, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        setFullScreenContentCallback();
                        setEnableOfOrderButtons(true);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                        setEnableOfOrderButtons(true);
                    }
                });
    }

    private void showInterstitialAd() {
        if (mInterstitialAd != null) {
            saveInterstitialTime();
            mInterstitialAd.show(this);
        }
        else loadInterstitialAd();
    }

    private void saveInterstitialTime() {
        final long currentTimeMillis = System.currentTimeMillis();
        new MySharedPreferences(this).storeLong(currentTimeMillis, FixedWords.SAVED_INTERSTITIAL_TIME);
    }

    private void setFullScreenContentCallback() {
        if (mInterstitialAd == null) return;
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                interstitialAdCompleted();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                interstitialAdCompleted();
            }
        });
    }

    private void interstitialAdCompleted() {
        mInterstitialAd = null;
        loadInterstitialAd();
        startOrderActivity(cachedButtonType);
        cachedButtonType = FixedWords.INVALID_ORDER;
    }

}