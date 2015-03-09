package com.jmelzer.myttr.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

import java.util.List;

/**
 * Created by J. Melzer on 21.02.2015.
 * Showing all results of actual liga.
 */
public class LigaAllResultsActivity extends AbstractLigaResultActivity {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liga_mannschaft_detail);

        init();

        setTitle(MyApplication.getSelectedLiga().getName() + " - Ergebnisse");

    }

    @Override
    boolean startWithRR() {
        return MyApplication.getSelectedLiga().getSpieleFor(null, false).size() > 0;
    }

    protected TabsPagerAdapter createTabsAdapter() {
        return new TabsPagerAdapter(getSupportFragmentManager(),
                MyApplication.getSelectedLiga());
    }


    public void bilanz(MenuItem item) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.liga_actions, menu);
        return true;
    }

}
