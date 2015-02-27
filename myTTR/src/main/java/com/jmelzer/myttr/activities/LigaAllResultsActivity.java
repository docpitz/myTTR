package com.jmelzer.myttr.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

/**
 * Created by J. Melzer on 21.02.2015.
 * Showing all results of actual liga.
 */
public class LigaAllResultsActivity extends AbstractLigaResultActivity {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liga_mannschaft_detail);

        init();


        setTitle(MyApplication.selectedLiga.getName() + " - Ergebnisse");

    }

    protected TabsPagerAdapter createTabsAdapter() {
        return new TabsPagerAdapter(getSupportFragmentManager(),
                MyApplication.selectedLiga);
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
