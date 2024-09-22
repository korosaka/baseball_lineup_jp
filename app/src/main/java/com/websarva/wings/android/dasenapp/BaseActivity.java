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

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

abstract class BaseActivity extends AppCompatActivity {

    protected boolean isReviewRequested = false;

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
        } else if (item.getItemId() == R.id.review_app) {
            requestReview();
        } else if (item.getItemId() == R.id.recommend) {
            RecommendAppFragment recommendationFragment = RecommendAppFragment.newInstance();
            recommendationFragment.show(getSupportFragmentManager(), null);
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

    //ref: https://developer.android.com/guide/playcore/in-app-review/kotlin-java?hl=ja#java
    protected void requestReview() {
        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(reviewInfoTask -> {
            if (reviewInfoTask.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = reviewInfoTask.getResult();
                Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(taskOfLaunch -> {
                    isReviewRequested = true;
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            }
        });
    }

}
