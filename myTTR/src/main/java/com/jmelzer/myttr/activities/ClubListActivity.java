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

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.UIUtil;

import java.util.List;

public class ClubListActivity extends BaseActivity {
    DetailHelper detailHelper;

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.clubPlayers != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.clublist);

        final ListView listview = findViewById(R.id.listview);
        final PlayerAdapter adapter = new PlayerAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.clubPlayers);
        listview.setAdapter(adapter);
        detailHelper = new DetailHelper(this, listview, MyApplication.clubPlayers);

//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                view.setSelected(true);
//                if (position > -1 && position < MyApplication.clubPlayers.size()) {
//                    Player p = MyApplication.clubPlayers.get(position);
//
//                    new EventsAsyncTask(ClubListActivity.this, EventsActivity.class, p).execute();
//                }
//            }
//        });
    }

    private static class ViewHolder {
        TextView textNr;
        TextView textFirstName;
        TextView textLastname;
        TextView textTtr;
    }

    class PlayerAdapter extends ArrayAdapter<Player> {
        private LayoutInflater layoutInflater;

        public PlayerAdapter(Context context, int resource, List<Player> players) {
            super(context, resource, players);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.playerrow, parent, false);
                holder = new ViewHolder();
                holder.textNr = (TextView) convertView.findViewById(R.id.number);
                holder.textFirstName = (TextView) convertView.findViewById(R.id.firstname);
                holder.textLastname = (TextView) convertView.findViewById(R.id.lastname);
                holder.textTtr = (TextView) convertView.findViewById(R.id.points);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Player player = getItem(position);

            holder.textNr.setText("" + (position + 1));

            String txt = player.getFirstname();
            String firstName = txt;
            //max 15 characters
            holder.textFirstName.setText(UIUtil.abbreviate(txt, 0, 15));
            txt = player.getLastname();
            String name = firstName + " " + txt;
            if (name.equals(MyApplication.getLoginUser().getRealName())) {
                holder.textFirstName.setTypeface(null, Typeface.BOLD);
                holder.textLastname.setTypeface(null, Typeface.BOLD);
            } else {
                holder.textFirstName.setTypeface(null, Typeface.NORMAL);
                holder.textLastname.setTypeface(null, Typeface.NORMAL);
            }

            if (player.getTtrPoints() > 0) {
                holder.textLastname.setText(UIUtil.abbreviate(txt, 0, 15));
            } else {
                holder.textLastname.setText(UIUtil.abbreviate(txt, 0, 30));
            }

            if (player.getTtrPoints() > 0) {
                txt = "" + player.getTtrPoints();
            } else {
                txt = "";
            }
            holder.textTtr.setText(txt);

            return convertView;
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        detailHelper.createMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return detailHelper.onSelect(item);
    }
}
