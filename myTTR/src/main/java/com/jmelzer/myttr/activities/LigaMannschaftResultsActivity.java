package com.jmelzer.myttr.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.ClickTTParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;

import java.util.List;

/**
 * Created by J. Melzer on 21.02.2015.
 * Shows the results of a team in a saison.
 */
public class LigaMannschaftResultsActivity extends AbstractLigaResultActivity {


    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.getSelectedLiga() != null;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.liga_mannschaft_detail);

        init();

        setTitle(MyApplication.selectedMannschaft.getName() + " - Ergebnisse");

    }

    @Override
    boolean startWithRR() {
        List<Mannschaftspiel> spiele = MyApplication.getSelectedLiga().getSpieleFor(MyApplication.selectedMannschaft.getName(),
                Liga.Spielplan.RR);
        return  (spiele.size() > 0 && spiele.get(0).getErgebnis() != null);
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

    public void verein(MenuItem item) {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, LigaVereinActivity.class) {
            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                ClickTTParser parser = new ClickTTParser();
                if (MyApplication.selectedMannschaft.getVereinUrl() == null) {
                    parser.readMannschaftsInfo(MyApplication.selectedMannschaft);
                }
                MyApplication.selectedVerein = parser.readVerein(MyApplication.selectedMannschaft);
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedVerein != null;
            }

        };
        task.execute();
    }
}
