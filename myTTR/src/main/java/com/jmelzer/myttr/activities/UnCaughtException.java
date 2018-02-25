package com.jmelzer.myttr.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.logic.Client;

import java.lang.Thread.UncaughtExceptionHandler;

public class UnCaughtException implements UncaughtExceptionHandler {

    UncaughtExceptionHandler orgHandler;

    public UnCaughtException() {
        orgHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        Boolean send = sharedPref.getBoolean(MySettingsActivity.KEY_PREF_SEND_ERROR, true);
        if (send) {
            Crashlytics.log(Log.ERROR, "URLs", Client.lastUrls());
            Log.e(Constants.LOG_TAG, "uncaught", ex);
            orgHandler.uncaughtException(thread, ex);
        } else {
            Log.e(Constants.LOG_TAG, "uncaught", ex);
            System.exit(0);
        }
    }
}