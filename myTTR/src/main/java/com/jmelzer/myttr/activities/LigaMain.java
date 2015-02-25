package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.ClickTTParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 20.02.2015.
 * Display the click tt selection Liga
 */
public class LigaMain extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liga_search);
        Spinner spinnerSex = (Spinner) findViewById(R.id.sex_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sex_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(adapter);

        spinnerSex.setOnItemSelectedListener(new SpinnerSexListener());


    }

    public void tabelle(View view) {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, LigaTabelle.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                new ClickTTParser().readLiga(MyApplication.selectedLiga);
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedLiga.getMannschaften().size() > 0;
            }


        };
        task.execute();
    }


    class LigaListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            MyApplication.selectedLiga = (Liga) parent.getItemAtPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class SpinnerSexListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Spinner spinnerLiga = (Spinner) findViewById(R.id.spinner_liga);
            final LigaAdapter ligaAdapter = new LigaAdapter(LigaMain.this,
                    android.R.layout.simple_spinner_item,
                    filterSex(MyApplication.topLigen, ((TextView) view).getText()));
            ligaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLiga.setAdapter(ligaAdapter);

            spinnerLiga.setOnItemSelectedListener(new LigaListener());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private List<Liga> filterSex(List<Liga> topLigen, CharSequence text) {
        List<Liga> filteredList = new ArrayList<>();
        for (Liga liga : topLigen) {
            if (liga.getSex().equals(text)) {
                filteredList.add(liga);
            }
        }
        return filteredList;
    }

    private class LigaAdapter<T> extends ArrayAdapter<Liga> {
        public LigaAdapter(Context context, int resource, List<Liga> list) {
            super(context, resource, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(getContext());
            label.setText(getItem(position).getName());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getView(position, convertView, parent);
        }
    }
}
