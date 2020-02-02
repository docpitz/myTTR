package com.jmelzer.myttr.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.impl.MytClickTTWrapper;

import java.util.List;

/**
 * Created by J. Melzer on 21.02.2015.
 */
public class LigaTabelleActivity extends BaseActivity {
    Liga liga;

    @Override
    protected boolean checkIfNeccessryDataIsAvailable() {
        return MyApplication.getSelectedLiga() != null;
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.liga_tabelle);

        liga = MyApplication.getSelectedLiga();
        final ListView listview = (ListView) findViewById(R.id.liga_tabelle_rows);
        final LigaAdapter adapter = new LigaAdapter(this,
                android.R.layout.simple_list_item_1,
                liga.getMannschaften());
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                MyApplication.selectedMannschaft = (Mannschaft) parent.getItemAtPosition(position);
                callMannschaftDetail(LigaMannschaftResultsActivity.class);

            }
        });

        setTitle(getTitle() + " - " + liga.getName());
    }

    void callMannschaftDetail(Class targetClz) {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(LigaTabelleActivity.this, targetClz) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException, NoClickTTException {
                MytClickTTWrapper p = new MytClickTTWrapper();
                if (MyApplication.selectedMannschaft != null) {
                    //todo check wether this is correct here
                    p.readMannschaftsInfo(MyApplication.saison, MyApplication.selectedMannschaft);
                }
                //rodo opimize  load on demand
                p.readVR(MyApplication.saison, liga);
                p.readRR(MyApplication.saison, liga);
                p.readGesamtSpielplan(MyApplication.saison, liga);
            }

            @Override
            protected boolean dataLoaded() {
                if (liga != null && liga.getSpieleVorrunde().size() > 0)
                    return true;
                if (liga != null && liga.getSpieleGesamt().size() > 0)
                    return true;

                if (MyApplication.selectedMannschaft != null) {
                    if (MyApplication.selectedMannschaft.getKontakt() != null)
                        return true;

                    if (MyApplication.selectedMannschaft.getSpielLokale() != null && MyApplication.selectedMannschaft.getSpielLokale().size() > 0)
                        return true;
                }

                return false;
            }


        };
        task.execute();
    }

    public void ligaResults(MenuItem item) {
        callMannschaftDetail(LigaAllResultsActivity.class);
    }

    public void favorite(MenuItem item) {
        new FavoriteManager(this, getApplicationContext()).favorite(liga);
    }

    private static class ViewHolder {
        ImageView arrow;
        TextView textName;
        TextView textPos;
        TextView textGames;
        TextView textPoints;
    }

    class LigaAdapter extends ArrayAdapter<Mannschaft> {
        private LayoutInflater layoutInflater;

        public LigaAdapter(Context context, int resource, List<Mannschaft> mannschaftList) {
            super(context, resource, mannschaftList);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.liga_tabelle_row, null);
                holder = new ViewHolder();
                holder.textName = convertView.findViewById(R.id.name);
                holder.textPos = convertView.findViewById(R.id.liga_pos);
                holder.textGames = convertView.findViewById(R.id.games);
                holder.textPoints = convertView.findViewById(R.id.points);
                holder.arrow = convertView.findViewById(R.id.arrow);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Mannschaft mannschaft = getItem(position);
            if (mannschaft != null) {
                switch (mannschaft.getLigaPosTyp()) {
                    case AUFSTEIGER:
                        holder.textPos.setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(),
                                R.color.color_aufsteiger));
                        break;
                    case AUF_RELEGATION:
                        holder.textPos.setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(),
                                R.color.color_aufsteiger_relagation));
                        break;
                    case ABSTEIGER:
                        holder.textPos.setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(),
                                R.color.color_absteiger));
                        break;
                    case AB_RELEGATION:
                        holder.textPos.setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(),
                                R.color.color_absteiger_relegation));
                        break;
                    default:
                        holder.textPos.setBackgroundColor(Color.BLACK);

                }
                holder.textName.setText(mannschaft.getName());
                holder.textPos.setText(String.valueOf(mannschaft.getPosition()));
                holder.textGames.setText(String.valueOf(mannschaft.getGamesCount()));
                holder.textPoints.setText(mannschaft.getPoints());

                holder.arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyApplication.selectedMannschaft = mannschaft;
                        callMannschaftDetail(LigaMannschaftResultsActivity.class);
                    }
                });
            }
            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.liga_actions, menu);
        return true;
    }
//    @Override
//    public void onBackPressed() {
//        if (favoritesWereModified) {
//            Intent target = new Intent(this, LigaHomeActivity.class);
//            startActivity(target);
//        } else {
//
//        }
//    }
}
