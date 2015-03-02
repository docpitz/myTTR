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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.ClickTTParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;

import java.util.List;

/**
 * Created by J. Melzer on 21.02.2015.
 *
 */
public class LigaTabelle extends BaseActivity {
    Liga liga;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liga_tabelle);

        liga = MyApplication.selectedLiga;
        final ListView listview = (ListView) findViewById(R.id.liga_tabelle_rows);
        final LigaAdapter adapter = new LigaAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.selectedLiga.getMannschaften());
        listview.setAdapter(adapter);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                MyApplication.selectedMannschaft = (Mannschaft) parent.getItemAtPosition(position);
                callMannschaftDetail(LigaMannschaftDetailActivity.class);

                return false;
            }
        });

//        TextView textView = (TextView) findViewById(R.id.selected_liga);
//        textView.setText(liga.getName());

        setTitle(getTitle() + " - " + liga.getName());
    }

    void callMannschaftDetail(Class targetClz) {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(LigaTabelle.this, targetClz) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                new ClickTTParser().readVR(MyApplication.selectedLiga);
                new ClickTTParser().readRR(MyApplication.selectedLiga);
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedLiga.getSpieleVorrunde().size() > 0;
            }


        };
        task.execute();
    }

    public void ligaInfo(MenuItem item) {

    }

    public void ligaResults(MenuItem item) {
        callMannschaftDetail(LigaAllResultsActivity.class);
    }

    class LigaAdapter extends ArrayAdapter<Mannschaft> {

        public LigaAdapter(Context context, int resource, List<Mannschaft> mannschaftList) {
            super(context, resource, mannschaftList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.liga_tabelle_row, parent, false);
            final Mannschaft mannschaft = getItem(position);

            TextView textView = (TextView) rowView.findViewById(R.id.name);
            String txt = mannschaft.getName();
            textView.setText(txt);

            textView = (TextView) rowView.findViewById(R.id.liga_pos);
            textView.setText("" + mannschaft.getPosition());
            textView = (TextView) rowView.findViewById(R.id.games);
            textView.setText(""  + mannschaft.getGamesCount() );
            textView = (TextView) rowView.findViewById(R.id.points);
            textView.setText(mannschaft.getPoints());

            final ImageView arrow = (ImageView) rowView.findViewById(R.id.arrow);
            arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.selectedMannschaft = mannschaft;
                    callMannschaftDetail(LigaMannschaftDetailActivity.class);
                }
            });
            return rowView;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.liga_actions, menu);
        return true;
    }
}
