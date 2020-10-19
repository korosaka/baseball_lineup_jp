package com.websarva.wings.android.dasenapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
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
        if (item.getItemId() == R.id.policy) {
            PrivacyPolicyFragment policyFragment = PrivacyPolicyFragment.newInstance(FixedWords.CLOSE);
            policyFragment.show(getSupportFragmentManager(), FixedWords.PRIVACY_POLICY);
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return isOnlineNew(connMgr);
        } else {
            return isOnlineOld(connMgr);
        }
    }

    /**
     * from API version 29, this method is deprecated
     */
    private boolean isOnlineOld(ConnectivityManager connMgr) {
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isOnlineNew(ConnectivityManager connMgr) {
        NetworkCapabilities nc = connMgr.getNetworkCapabilities(connMgr.getActiveNetwork());

        if (nc != null) {
            if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true;
            if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true;
        }
        return false;
    }

}
