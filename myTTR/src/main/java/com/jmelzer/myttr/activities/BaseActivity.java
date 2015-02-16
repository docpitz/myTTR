package com.jmelzer.myttr.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;

/**
 * Base class for all activities setting the header etc.
 */
public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(this));


        if (MyApplication.manualClub != null && !"".equals(MyApplication.manualClub)) {
            setTitle(MyApplication.getTitle() + " - " + MyApplication.manualClub);
        } else {
            setTitle(MyApplication.getTitle());
        }
//        setTitle(MyApplication.getTitle() + " - " + getClass().getSimpleName());
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(Constants.LOG_TAG, "back pressed in " + this);
    }


}
