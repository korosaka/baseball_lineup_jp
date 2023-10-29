package com.websarva.wings.android.dasenapp;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Collections;
import java.util.List;

/**
 * updated the library to 20 from 19
 * reference: https://developers.google.com/admob/android/migration?hl=ja#migrate-to-v20
 *
 * however, version 20 will be sunset in 2024
 * this is why please update this to the latest one by the end of 2023
 * This time, there is not enough time to do it
 * it's just the first aid
 */

abstract class BaseAdActivity extends BaseActivity {

    private AdView adView;
    private FrameLayout adViewContainer;
    private static final String BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";

    private InterstitialAd mInterstitialAd;
    private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";

    protected void setAdView(FrameLayout container) {
        adViewContainer = container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        indicateAd();
    }


    /**
     * reference
     * https://developers.google.com/admob/android/banner#java_1
     */
    private void indicateAd() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        customAdView();
        loadBanner();
    }

    private void customAdView() {
        adView = new AdView(this);
        adView.setAdUnitId(BANNER_AD_UNIT_ID);
        adView.setAdSize(getAdSize());
        adViewContainer.addView(adView);
    }

    /**
     * reference
     * https://developers.google.com/admob/android/banner/adaptive
     */
    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    protected void loadBanner() {
        List<String> testDeviceIds = Collections.singletonList(AdRequest.DEVICE_ID_EMULATOR);
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration); // it will effect globally

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    /**
     * reference
     * https://developers.google.com/admob/android/interstitial
     */
    protected void loadInterstitialAd() {

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,INTERSTITIAL_AD_UNIT_ID, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        setFullScreenContentCallback();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d("loadInterstitialAd", loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
    }

    private void setFullScreenContentCallback() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdClicked() {
                finish();
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                mInterstitialAd = null;
                finish();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                mInterstitialAd = null;
                finish();
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                finish();
            }
        });
    }

    protected void showInterstitialAd() {
        if (mInterstitialAd != null) mInterstitialAd.show(this);
        else finish();
    }
}
