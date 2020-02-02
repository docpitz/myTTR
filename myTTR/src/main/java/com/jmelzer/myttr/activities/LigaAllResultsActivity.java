package com.jmelzer.myttr.activities;

import android.os.Bundle;
import android.view.Menu;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

import java.util.List;

/**
 * Created by J. Melzer on 21.02.2015.
 * Showing all results of actual liga.
 */
public class LigaAllResultsActivity extends AbstractLigaResultActivity {

    @Override
    protected boolean checkIfNeccessryDataIsAvailable() {
        return MyApplication.getSelectedLiga() != null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.liga_mannschaft_detail);

        init();

        setTitle(MyApplication.getSelectedLiga().getName() + " - Ergebnisse");

    }

    @Override
    boolean startWithRR() {
        List<Mannschaftspiel> spiele = MyApplication.getSelectedLiga().getSpieleFor(null, Liga.Spielplan.RR);
        return  (spiele.size() > 0 && spiele.get(0).getErgebnis() != null);
    }

    protected LigaTabsPagerAdapter createTabsAdapter() {
        return new LigaTabsPagerAdapter(getSupportFragmentManager(),
                MyApplication.getSelectedLiga());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
