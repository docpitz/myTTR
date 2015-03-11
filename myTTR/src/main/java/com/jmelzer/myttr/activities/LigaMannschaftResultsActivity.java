package com.jmelzer.myttr.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
                false).size() > 0;
    }

    protected TabsPagerAdapter createTabsAdapter() {
        return new TabsPagerAdapter(getSupportFragmentManager(),
                MyApplication.selectedMannschaft);
    }


    public void bilanz(MenuItem item) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.liga_mannschaft_actions, menu);
        return true;
    }

    public void info(MenuItem item) {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, LigaMannschaftInfoActivity.class) {
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
}
