package com.jmelzer.myttr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.jmelzer.myttr.Group;
import com.jmelzer.myttr.KoPhase;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.Spieler;
import com.jmelzer.myttr.TournamentGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 25.06.2017.
 */
public class TournamentResultsActivity extends BaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.tournament_results);

        TextView clubNameView = findViewById(R.id.textViewName);
        clubNameView.setText(MyApplication.selectedTournament.getName());

        ExpandableListView listView = findViewById(R.id.expandableListView);
        listView.setAdapter(new TournamentResultsActivity.Adapter(this,
                prepareData(), listView));

        setTitle("Ergebnisse");

    }

    public static class Parent {
        String name;
        List<TournamentGame> children = new ArrayList<>();
    }

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.selectedCompetition != null;
    }

    private List<TournamentResultsActivity.Parent> prepareData() {
        List<TournamentResultsActivity.Parent> list = new ArrayList<>();

        for (Group group : MyApplication.selectedCompetition.getGroups()) {

            TournamentResultsActivity.Parent p = new TournamentResultsActivity.Parent();
            p.name = group.getName();
            for (TournamentGame tournamentGame : group.getGames()) {
                p.children.add(tournamentGame);
            }
            list.add(p);
        }

        for (KoPhase koPhase : MyApplication.selectedCompetition.getKoPhases()) {
            TournamentResultsActivity.Parent p = new TournamentResultsActivity.Parent();
            p.name = koPhase.getName();
            for (TournamentGame tournamentGame : koPhase.getGames()) {
                p.children.add(tournamentGame);
            }
            list.add(p);
        }

        return list;
    }


    public class Adapter extends BaseExpandableListAdapter {
        List<Parent> list;
        LayoutInflater layInflator;
        Context context;
        ExpandableListView expandableListView;

        public Adapter(Context context, List<Parent> data, ExpandableListView expandableListView) {
            this.list = data;
            this.context = context;
            layInflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.expandableListView = expandableListView;
        }

        @Override
        public int getGroupCount() {
            return list.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return 1;
        }


        @Override
        public Object getGroup(int groupPosition) {
            return list.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return list.get(groupPosition).children.get(childPosition);
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
            Parent parent1 = (Parent) getGroup(groupPosition);
            if (convertView == null) {
                convertView = layInflator.inflate(R.layout.liga_spieler_result_header, null);
            }
            TextView lblListHeader = convertView.findViewById(R.id.groupName);
            lblListHeader.setText(parent1.name);
            return convertView;
        }


        @Override
        public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Parent parent = (Parent) getGroup(groupPosition);

            DebugListView listView = new DebugListView(context);
            final SearchResultAdapter adapter = new SearchResultAdapter(context,
                    android.R.layout.simple_list_item_1,
                    parent.children);

            listView.setAdapter(adapter);
            listView.setRows(parent.children.size());
            listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // disallow the onTouch for your scrollable parent view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            return listView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }
    }

    class SearchResultAdapter extends ArrayAdapter<TournamentGame> {
        Context context;
        public SearchResultAdapter(Context context, int resource, List<TournamentGame> games) {
            super(context, resource, games);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.tournament_result_row, null);
            }
            final TournamentGame game = getItem(position);
            TextView textView = convertView.findViewById(R.id.player1);
            textView.setText(game.getSpieler1Name());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSearch(game.getSpieler1Name());
                }
            });
            textView = convertView.findViewById(R.id.player2);
            textView.setText(game.getSpieler2Name());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSearch(game.getSpieler2Name());
                }
            });
            textView = convertView.findViewById(R.id.result);
            textView.setText(game.getResult());
            textView = convertView.findViewById(R.id.sets);
            textView.setText(game.getSetsInARow());
            return convertView;
        }
    }

    private void startSearch(final String name) {
        MyApplication.selectedLigaSpieler = new Spieler(name);
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.INTENT_LIGA_PLAYER, true);
        intent.putExtra(SearchActivity.BACK_TO, EventsActivity.class);
        startActivity(intent);
    }
}
