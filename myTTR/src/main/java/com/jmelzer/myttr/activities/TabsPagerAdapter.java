package com.jmelzer.myttr.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;

//todo rename
public class TabsPagerAdapter extends FragmentPagerAdapter {
    Mannschaft mannschaft;
    Liga liga;

    public TabsPagerAdapter(FragmentManager fm, Mannschaft m) {
        super(fm);
        mannschaft = m;
    }

    public TabsPagerAdapter(FragmentManager fm, Liga l) {
        super(fm);
        liga = l;
    }

    @Override
    public Fragment getItem(int index) {
        LigaMannschaftOrLigaResultsFragment f;
        f = new LigaMannschaftOrLigaResultsFragment();
        f.setPos(index);
        f.setLiga(liga);
        f.setMannschaft(mannschaft);

        return f;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}
