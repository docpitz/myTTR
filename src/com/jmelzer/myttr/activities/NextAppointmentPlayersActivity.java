package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

/**
 * TODO
 * User: jmelzer
 * Date: 22.03.14
 * Time: 13:06
 */
public class NextAppointmentPlayersActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clublist);

        final ListView listview = (ListView) findViewById(R.id.listview);
        final PlayerAdapter adapter = new PlayerAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.foreignTeamPlayers);
        listview.setAdapter(adapter);
    }
}
