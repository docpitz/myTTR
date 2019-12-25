package com.jmelzer.myttr.activities;

import android.os.Bundle;
import android.widget.ListView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.impl.MytClickTTWrapper;

/**
 * Created by J. Melzer on 03.10.2018.
 * Shows the results of a team in a saison.
 */
public class TeamSpielPlanActivity extends BaseActivity {
    MytClickTTWrapper clickTTWrapper = new MytClickTTWrapper();
    SpielAdapter adapter;

    @Override
    protected boolean checkIfNeccessryDataIsAvailable() {
        return MyApplication.selectedMannschaft != null;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.liga_mannschaft_results);

        setTitle("Spielplan");
        final ListView listview = findViewById(R.id.liga_mannschaft_detail_row);

        //create emtpty list
        adapter = new SpielAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.selectedMannschaft.getSpiele());
        listview.setAdapter(adapter);

    }

}
