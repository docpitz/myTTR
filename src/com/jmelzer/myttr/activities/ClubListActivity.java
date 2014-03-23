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
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.Util;

import java.util.List;

public class ClubListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clublist);

        final ListView listview = (ListView) findViewById(R.id.listview);
        final PlayerAdapter adapter = new PlayerAdapter(this,
                                                        android.R.layout.simple_list_item_1,
                                                        MyApplication.clubPlayers);
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
            Player player = MyApplication.clubPlayers.get(position);

            TextView txtViewNumber  = (TextView) rowView.findViewById(R.id.number);
            txtViewNumber.setText("" + (position+1));

            TextView textView = (TextView) rowView.findViewById(R.id.firstname);
            TextView textViewFirstName = textView;
            String txt = player.getFirstname();
            String firstName = txt;
            //max 15 characters
            textView.setText(Util.abbreviate(txt, 0, 8));
            textView = (TextView) rowView.findViewById(R.id.lastname);
            txt = player.getLastname();
            String name = firstName + " " + txt;
            if (name.equals(MyApplication.loginUser.getRealName())) {
                textView.setTypeface(null, Typeface.BOLD);
                textViewFirstName.setTypeface(null, Typeface.BOLD);
            } else {
                textView.setTypeface(null, Typeface.NORMAL);
                textViewFirstName.setTypeface(null, Typeface.NORMAL);
            }

            textView.setText(Util.abbreviate(txt, 0, 15));

            textView = (TextView) rowView.findViewById(R.id.points);
            txt = "" + player.getTtrPoints();
            textView.setText(txt);

            return rowView;
        }
    }


}
