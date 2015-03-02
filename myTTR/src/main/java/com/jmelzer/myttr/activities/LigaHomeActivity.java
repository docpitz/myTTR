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
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by J. Melzer on 20.02.2015.
 * Display the click tt selection Liga
 */
public class LigaHomeActivity extends BaseActivity {

    private CharSequence selectedKategorie;
    private Bezirk selectedBezirk;
    private List<Liga> ligaList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liga_home);

        Spinner spinnerVerband = (Spinner) findViewById(R.id.spinner_verband);
        VerbandAdapter adapterVerband = new VerbandAdapter(this, android.R.layout.simple_spinner_item, Verband.verbaende);
        spinnerVerband.setAdapter(adapterVerband);
        spinnerVerband.setOnItemSelectedListener(new VerbandListener());


        configBezirkAdapter();
        configLigAdapter();

        Spinner spinnerKat = (Spinner) findViewById(R.id.kategorie_spinner);
        KategorieAdapter adapter = new KategorieAdapter(this, android.R.layout.simple_spinner_item, filterKategorien());

        spinnerKat.setAdapter(adapter);
        spinnerKat.setOnItemSelectedListener(new SpinnerKategorieListener());


    }

    private List<String> filterKategorien() {
        List<String> list = new ArrayList<>();

        if (ligaList == null) return list;

        Set<String> set = new TreeSet<>();

        for (Liga liga : ligaList) {
            set.add(liga.getKategorie());
        }
        list.addAll(set);
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

    public List<Bezirk> getBezirkList() {
        List<Bezirk> list = MyApplication.selectedVerband.getBezirkList();
        list.add(new Bezirk("", null));
        return list;
    }


    class LigaListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Liga l = (Liga) parent.getItemAtPosition(position);
            MyApplication.selectedLiga = l;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    class VerbandListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            MyApplication.selectedVerband = (Verband) parent.getItemAtPosition(position);
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
                if (selectedBezirk == null || selectedBezirk.getUrl() == null) return;

                AsyncTask<String, Void, Integer> task = new BaseAsyncTask(LigaHomeActivity.this, null) {
                    @Override
                    protected void callParser() throws NetworkException, LoginExpiredException {
                        new ClickTTParser().readKreiseAndLigen(selectedBezirk);
                    }

                    @Override
                    protected boolean dataLoaded() {
                        return selectedBezirk.getLigen().size() > 0;
                    }

                    @Override
                    protected void startNextActivity() {
                        configLigAdapter();
                    }
                };
                task.execute();
                configLigAdapter();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    void configLigAdapter() {
        Spinner spinnerLiga = (Spinner) findViewById(R.id.spinner_liga);
        if (selectedBezirk != null && selectedBezirk.getUrl() != null) {
            ligaList = filterKategorie(selectedBezirk.getLigen(), selectedKategorie);
        } else {
            ligaList = filterKategorie(MyApplication.selectedVerband.getLigaList(), selectedKategorie);
        }
        final LigaAdapter ligaAdapter = new LigaAdapter(this,
                android.R.layout.simple_spinner_item,
                ligaList);

        spinnerLiga.setAdapter(ligaAdapter);

        spinnerLiga.setOnItemSelectedListener(new LigaListener());
    }

    void configBezirkAdapter() {
        Spinner spinnerBezirke = (Spinner) findViewById(R.id.spinner_bezirk);
        BezirkAdapter bezirkAdapter = new BezirkAdapter(this,
                android.R.layout.simple_spinner_item,
                getBezirkList());
        spinnerBezirke.setAdapter(bezirkAdapter);
        spinnerBezirke.setOnItemSelectedListener(new SpinnerBezirkListener());
    }

    private List<Liga> filterKategorie(List<Liga> topLigen, CharSequence text) {
        List<Liga> filteredList = new ArrayList<>();
        for (Liga liga : topLigen) {
            if (text == null || liga.getKategorie().equals(text)) {
                filteredList.add(liga);
            }
        }
        return filteredList;
    }

    private class LigaAdapter extends ArrayAdapter<Liga> {
        public LigaAdapter(Context context, int resource, List<Liga> list) {
            super(context, resource, list);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    private class VerbandAdapter extends ArrayAdapter<Verband> {
        public VerbandAdapter(Context context, int resource, List<Verband> list) {
            super(context, resource, list);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    private class BezirkAdapter extends ArrayAdapter<Bezirk> {
        public BezirkAdapter(Context context, int resource, List<Bezirk> list) {
            super(context, resource, list);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    private class KategorieAdapter extends ArrayAdapter<String> {
        public KategorieAdapter(Context context, int resource, List<String> list) {
            super(context, resource, list);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(getContext());
            label.setText(getItem(position));
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getView(position, convertView, parent);
        }
    }
}
