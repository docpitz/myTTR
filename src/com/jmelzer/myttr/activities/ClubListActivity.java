/*
 * Copyright (c) Juergen Melzer
 *
 * 2014.
 */

/*
* Author: J. Melzer
* Date: 09.03.14 
*
*/


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;

public class ClubListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clublist);

        final ListView listview = (ListView) findViewById(R.id.listview);
        final PlayerAdapter adapter = new PlayerAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.clubPlayers);
        listview.setAdapter(adapter);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                view.setSelected(true);
                if (position > -1 && position < MyApplication.clubPlayers.size()) {
                    Player p = MyApplication.clubPlayers.get(position);

                    new EventsAsyncTask(ClubListActivity.this, EventsActivity.class, p).execute();
                    return true;
                }
                return false;
            }
        });
    }

}
