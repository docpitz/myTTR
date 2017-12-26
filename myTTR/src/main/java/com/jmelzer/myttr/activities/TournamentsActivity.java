package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.Tournament;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.logic.ClickTTParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by J. Melzer on 15.06.2017.
 */

public class TournamentsActivity extends BaseActivity {
    Calendar date = Calendar.getInstance();
    ClickTTParser parser = new ClickTTParser();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.tournaments);


        final ListView listview = findViewById(R.id.tournament_row);
        final TournamentsActivity.RowAdapter adapter = new TournamentsActivity.RowAdapter(this,
                android.R.layout.simple_list_item_1,
                new ArrayList<Tournament>());
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                MyApplication.selectedTournament = (Tournament) parent.getItemAtPosition(position);
                callDetail();

            }
        });

        final ImageButton btnLeft = findViewById(R.id.left);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date.add(Calendar.MONTH, -1);
                refreshDateView();
                refreshList();

            }
        });
        final ImageButton btnRight = findViewById(R.id.right);
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date.add(Calendar.MONTH, 1);
                refreshDateView();
                refreshList();

            }
        });

        Spinner spinnerVerband = findViewById(R.id.spinner_verband);
        VerbandAdapter adapterVerband = new VerbandAdapter(this, R.layout.liga_home_spinner_selected_item,
                Verband.verbaendeWithTournaments());
        adapterVerband.setDropDownViewResource(R.layout.liga_home_spinner_item);
        spinnerVerband.setAdapter(adapterVerband);
        spinnerVerband.setOnItemSelectedListener(new VerbandListener());

        refreshDateView();
    }

    private void refreshDateView() {
        TextView txtView = findViewById(R.id.month);
        txtView.setText(DateFormatUtils.format(date, "MMMM yyyy", Locale.GERMAN));
    }

    private class VerbandAdapter extends ArrayAdapter<Verband> {
        public VerbandAdapter(Context context, int resource, List<Verband> list) {
            super(context, resource, list);
            setDropDownViewResource(R.layout.liga_home_spinner_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView label = (TextView) super.getView(position, convertView, parent);
            label.setText(getItem(position).getName());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            TextView label = (TextView) super.getDropDownView(position, convertView, parent);
            label.setText(getItem(position).getName());
            return label;
        }
    }

    class VerbandListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            MyApplication.myTournaments = null;
            MyApplication.selectedVerband = (Verband) parent.getItemAtPosition(position);
            if (MyApplication.selectedVerband == null) {
                return;
            }
            if (MyApplication.selectedVerband.gettUrl() == null)
                return;

            refreshList();

        }


        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void refreshList() {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(TournamentsActivity.this, null) {
            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                MyApplication.myTournaments = parser.readTournaments(MyApplication.selectedVerband,
                        date.getTime());
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.myTournaments != null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);

                final ListView listview = findViewById(R.id.tournament_row);
                final RowAdapter adapter = new RowAdapter(TournamentsActivity.this,
                        android.R.layout.simple_list_item_1,
                        MyApplication.myTournaments);
                listview.setAdapter(adapter);

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        MyApplication.selectedTournament = (Tournament) parent.getItemAtPosition(position);
                        callDetail();

                    }
                });
            }
        };
        task.execute();
    }

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return true;
    }


    private static class ViewHolder {
        TextView textDate;
        TextView textName;
        TextView textRegion;
        ImageView arrow;
    }

    class RowAdapter extends ArrayAdapter<Tournament> {
        private LayoutInflater layoutInflater;

        public RowAdapter(Context context, int resource, List<Tournament> tournaments) {
            super(context, resource, tournaments);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.tournament_row, null);
                holder = new TournamentsActivity.ViewHolder();
                holder.textName = convertView.findViewById(R.id.name);
                holder.textDate = convertView.findViewById(R.id.tournamentDate);
                holder.textRegion = convertView.findViewById(R.id.region);
                holder.arrow = convertView.findViewById(R.id.arrow);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Tournament tournament = getItem(position);

            holder.textName.setText(tournament.getName());
            holder.textDate.setText(tournament.getDate());
            holder.textRegion.setText(tournament.getRegion());
            holder.arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.selectedTournament = tournament;
                    callDetail();
                }
            });
            return convertView;
        }
    }

    void callDetail() {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(TournamentsActivity.this,
                TournamentDetailActivity.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                parser.readTournamentDetail(MyApplication.selectedTournament);
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedTournament.getName() != null;
//                return MyApplication.selectedTournament.getOmpetitions().size() > 0;
            }


        };
        task.execute();
    }
}
