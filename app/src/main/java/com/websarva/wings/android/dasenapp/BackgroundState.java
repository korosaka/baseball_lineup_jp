package com.websarva.wings.android.dasenapp;

import android.os.Handler;
import android.os.Looper;

public class BackgroundState {

    public static boolean preventFromGoingBackToTop = false;

    public static void temporaryPrevent() {
        preventFromGoingBackToTop = true;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                preventFromGoingBackToTop = false;
            }
        }, 2000);
    }
}
