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

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.logic.ClickTTParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 20.02.2015.
 * Display the click tt selection Liga
 */
public class LigaHomeActivity extends BaseActivity {

    private CharSequence selectedKategorie;
    private Bezirk selectedBezirk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liga_home);

        Spinner spinnerVerband = (Spinner) findViewById(R.id.verband_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getVerbaendeAsStrings()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVerband.setAdapter(adapter);
        spinnerVerband.setOnItemSelectedListener(new VerbandListener());
//        spinnerVerband.setOnItemSelectedListener(new SpinnerKategorieListener());


        Spinner spinnerBezirke = (Spinner) findViewById(R.id.spinner_bezirk);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getBezirkeAsString()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBezirke.setAdapter(adapter);
        spinnerBezirke.setOnItemSelectedListener(new SpinnerBezirkListener());

        Spinner spinnerKat = (Spinner) findViewById(R.id.kategorie_spinner);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Liga.alleKategorien
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerKat.setAdapter(adapter);
        spinnerKat.setOnItemSelectedListener(new SpinnerKategorieListener());


    }

    private List<String> getBezirkeAsString() {
        List<String> list = new ArrayList<>();
        for (Bezirk bezirk : MyApplication.selectedVerband.getBezirkList()) {
            list.add(bezirk.getName());
        }
        return list;
    }

    List<String> getVerbaendeAsStrings() {
        List<String> list = new ArrayList<>();
        for (Verband verband : Verband.verbaende) {
            list.add(verband.getName());
        }
        return list;
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
            String s = (String) parent.getItemAtPosition(position);
            Liga l = null;
            for (Liga liga : MyApplication.selectedVerband.getLigaList()) {
                if (s.equals(liga.getName())) {
                    l = liga;
                    break;
                }
            }
            MyApplication.selectedLiga = l;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    class VerbandListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String vAsS = (String) parent.getItemAtPosition(position);
            for (Verband verband : Verband.verbaende) {
                if (verband.getName().equals(vAsS)) {
                    MyApplication.selectedVerband = verband;
                    break;
                }
            }
            if (MyApplication.selectedVerband == null)
                return;

            if (MyApplication.selectedVerband.getLigaList().size() == 0) {
                AsyncTask<String, Void, Integer> task = new BaseAsyncTask(LigaHomeActivity.this, null) {
                    @Override
                    protected void callParser() throws NetworkException, LoginExpiredException {
                        new ClickTTParser().readLigen(MyApplication.selectedVerband);
                        new ClickTTParser().readBezirke(MyApplication.selectedVerband);
                    }

                    @Override
                    protected boolean dataLoaded() {
                        return MyApplication.selectedVerband.getLigaList().size() > 0;
                    }

                    @Override
                    protected void startNextActivity() {
                        configBezirkAdapter();
                        configLigAdapter();
                    }
                };
                task.execute();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class SpinnerKategorieListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (view != null) {
                selectedKategorie = ((TextView) view).getText();
                configLigAdapter();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class SpinnerBezirkListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (view != null) {
                String b = ((TextView) view).getText().toString();
                selectedBezirk = null;
                for (Bezirk bezirk : MyApplication.selectedVerband.getBezirkList()) {
                    if (bezirk.getName().equals(b)) {
                        selectedBezirk = bezirk;
                        break;
                    }
                }
                configLigAdapter();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    void configLigAdapter() {
        Spinner spinnerLiga = (Spinner) findViewById(R.id.spinner_liga);
        List<String> list;
        if (selectedBezirk != null) {
            list = filterKategorie(selectedBezirk.getLigen(), selectedKategorie);
        } else {
            list = filterKategorie(MyApplication.selectedVerband.getLigaList(), selectedKategorie);
        }
        final ArrayAdapter<String> ligaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                list);
        ligaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLiga.setAdapter(ligaAdapter);

        spinnerLiga.setOnItemSelectedListener(new LigaListener());
    }

    void configBezirkAdapter() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_bezirk);
        final ArrayAdapter<String> ligaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getBezirkeAsString());
        ligaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(ligaAdapter);

        spinner.setOnItemSelectedListener(new SpinnerBezirkListener());
    }

    private List<String> filterKategorie(List<Liga> topLigen, CharSequence text) {
        List<String> filteredList = new ArrayList<>();
        for (Liga liga : topLigen) {
            if (liga.getKategorie().equals(text)) {
                filteredList.add(liga.getName());
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
