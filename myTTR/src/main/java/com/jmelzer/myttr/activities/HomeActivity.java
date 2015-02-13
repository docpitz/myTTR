/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;

public class HomeActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);


//        startService(new Intent(this, SyncManager.class));
//        new SyncManager().startService();

    }

    public void manual(final View view) {

        Intent target = new Intent(this, TTRCalculatorActivity.class);
        startActivity(target);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (MyApplication.getPoints() < 0) {
            Intent target = new Intent(this, EnterTTRActivity.class);
            startActivity(target);
        }
    }

    public void clublist(View view) {
        AsyncTask<String, Void, Integer> task = new ClubListAsyncTask(this, ClubListActivity.class);
        task.execute();
    }

    public void statistik(View view) {
        MyApplication.selectedPlayer = null;
        AsyncTask<String, Void, Integer> task = new EventsAsyncTask(this, EventsActivity.class);
        task.execute();
    }

    public void player_sim(View view) {
        AsyncTask<String, Void, Integer> task = new OwnClubListAsyncTask(this,
                SelectTeamPlayerActivity.class);
        task.execute();
    }

    public void search(View view) {
        Intent target = new Intent(this, SearchActivity.class);
        target.putExtra(SearchActivity.BACK_TO, EventsActivity.class);
        startActivity(target);
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

    class OwnClubListAsyncTask extends BaseAsyncTask {


        OwnClubListAsyncTask(Activity parent, Class targetClz) {
            super(parent, targetClz);
        }

        @Override
        protected void callParser() throws NetworkException, LoginExpiredException {
            MyApplication.myTeamPlayers = new MyTischtennisParser().readPlayersFromTeam(null);
        }

        @Override
        protected boolean dataLoaded() {
            return MyApplication.myTeamPlayers != null;
        }

    }
}
