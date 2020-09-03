package com.websarva.wings.android.dasenapp;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO refactor ?
        switch (item.getItemId()) {
            case R.id.policy:
                PrivacyPolicyFragment policyFragment = PrivacyPolicyFragment.newInstance(FixedWords.CLOSE);
                policyFragment.show(getSupportFragmentManager(), FixedWords.PRIVACY_POLICY);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
