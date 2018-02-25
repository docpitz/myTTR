package com.jmelzer.myttr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.Spieler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 21.02.2015.
 * Shows the results of a player in a saison.
 */
public class LigaSpielerResultsActivity extends BaseActivity {

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.getSelectedLigaSpieler() != null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.liga_spieler_results);

        Spieler spieler = MyApplication.getSelectedLigaSpieler();

        TextView textView = (TextView) findViewById(R.id.textName);
        textView.setText(MyApplication.getSelectedLigaSpieler().getName());

        textView = (TextView) findViewById(R.id.textMeldungen);
        textView.setText(spieler.getPosition());

        textView = (TextView) findViewById(R.id.textEinsaetze);
        String txt = "";
        for (Spieler.Einsatz einsatz : spieler.getEinsaetze()) {
            txt += einsatz.getLigaName() + "\n";
        }
        textView.setText(txt);

        textView = (TextView) findViewById(R.id.textBilanzen);
        txt = "";
        for (Spieler.Bilanz bilanz : spieler.getBilanzen()) {
            txt += bilanz.getKategorie() + ": " + bilanz.getErgebnis() + "\n";
        }
        textView.setText(txt);


        List<String> groupList = new ArrayList<>();
        List<List<Spieler.EinzelSpiel>> children = new ArrayList<>();

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.expandableListView);

        for (Spieler.LigaErgebnisse ergebnisse : spieler.getErgebnisse()) {
            groupList.add(ergebnisse.getName());
            children.add(ergebnisse.getSpiele());

        }
        listView.setAdapter(new ErgebnissAdapter(this, groupList, children));
    }


    class ErgebnissAdapter extends BaseExpandableListAdapter {
        List<String> groupList;
        List<List<Spieler.EinzelSpiel>> children;
        Context context;

        ErgebnissAdapter(Context context, List<String> groupList, List<List<Spieler.EinzelSpiel>> children) {
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
            final Spieler.EinzelSpiel childElem = (Spieler.EinzelSpiel) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.liga_spieler_result_row, null);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.datum);
            textView.setText(childElem.getDatum());
            textView = (TextView) convertView.findViewById(R.id.pos);
            textView.setText(childElem.getPos());
            textView = (TextView) convertView.findViewById(R.id.gegner);
            textView.setText(childElem.getGegner());
            //todo add context menu here for detail sets
            textView = (TextView) convertView.findViewById(R.id.ergebnis);
            textView.setText(childElem.getErgebnis());
//            textView = (TextView) convertView.findViewById(R.id.saetze);
//            textView.setText(childElem.getSaetze());
            textView = (TextView) convertView.findViewById(R.id.gegnerM);
            textView.setText(childElem.getGegnerMannschaft());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.liga_spieler_detail_actions, menu);
        return true;
    }

    public void details(MenuItem item) {
        if (MyApplication.getSelectedLigaSpieler().getMytTTClickTTUrl() == null) {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra(SearchActivity.INTENT_LIGA_PLAYER, true);
            intent.putExtra(SearchActivity.BACK_TO, EventsActivity.class);
            startActivity(intent);
        } else {
            Player p = new Player("", MyApplication.getSelectedLigaSpieler().getName());
            p.setPersonId(MyApplication.getSelectedLigaSpieler().getPersonId());

            new EventsAsyncTask(this, EventsActivity.class, p).execute();
        }
    }
}
