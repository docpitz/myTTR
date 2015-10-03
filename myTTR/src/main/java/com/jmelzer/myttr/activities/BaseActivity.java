package com.jmelzer.myttr.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;

/**
 * Base class for all activities setting the header etc.
 */
public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(Constants.LOG_TAG, "activity " + this + " in oncreate");

        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(this));

        Tracker t = MyApplication.getTracker();

// Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory("activity")
                .setAction("create")
                .setLabel("todo")
                .build());


        if (MyApplication.manualClub != null && !"".equals(MyApplication.manualClub)) {
            setTitle(MyApplication.getTitle() + " - " + MyApplication.manualClub);
        } else {
            setTitle(MyApplication.getTitle());
        }
        if (MyApplication.getLoginUser().getUsername().equals("chokdee")) {
            setTitle(MyApplication.getTitle() + " - " + getClass().getSimpleName());
        }
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Stop the analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(Constants.LOG_TAG, "activity " + this + "  on Restart called");
        toLoginIfNeccassry();
    }

    protected boolean toLoginIfNeccassry() {
//        if (MyApplication.userIsEmpty() && !getClass().equals(LoginActivity.class)) {
        if (!checkIfNeccessryDataIsAvaible()) {
            Log.i(Constants.LOG_TAG, "restart after stop");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * sub classes must check if the needed data is avaible otherwise we go back to the login activity
     * @return ture if data is avaible
     */
    protected abstract boolean checkIfNeccessryDataIsAvaible();
}
