package com.websarva.wings.android.dasenapp;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class BaseAdActivity extends AppCompatActivity {

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
        adView = new AdView(this);
        adView.setAdUnitId(BANNER_AD_UNIT_ID);
        adViewContainer.addView(adView);
        loadBanner();
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

    private void loadBanner() {
        AdRequest adRequest =
                new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build();
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.loadAd(adRequest);
    }

    /**
     * reference
     * https://developers.google.com/admob/android/interstitial
     */
    protected void loadInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(INTERSTITIAL_AD_UNIT_ID);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                finish();
            }

            @Override
            public void onAdClosed() {
                finish();
            }
        });
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    protected void showInterstitialAd() {
        if (mInterstitialAd.isLoaded()) mInterstitialAd.show();
    }
}
