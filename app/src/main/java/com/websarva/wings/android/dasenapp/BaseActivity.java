package com.websarva.wings.android.dasenapp;

import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

abstract class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            keyBackFunction();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    abstract void keyBackFunction();
}
