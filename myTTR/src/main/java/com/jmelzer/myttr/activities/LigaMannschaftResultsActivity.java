package com.jmelzer.myttr.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.ClickTTParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;

/**
 * Created by J. Melzer on 21.02.2015.
 * Shows the results of a team in a saison.
 */
public class LigaMannschaftResultsActivity extends AbstractLigaResultActivity {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liga_mannschaft_detail);

        init();

        setTitle(MyApplication.selectedMannschaft.getName() + " - Ergebnisse");

    }

    @Override
    boolean startWithRR() {
        return MyApplication.getSelectedLiga().getSpieleFor(MyApplication.selectedMannschaft.getName(),
                Liga.Spielplan.RR).size() > 0;
    }

    protected LigaTabsPagerAdapter createTabsAdapter() {
        return new LigaTabsPagerAdapter(getSupportFragmentManager(),
                MyApplication.selectedMannschaft);
    }


    public void bilanz(MenuItem item) {
        readInfoAndStartActivity(LigaMannschaftBilanzActivity.class);

    }

    private void readInfoAndStartActivity(Class clz) {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, clz) {
            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                new ClickTTParser().readMannschaftsInfo(MyApplication.selectedMannschaft);
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedMannschaft.getKontakt() != null;
            }

        };
        task.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.liga_mannschaft_actions, menu);
        return true;
    }

    public void info(MenuItem item) {
        readInfoAndStartActivity(LigaMannschaftInfoActivity.class);
    }
}
