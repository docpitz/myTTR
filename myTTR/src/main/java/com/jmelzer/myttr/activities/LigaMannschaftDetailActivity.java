package com.jmelzer.myttr.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

import java.lang.reflect.Method;

/**
 * Created by J. Melzer on 21.02.2015.
 */
public class LigaMannschaftDetailActivity extends AbstractLigaResultActivity {



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liga_mannschaft_detail);

        init();

        setTitle(MyApplication.selectedMannschaft.getName() + " - Ergebnisse");



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

}
