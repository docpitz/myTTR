/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

public class TeamHomeActivity extends BaseActivity {

    @Override
    protected boolean checkIfNeccessryDataIsAvailable() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.team_home);
    }


    public void ligaranking(View view) {
        AsyncTask<String, Void, Integer> task = new LigaRanglisteAsyncTask(this);
        task.execute();
    }


    public void tabelle(View view) {
        Liga liga = new Liga("", "https://www.mytischtennis.de/clicktt/home-tab?id=gruppe");
        MyApplication.setSelectedLiga(liga);

        AsyncTask<String, Void, Integer> task = new LigaTabelleAsyncTask(this);
        task.execute();
    }

    public void spielplan(View view) {
        new ReadOwnTeamTask(this, LigaMannschaftResultsActivity.class).execute();
    }

    public void aufstellungen(View view) {
        new ReadOwnTeamTask(this, LigaMannschaftBilanzActivity.class).execute();
    }

    public void club(View view) {
        new ReadOwnClubTask(this, LigaVereinActivity.class).execute();
    }

    public void otherTeam(View view) {
        AsyncTask<String, Void, Integer> task = new SelectOtherTeamAsyncTask(this, TeamHomeActivity.class);
        task.execute();
    }
}
