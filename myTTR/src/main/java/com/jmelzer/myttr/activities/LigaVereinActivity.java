package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.logic.impl.MytClickTTWrapper;
import com.jmelzer.myttr.model.Verein;
import com.jmelzer.myttr.util.GoogleMapStarter;
import com.jmelzer.myttr.util.UrlUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 05.06.2015.
 * Shows the informations  of the club.
 */
public class LigaVereinActivity extends BaseActivity {

    static final int KONTAKT_POS = 0;
    static final int SPIELLOKALE_POS = 1;
    static final int SPIELPLAN_POS = 2;
    static final int MANNSCHAFTEN_POS = 3;

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.selectedVerein != null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.liga_verein);

        Verein verein = MyApplication.selectedVerein;
        ExpandableListView listView = findViewById(R.id.expandableListView);
        TextView clubNameView = findViewById(R.id.textViewClub);
        clubNameView.setText(verein.getName());

        listView.setAdapter(new VereinAdapter(this, prepareData(verein)));
    }

    private List<Parent> prepareData(Verein verein) {
        List<Parent> list = new ArrayList<>();
        Parent p = new Parent();
        p.name = "Kontakt";
        //todo telefon stripping?
        p.children.add(new Child(verein.getKontakt() == null ? "Unbekannt" : verein.getKontakt().getNameAddress()));
        list.add(p);
        p = new Parent();
        p.name = "Spiellokale";
        for (Verein.SpielLokal l : verein.getLokale()) {
            p.children.add(new Child(l.formatted()));
        }
        for (String s : verein.getLokaleUnformatted()) {
            p.children.add(new Child(s));
        }
        list.add(p);

        p = new Parent();
        p.name = "Spielplan";
        for (Mannschaftspiel ms : verein.getLetzteSpiele()) {
            p.children.add(new Child(ms));
        }
        for (Mannschaftspiel ms : verein.getSpielplan()) {
            p.children.add(new Child(ms));

        }
        list.add(p);

        p = new Parent();
        p.name = "Mannschaften";
        for (Verein.Mannschaft m : verein.getMannschaften()) {
            p.children.add(new Child(m));
        }
        list.add(p);

        return list;
    }

    public void ligaResults(MenuItem item) {
        //todo
    }

    public static class Child {
        Verein.Mannschaft mannschaft;
        String text;
        Mannschaftspiel mannschaftspiel;

        public Child(String text) {
            this.text = text;
        }

        public Child(Verein.Mannschaft mannschaft) {
            this.mannschaft = mannschaft;
        }

        public Child(Mannschaftspiel mannschaftspiel) {
            this.mannschaftspiel = mannschaftspiel;
        }
    }

    public static class Parent {
        String name;
        List<Child> children = new ArrayList<>();
    }

    class VereinAdapter extends BaseExpandableListAdapter {
        LayoutInflater layInflator;
        List<Parent> list;
        Context context;

        VereinAdapter(Context context, List<Parent> groupList) {
            this.context = context;
            this.list = groupList;
            layInflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getGroupCount() {
            return list.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return list.get(groupPosition).children.size();
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
            TextView lblListHeader = (TextView) convertView.findViewById(R.id.groupName);
            lblListHeader.setText(parent1.name);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final Child childElem = (Child) getChild(groupPosition, childPosition);
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (groupPosition == SPIELPLAN_POS) {
                convertView = infalInflater.inflate(R.layout.liga_mannschaft_results_row, null);
                ((TextView) convertView.findViewById(R.id.date)).setText(childElem.mannschaftspiel.getDate());
                ((TextView) convertView.findViewById(R.id.heim)).setText(childElem.mannschaftspiel.getHeimMannschaft().getName());
                ((TextView) convertView.findViewById(R.id.gast)).setText(childElem.mannschaftspiel.getGastMannschaft().getName());
                ((TextView) convertView.findViewById(R.id.result)).setText(childElem.mannschaftspiel.getErgebnis());
                convertView.findViewById(R.id.map).setVisibility(View.VISIBLE);
            } else if (groupPosition == SPIELLOKALE_POS) {
                final String text = childElem.text;
                convertView = infalInflater.inflate(R.layout.liga_spiellokal_row, null);
                ((TextView) convertView.findViewById(R.id.name)).setText(text);
                if (text != null) {
                    convertView.findViewById(R.id.map).setVisibility(View.VISIBLE);
                    convertView.findViewById(R.id.map).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GoogleMapStarter.showMap(LigaVereinActivity.this, text);
                        }
                    });
                } else {
                    convertView.findViewById(R.id.map).setVisibility(View.INVISIBLE);
                }

            } else if (groupPosition == MANNSCHAFTEN_POS) {
                final Verein.Mannschaft m = childElem.mannschaft;
                convertView = infalInflater.inflate(R.layout.liga_verein_mannschaft_row, null);
                ((TextView) convertView.findViewById(R.id.name)).setText(m.name);
                ((TextView) convertView.findViewById(R.id.liga)).setText(m.liga);
                convertView.findViewById(R.id.liga).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoLiga(m);
                    }
                });
                convertView.findViewById(R.id.arrow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoLiga(m);
                    }
                });

            } else {
                //we can not reuse the view here
                convertView = infalInflater.inflate(R.layout.liga_verein_row, null);
//todo make different lyouts for different views
                TextView textView = (TextView) convertView.findViewById(R.id.child1);
                textView.setText(childElem.text);
            }
            final ImageView arrow = convertView.findViewById(R.id.arrow);
            final ImageView map = convertView.findViewById(R.id.map);
            if (childElem.mannschaftspiel != null) {
                if (childElem.mannschaftspiel.getUrlDetail() == null) {
                    arrow.setVisibility(View.INVISIBLE);
                    if (childElem.mannschaftspiel.getNrSpielLokal() > -1) {
                        map.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (childElem.mannschaftspiel.getHeimMannschaft().getSpielLokale().size() == 0) {
                                    new ReadInfoAsyncTask(childElem.mannschaftspiel.getHeimMannschaft(),
                                            childElem.mannschaftspiel.getNrSpielLokal(), LigaVereinActivity.this).execute();
                                } else
                                    GoogleMapStarter.showMap(LigaVereinActivity.this, childElem.mannschaftspiel.getActualSpiellokal());
                            }
                        });
                    } else {
                        map.setVisibility(View.INVISIBLE);
                    }
                } else {
                    map.setVisibility(View.INVISIBLE);
                    arrow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyApplication.selectedMannschaftSpiel = childElem.mannschaftspiel;
                            callMannschaftSpielDetail();
                        }
                    });
                }
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    public void gotoLiga(final Verein.Mannschaft m) {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, LigaTabelleActivity.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException, ValidationException, NoClickTTException {
                Liga liga = new Liga();
                if (!m.url.startsWith("http"))
                    liga.setUrl(UrlUtil.getHttpAndDomain(MyApplication.selectedVerein.getUrl()) + m.url);
                else
                    liga.setUrl(m.url);

                MyApplication.setSelectedLiga(liga);
                new MytClickTTWrapper().readLiga(MyApplication.saison, MyApplication.getSelectedLiga());
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.getSelectedLiga().getMannschaften().size() > 0;
            }


        };
        task.execute();
    }

    private void callMannschaftSpielDetail() {
        if (MyApplication.selectedMannschaftSpiel.getUrlDetail() == null) {
            return;
        }
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, LigaSpielberichtActivity.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException, NoClickTTException {
                new MytClickTTWrapper().readDetail(MyApplication.saison, MyApplication.selectedMannschaftSpiel);
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedMannschaftSpiel.getSpiele().size() > 0;
            }


        };
        task.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.verein_actions, menu);
        return true;
    }

    public void favorite(MenuItem item) {
        new FavoriteManager(this, getApplicationContext()).favorite(MyApplication.selectedVerein);
    }
}
