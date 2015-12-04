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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.MyTTLiga;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.UIUtil;

import java.util.ArrayList;
import java.util.List;

public class LigaRanglisteActivity extends BaseActivity {
    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.myTTLigen != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.ligarangliste);

        List<String> groupList = new ArrayList<>();
        List<List<Player>> children = new ArrayList<>();

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.expandableListView);
        List<MyTTLiga> myTTLigen = MyApplication.myTTLigen;
        for (MyTTLiga myTTLiga : myTTLigen) {
            groupList.add(myTTLiga.getLigaName());
            children.add(myTTLiga.getRanking());
        }

        listView.setAdapter(new PlayerAdapter(this, groupList, children));

        //todo
//        final MyTTLiga myTTLiga = MyApplication.myTTLigen.get(0);
//        final TextView ligaNameView = (TextView) findViewById(R.id.textViewLigaName);
//        ligaNameView.setText(myTTLiga.getLigaName());
//        final ListView listview = (ListView) findViewById(R.id.listview);
//
//        final PlayerAdapter adapter = new PlayerAdapter(this,
//                android.R.layout.simple_list_item_1,
//                myTTLiga.getRanking());
//        listview.setAdapter(adapter);
//

    }

    private static class ViewHolder {
        TextView textNr;
        TextView textFirstName;
        TextView textLastname;
        TextView textTtr;
    }

    class PlayerAdapter extends BaseExpandableListAdapter {
        List<String> groupList;
        List<List<Player>> children;
        Context context;

        public PlayerAdapter(Context context, List<String> groupList, List<List<Player>> children) {
            this.context = context;
            this.groupList = groupList;
            this.children = children;
        }


        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return children.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return children.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater layInflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layInflator.inflate(R.layout.liga_spieler_result_header, null);
            }
            TextView lblListHeader = (TextView) convertView.findViewById(R.id.groupName);
            lblListHeader.setText(headerTitle);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final Player player = (Player) getChild(groupPosition, childPosition);
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.playerrow, null);

                holder = new ViewHolder();
                holder.textNr = (TextView) convertView.findViewById(R.id.number);
                holder.textFirstName = (TextView) convertView.findViewById(R.id.firstname);
                holder.textLastname = (TextView) convertView.findViewById(R.id.lastname);
                holder.textTtr = (TextView) convertView.findViewById(R.id.points);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textNr.setText("" + (childPosition + 1));

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
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    new EventsAsyncTask(LigaRanglisteActivity.this, EventsActivity.class, player).execute();
                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
