package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Kreis;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.db.FavoriteLigaDataBaseAdapter;
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
    private Kreis selectedKreis;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liga_home);

        Spinner spinnerVerband = (Spinner) findViewById(R.id.spinner_verband);
        VerbandAdapter adapterVerband = new VerbandAdapter(this, android.R.layout.simple_spinner_item, Verband.verbaende);
        spinnerVerband.setAdapter(adapterVerband);
        spinnerVerband.setOnItemSelectedListener(new VerbandListener());


        configBezirkAdapter();
        configKreisAdapter();
        configLigAdapter();

        Spinner spinnerKat = (Spinner) findViewById(R.id.kategorie_spinner);
        KategorieAdapter adapter = new KategorieAdapter(this, android.R.layout.simple_spinner_item, filterKategorien());

        spinnerKat.setAdapter(adapter);
        spinnerKat.setOnItemSelectedListener(new KategorieListener());


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


    public void tabelle() {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, LigaTabelleActivity.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                new ClickTTParser().readLiga(MyApplication.getSelectedLiga());
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.getSelectedLiga().getMannschaften().size() > 0;
            }


        };
        task.execute();
    }

    public List<Bezirk> getBezirkList() {
        List<Bezirk> list = MyApplication.selectedVerband.getBezirkList();
        List<Bezirk> listC = new ArrayList<>(list);
        listC.add(0, new Bezirk("", null));
        return listC;
    }

    public List<Kreis> getKreisList() {
        if (selectedBezirk != null) {
            List<Kreis> list = selectedBezirk.getKreise();
            List<Kreis> listC = new ArrayList<>(list);
            listC.add(0, new Kreis("", null));
            return listC;
        }
        return new ArrayList<>();
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
                        selectedBezirk = null;
                        configBezirkAdapter();
                        configKreisAdapter();
                        configLigAdapter();
                    }
                };
                task.execute();
            } else {
                selectedBezirk = null;
                configBezirkAdapter();
                configLigAdapter();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class KategorieListener implements AdapterView.OnItemSelectedListener {
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

    class BezirkListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (view != null) {
                selectedBezirk = (Bezirk) parent.getItemAtPosition(position);
                if (selectedBezirk == null || selectedBezirk.getUrl() == null) return;
                if (selectedBezirk.getLigen().size() == 0) {
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
                            configKreisAdapter();
                        }
                    };
                    task.execute();
                } else {
                    configLigAdapter();
                    configKreisAdapter();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class KreisListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (view != null) {
                selectedKreis = (Kreis) parent.getItemAtPosition(position);
                if (selectedKreis == null || selectedKreis.getUrl() == null) return;

                if (selectedKreis.getLigen().size() == 0) {
                    AsyncTask<String, Void, Integer> task = new BaseAsyncTask(LigaHomeActivity.this, null) {
                        @Override
                        protected void callParser() throws NetworkException, LoginExpiredException {
                            new ClickTTParser().readLigen(selectedKreis);
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
                } else
                    configLigAdapter();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    void configLigAdapter() {
        final ListView listview = (ListView) findViewById(R.id.liga_detail_list);
        if (selectedBezirk != null && selectedBezirk.getUrl() != null) {
            if (selectedKreis != null && selectedKreis.getUrl() != null)
                ligaList = filterKategorie(selectedKreis.getLigen(), selectedKategorie);
            else
                ligaList = filterKategorie(selectedBezirk.getLigen(), selectedKategorie);
        } else {
            ligaList = filterKategorie(MyApplication.selectedVerband.getLigaList(), selectedKategorie);
        }

        final LigaAdapter adapter = new LigaAdapter(this, android.R.layout.simple_list_item_1, ligaList);
        listview.setAdapter(adapter);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                view.setSelected(true);

                MyApplication.setSelectedLiga((Liga) parent.getItemAtPosition(position));
                tabelle();
                return false;
            }
        });
    }

    void configBezirkAdapter() {
        Spinner spinnerBezirke = (Spinner) findViewById(R.id.spinner_bezirk);
        BezirkAdapter bezirkAdapter = new BezirkAdapter(this,
                android.R.layout.simple_spinner_item,
                getBezirkList());
        spinnerBezirke.setAdapter(bezirkAdapter);
        spinnerBezirke.setOnItemSelectedListener(new BezirkListener());
    }

    void configKreisAdapter() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_kreise);
        KreisAdapter adapter = new KreisAdapter(this,
                android.R.layout.simple_spinner_item,
                getKreisList());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new KreisListener());
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

    private class KreisAdapter extends ArrayAdapter<Kreis> {
        public KreisAdapter(Context context, int resource, List<Kreis> list) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.liga_home_actions, menu);
        //see http://stackoverflow.com/questions/7042958/android-adding-a-submenu-to-a-menuitem-where-is-addsubmenu
        SubMenu subm = menu.getItem(0).getSubMenu(); // get my MenuItem with placeholder submenu
        subm.clear(); // delete place holder
        List<Liga> list = getLigaList();
        int id = 0;
        for (Liga liga : list) {
            subm.add(0,id++, Menu.NONE, liga.getName());
        }
        return true;
    }

    List<Liga> getLigaList() {
        FavoriteLigaDataBaseAdapter adapter = new FavoriteLigaDataBaseAdapter(getApplicationContext());
        adapter.open();
        return adapter.getAllEntries();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<Liga> list = getLigaList();
        MyApplication.setSelectedLiga(list.get(item.getItemId()));
        tabelle();
        return super.onOptionsItemSelected(item);
    }
    /**
     * shows the favorite entries
     */
    public void favorite(MenuItem item) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setItems(new String[] {"TTG St. Augustin"}, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //search liga?
//            }
//        });
//        AlertDialog dlg = builder.create();
//        dlg.show();
    }
}
