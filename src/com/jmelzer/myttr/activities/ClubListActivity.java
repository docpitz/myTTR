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
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;

import java.util.List;

public class ClubListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clublist);

        final ListView listview = (ListView) findViewById(R.id.listview);

        final PlayerAdapter adapter = new PlayerAdapter(this,
                                                        android.R.layout.simple_list_item_1,
                                                        AfterLoginActivity.players);
        listview.setAdapter(adapter);
    }

    class PlayerAdapter extends ArrayAdapter<Player> {

        public PlayerAdapter(Context context, int resource, List<Player> players) {
            super(context, resource, players);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.playerrow, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.firstname);
            String txt = AfterLoginActivity.players.get(position).getFirstname();
            if (txt != null) {
                textView.setText(txt);
            }
            textView = (TextView) rowView.findViewById(R.id.lastname);
            txt = AfterLoginActivity.players.get(position).getLastname();
            if (txt != null) {
                textView.setText(txt);
            }
            textView = (TextView) rowView.findViewById(R.id.points);
            txt = "" + AfterLoginActivity.players.get(position).getTtrPoints();
            if (txt != null) {
                textView.setText(txt);
            }


            return rowView;
        }
    }
}
