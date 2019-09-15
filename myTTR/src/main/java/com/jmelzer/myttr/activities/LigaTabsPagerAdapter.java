package com.jmelzer.myttr.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.MyApplication;

/**
 * Adapter for the tabs in liga result.
 */
public class LigaTabsPagerAdapter extends FragmentPagerAdapter {
    Mannschaft mannschaft;
    Liga liga;

    public LigaTabsPagerAdapter(FragmentManager fm, Mannschaft m) {
        super(fm);
        mannschaft = m;
    }

    public LigaTabsPagerAdapter(FragmentManager fm, Liga l) {
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
        if (MyApplication.getSelectedLiga().getUrlGesamt() != null) {
            return 1;
        }
        return 2;
    }

}
