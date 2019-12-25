package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.Competition;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.Tournament;
import com.jmelzer.myttr.logic.ClickTTParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.util.GoogleMapStarter;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by J. Melzer on 17.06.2017.
 */
public class TournamentDetailActivity extends BaseActivity {
    static final int SPIELLOKALE_POS = 2;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.tournament_detail);

        TextView clubNameView = findViewById(R.id.textViewName);
        clubNameView.setText(MyApplication.selectedTournament.getName());

        ExpandableListView listView = findViewById(R.id.expandableListView);
        listView.setAdapter(new TournamentDetailActivity.Adapter(this,
                prepareData(MyApplication.selectedTournament)));

        final ListView listview = findViewById(R.id.competitionlist);
        final CompetitionAdapter adapter = new CompetitionAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.selectedTournament.getCompetitions());
        listview.setAdapter(adapter);
    }

    private String buildInfo(Tournament tournament) {
        String s = "";

        if (tournament.getLongDate() != null)
            s += tournament.getLongDate() + "\n";
        if (tournament.getTurnierArt() != null)
            s += "Turnierart: " + tournament.getTurnierArt() + "\n";
        if (tournament.getRanglistenbezug() != null)
            s += "Ranglistenbezug: " + tournament.getRanglistenbezug() + "\n";
        if (tournament.getPriceMoney() != null)
            s += "Preisgelder/Sachpreise: " + tournament.getPriceMoney() + "\n";
        if (tournament.getTurnierhomepage() != null)
            s += "Homepage: " + tournament.getTurnierhomepage() + "\n"; //todo add clickable
        return s;
    }

    public static class Child {
        String text;

        public Child(String text) {
            this.text = text;
        }
    }

    public static class Parent {
        String name;
        List<Child> children = new ArrayList<>();
    }

    @Override
    protected boolean checkIfNeccessryDataIsAvailable() {
        return MyApplication.myTournaments != null;
    }

    private List<TournamentDetailActivity.Parent> prepareData(Tournament tournament) {
        List<TournamentDetailActivity.Parent> list = new ArrayList<>();

        TournamentDetailActivity.Parent p = new TournamentDetailActivity.Parent();
        p.name = "Info";
        p.children.add(new TournamentDetailActivity.Child(buildInfo(tournament)));
        list.add(p);

        p = new TournamentDetailActivity.Parent();
        p.name = "Kontakt";
        String c = "";
        if (tournament.getContact() != null)
            c += tournament.getContact();
        if (tournament.getEmail() != null) {
            c += "\nE-Mail:" + tournament.getEmail();
        }
        p.children.add(new TournamentDetailActivity.Child(c));
        list.add(p);

        p = new TournamentDetailActivity.Parent();
        p.name = "Austragungsorte";
        p.children.add(new TournamentDetailActivity.Child(tournament.getLocation() == null ? "Unbekannt" : tournament.getLocation()));
        list.add(p);

        p = new TournamentDetailActivity.Parent();
        p.name = "Informationen zur Meldung";
        p.children.add(new TournamentDetailActivity.Child(tournament.getRegistrationInfo() == null ? "Unbekannt" : tournament.getRegistrationInfo()));
        list.add(p);

        p = new TournamentDetailActivity.Parent();
        p.name = "Material";
        p.children.add(new TournamentDetailActivity.Child(tournament.getMaterial() == null ? "Unbekannt" : tournament.getMaterial()));
        list.add(p);

        return list;
    }


    public class Adapter extends BaseExpandableListAdapter {
        List<Parent> list;
        LayoutInflater layInflator;
        Context context;

        public Adapter(Context context, List<Parent> data) {
            this.list = data;
            this.context = context;
            layInflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            convertView = infalInflater.inflate(R.layout.liga_verein_row, null);
            final Child childElem = (Child) getChild(groupPosition, childPosition);

            if (groupPosition == SPIELLOKALE_POS) {
                final String text = childElem.text;
                convertView = infalInflater.inflate(R.layout.liga_spiellokal_row, null);
                ((TextView) convertView.findViewById(R.id.name)).setText(text);
                if (text != null) {
                    convertView.findViewById(R.id.map).setVisibility(View.VISIBLE);
                    convertView.findViewById(R.id.map).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GoogleMapStarter.showMap(TournamentDetailActivity.this, text);
                        }
                    });
                } else {
                    convertView.findViewById(R.id.map).setVisibility(View.INVISIBLE);
                }
            } else {
                TextView textView = convertView.findViewById(R.id.child1);
                textView.setText(((Child) getChild(groupPosition, childPosition)).text);
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }
    }

    private static class ViewHolder {
        int id;
        TextView textName;
        TextView textQttr;
        TextView textOpenFor;
        TextView textDate;
        TextView textttrRelevant;
        TextView textResults;
        TextView textParticipants;
    }

    class CompetitionAdapter extends ArrayAdapter<Competition> {
        private LayoutInflater layoutInflater;

        public CompetitionAdapter(Context context, int resource, List<Competition> competitionList) {
            super(context, resource, competitionList);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.competition_row, null);
                holder = new ViewHolder();
                holder.textName = convertView.findViewById(R.id.name);
                holder.textQttr = convertView.findViewById(R.id.qttr);
                holder.textOpenFor = convertView.findViewById(R.id.openFor);
                holder.textDate = convertView.findViewById(R.id.date);
                holder.textttrRelevant = convertView.findViewById(R.id.ttrRelevant);
                holder.textResults = convertView.findViewById(R.id.results);
                holder.textParticipants = convertView.findViewById(R.id.teilnehmer);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Competition competition = getItem(position);

            holder.id = position;
            holder.textName.setText(competition.getName());
            holder.textQttr.setText(competition.getQttr());
            holder.textDate.setText(competition.getDate());
            holder.textOpenFor.setText(competition.getOpenFor());
            holder.textttrRelevant.setText(competition.getTtrRelevant());
            if (competition.getParticipants() != null || competition.getResults() != null)
                TournamentDetailActivity.this.registerForContextMenu(convertView);
            if (competition.getParticipants() == null)
                holder.textParticipants.setVisibility(INVISIBLE);
            else
                holder.textParticipants.setVisibility(VISIBLE);
            if (competition.getResults() == null)
                holder.textResults.setVisibility(INVISIBLE);
            else
                holder.textResults.setVisibility(VISIBLE);
            return convertView;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Aktionen");
        ViewHolder holder = (ViewHolder) v.getTag();
        List<Competition> list = MyApplication.selectedTournament.getCompetitions();
        Competition competition = list.get(holder.id);
        if (competition.getParticipants() != null)
            menu.add(1, holder.id, 1, "Teilnehmer");

        if (competition.getResults() != null)
            menu.add(2, holder.id, 2, "Ergebnisse");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getGroupId()) {
            case 1:
                callParticipant(item.getItemId());
                break;
            case 2:
                callResults(item.getItemId());
                break;

        }
        return super.onContextItemSelected(item);
    }

    private void callParticipant(int itemId) {
        MyApplication.selectedCompetition = MyApplication.selectedTournament.getCompetitions().get(itemId);
        AsyncTask<String, Void, Integer> task = new ParticipantAsyncTask(this, ParticipantActivity.class);
        task.execute();

    }


    private void callResults(int itemId) {
        MyApplication.selectedCompetition = MyApplication.selectedTournament.getCompetitions().get(itemId);
        AsyncTask<String, Void, Integer> task = new ResultsAsyncTask(this, TournamentResultsActivity.class);
        task.execute();

    }

    private class ParticipantAsyncTask extends BaseAsyncTask {

        public ParticipantAsyncTask(Activity parent, Class targetClz) {
            super(parent, targetClz);
        }

        @Override
        protected void callParser() throws NetworkException, LoginExpiredException {
            new ClickTTParser().readTournamentParticipants(MyApplication.selectedCompetition);
        }


        @Override
        protected boolean dataLoaded() {
            return MyApplication.selectedCompetition.getParticipantList().size() > 0;
        }
    }

    private class ResultsAsyncTask extends BaseAsyncTask {

        public ResultsAsyncTask(Activity parent, Class targetClz) {
            super(parent, targetClz);
        }

        @Override
        protected void callParser() throws NetworkException, LoginExpiredException {
            new ClickTTParser().readTournamentResults(MyApplication.selectedCompetition);
        }


        @Override
        protected boolean dataLoaded() {
            return MyApplication.selectedCompetition.getGroups().size() > 0 ||
                    MyApplication.selectedCompetition.getKoPhases().size() > 0;
        }
    }
}
