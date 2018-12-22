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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.logic.impl.MytClickTTWrapper;
import com.jmelzer.myttr.model.Favorite;

import java.util.List;

import static com.jmelzer.myttr.MyApplication.actualTTR;
import static com.jmelzer.myttr.MyApplication.selectedMannschaft;

public class TeamHomeActivity extends BaseActivity {

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
        final Liga liga = new Liga("", "https://www.mytischtennis.de/clicktt/home-tab?id=gruppe");
        liga.setUrlVR("https://www.mytischtennis.de/clicktt/home-tab?id=plan");
        liga.setUrlRR("https://www.mytischtennis.de/clicktt/home-tab?id=plan");
        MyApplication.setSelectedLiga(liga);

        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(TeamHomeActivity.this, LigaMannschaftResultsActivity.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException, NoClickTTException, NoDataException, ValidationException {
                MyTischtennisParser p = new MyTischtennisParser();
                MyApplication.selectedMannschaft = p.readOwnTeam();
                MyApplication.setSelectedLiga(selectedMannschaft.getLiga());
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedMannschaft != null;
            }


        };
        task.execute();
    }
}
