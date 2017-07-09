/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.ClickTTParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.SyncManager;

public class HomeActivity extends BaseActivity {

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.home);

        if (!isMyServiceRunning(SyncManager.class)) {
            Log.d(Constants.LOG_TAG, "SyncManager will be started");
            startService(new Intent(this, SyncManager.class));
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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

    public void liga(View view) {
        MyApplication.selectedVerband = null;
        AsyncTask<String, Void, Integer> task = new LigenAsyncTask(this, LigaHomeActivity.class);
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

    public void ligaranking(View view) {
        AsyncTask<String, Void, Integer> task = new LigaRanglisteAsyncTask(this);
        task.execute();
    }

    public void info(View view) {
        AboutDialog about = new AboutDialog(this);
        about.show();
    }

    public void tournament(View view) {
        Intent target = new Intent(this, TournamentsActivity.class);
        startActivity(target);
    }

    public void cups(View view) {
        Intent target = new Intent(this, CupsActivity.class);
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

    private class LigaRanglisteAsyncTask extends BaseAsyncTask {

        public LigaRanglisteAsyncTask(Activity parent) {
            super(parent, LigaRanglisteActivity.class);
        }

        @Override
        protected void callParser() throws NetworkException, LoginExpiredException {
            MyApplication.myTTLigen = new MyTischtennisParser().readOwnLigaRanking();
        }


        @Override
        protected boolean dataLoaded() {
            return MyApplication.myTTLigen != null;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_actions, menu);
        menu.getItem(0).setVisible(MyApplication.simPlayer != null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings: {
                Intent intent = new Intent(this, MySettingsActivity.class);
                startActivity(intent);
                break;
            }
//            case R.id.action_home: {
//                Intent intent = new Intent(this, HomeActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                break;
//            }
            case R.id.action_logout: {
                new LoginManager().logout();
                MyApplication.createEmptyUser();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra(LoginActivity.NOAUTOLOGIN, true);
                startActivity(intent);
                break;
            }
            case R.id.action_remove_sim: {
                MyApplication.simPlayer = null;
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Simulation wurde beendet", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.menu_about: {
                AboutDialog about = new AboutDialog(this);
                about.show();
                break;
            }
            case R.id.menu_impressum: {
                ImpressumDialog dialog = new ImpressumDialog(this);
                dialog.setTitle("Impressum");
                dialog.show();
                break;
            }
        }
        return false;
    }
}
