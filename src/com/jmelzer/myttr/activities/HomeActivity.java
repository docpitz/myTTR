/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.Client;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.SyncManager;

public class HomeActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.afterlogin);


        startService(new Intent(this, SyncManager.class));
//        new SyncManager().startService();

    }

    public void manual(final View view) {
        Intent target = new Intent(this, ManualEntriesActivity.class);
        startActivity(target);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (MyApplication.loginUser.getPoints() < 0) {
            Intent target = new Intent(this, EnterTTRActivity.class);
            startActivity(target);
        }
    }

    public void clublist(View view) {
        AsyncTask<String, Void, Integer> task = new ClubListAsyncTask(this, ClubListActivity.class);
        task.execute();
    }

    public void statistik(View view) {
        AsyncTask<String, Void, Integer> task = new EventsAsyncTask(this, EventsActivity.class);
        task.execute();
    }

    private class ClubListAsyncTask extends BaseAsyncTask {

        public ClubListAsyncTask(Activity parent, Class targetClz) {
            super(parent, targetClz);
        }

        @Override
        protected void callParser() throws NetworkException, LoginExpiredException {
            MyApplication.clubPlayers = new MyTischtennisParser().getClubList();
        }


        @Override
        protected boolean dataLoaded() {
            return MyApplication.clubPlayers != null;
        }
    }

}
