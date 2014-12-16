/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.activities;

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
        AsyncTask<String, Void, Integer> task = new ClubListAsyncTask();
        task.execute();
    }

    public void statistik(View view) {
        AsyncTask<String, Void, Integer> task = new EventsAsyncTask();
        task.execute();
    }

    private class ClubListAsyncTask extends AsyncTask<String, Void, Integer> {
        ProgressDialog progressDialog;
        private String errorMessage;

        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setMessage("Lade die Vereinsliste, bitte warten...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
            try {
                MyApplication.clubPlayers = myTischtennisParser.getClubList();
            } catch (NetworkException e) {
                errorMessage = NetworkException.translate(e);
                Log.d(Constants.LOG_TAG, "", e);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (MyApplication.clubPlayers != null) {
                Intent intent = new Intent(HomeActivity.this, ClubListActivity.class);
                startActivity(intent);
            } else if (errorMessage != null) {
                Toast.makeText(HomeActivity.this, errorMessage,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(HomeActivity.this, "Konnte die Vereinsliste nicht laden",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class EventsAsyncTask extends AsyncTask<String, Void, Integer> {
        ProgressDialog progressDialog;

        private String errorMessage;

        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setMessage("Lade deine Spiele, bitte warten...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
            try {
                MyApplication.events = myTischtennisParser.readEvents();
            } catch (NetworkException e) {
                errorMessage = NetworkException.translate(e);
                Log.d(Constants.LOG_TAG, "", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (errorMessage != null) {
                Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(HomeActivity.this, EventsActivity.class);
                startActivity(intent);
            }
        }
    }
}
