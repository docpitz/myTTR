package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Kreis;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.logic.impl.MytClickTTWrapper;
import com.jmelzer.myttr.model.Favorite;
import com.jmelzer.myttr.model.Saison;
import com.jmelzer.myttr.model.Verein;

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
    private List<Liga> allLigaList;
    MytClickTTWrapper clickTTWrapper = new MytClickTTWrapper();
    FavoriteManager favoriteManager;

    @Override
    protected boolean checkIfNeccessryDataIsAvailable() {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }
        favoriteManager = new FavoriteManager(this, getApplicationContext());
        setContentView(R.layout.liga_home);

        Spinner spinnerSaison = findViewById(R.id.spinner_saison);


        SaisonAdapter saisonAdapter = new SaisonAdapter(this, R.layout.liga_home_spinner_selected_item,
                Saison.saisons);
        saisonAdapter.setDropDownViewResource(R.layout.liga_home_spinner_item);
        spinnerSaison.setAdapter(saisonAdapter);
        spinnerSaison.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                view.setTex
                Saison tmpSaison = Saison.parse((String) parent.getItemAtPosition(position));
                if (tmpSaison == MyApplication.saison) {
                    return;
                }
                MyApplication.saison = tmpSaison;
                selectedBezirk = null;
                ligaList = null;
                selectedKreis = null;
                allLigaList = null;
                MyApplication.selectedVerband.clearLigaList();
                readLigenAndBezirke();
                configBezirkAdapter();
                configKreisAdapter();
                configLigAdapter();
                configKategorienAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        if (Saison.saisons.size() > 1) {
//            findViewById(R.id.layoutSaison).setVisibility(View.VISIBLE);
//        }

        Spinner spinnerVerband = (Spinner) findViewById(R.id.spinner_verband);
        VerbandAdapter adapterVerband = new VerbandAdapter(this, R.layout.liga_home_spinner_selected_item,
                Verband.verbaende);
        adapterVerband.setDropDownViewResource(R.layout.liga_home_spinner_item);
        spinnerVerband.setAdapter(adapterVerband);
        spinnerVerband.setOnItemSelectedListener(new VerbandListener());
        if (MyApplication.selectedVerband != null) {
            spinnerVerband.setSelection(adapterVerband.getPosition(MyApplication.selectedVerband));
        }


        configBezirkAdapter();
        configKreisAdapter();
        configLigAdapter();
        configKategorienAdapter();


    }

    private List<String> filterKategorien() {
        List<String> list = new ArrayList<>();

        if (allLigaList == null) {
            return list;
        }

        Set<String> set = new TreeSet<String>();

        for (Liga liga : allLigaList) {
            if (liga.getKategorie() != null)
                set.add(liga.getKategorie());
            else
                Log.e(Constants.LOG_TAG, "error in kategorie " + liga);
        }
        list.addAll(set);
        list.remove("Herren");
        list.add(0, "Herren");
        return list;

    }


    public void tabelle() {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, LigaTabelleActivity.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException, ValidationException, NoClickTTException {
                clickTTWrapper.readLiga(MyApplication.saison, MyApplication.getSelectedLiga());
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.getSelectedLiga().getMannschaften().size() > 0;
            }


        };
        task.execute();
    }


    public void verein(final Verein verein) {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, LigaVereinActivity.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException, NoClickTTException {
                MyApplication.selectedVerein = clickTTWrapper.readVerein(verein.getUrl(), MyApplication.saison);
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedVerein != null;
            }


        };
        task.execute();
    }

    public List<Bezirk> getBezirkList() {
        List<Bezirk> listC = new ArrayList<>();
        if (MyApplication.selectedVerband != null) {
            List<Bezirk> list = MyApplication.selectedVerband.getBezirkList();
            if (list.size() > 0) {
                listC.addAll(list);
                listC.add(0, new Bezirk("", null));
            }
        }
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
            if (MyApplication.selectedVerband == null) {
                return;
            }

            if (MyApplication.selectedVerband.getLigaList().size() == 0) {
                readLigenAndBezirke();
            } else {
                selectedBezirk = null;
                configBezirkAdapter();
                configLigAdapter();
                configKategorienAdapter();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void readLigenAndBezirke() {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(LigaHomeActivity.this, null) {
            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                clickTTWrapper.readBezirkeAndLigen(MyApplication.selectedVerband, MyApplication.saison);
//                new ClickTTParser().readBezirke(MyApplication.selectedVerband, selectedSaison);
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedVerband.getLigaList().size() > 0;
            }

            @Override
            protected void startActivityAfterParsing() {
                selectedBezirk = null;
                configBezirkAdapter();
                configKreisAdapter();
                configLigAdapter();
                configKategorienAdapter();
            }
        };
        task.execute();
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
                if (selectedBezirk == null || selectedBezirk.getUrl() == null) {
                    return;
                }
                if (selectedBezirk.getLigen().size() == 0) {
                    AsyncTask<String, Void, Integer> task = new BaseAsyncTask(LigaHomeActivity.this, null) {
                        @Override
                        protected void callParser() throws NetworkException, LoginExpiredException {
                            clickTTWrapper.readKreiseAndLigen(MyApplication.saison, selectedBezirk);
                        }

                        @Override
                        protected boolean dataLoaded() {
                            return selectedBezirk.getLigen().size() > 0;
                        }

                        @Override
                        protected void startActivityAfterParsing() {
                            configLigAdapter();
                            configKreisAdapter();
                            configKategorienAdapter();
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
                if (selectedKreis == null || selectedKreis.getUrl() == null) {
                    return;
                }

                if (selectedKreis.getLigen().size() == 0) {
                    AsyncTask<String, Void, Integer> task = new BaseAsyncTask(LigaHomeActivity.this, null) {
                        @Override
                        protected void callParser() throws NetworkException, LoginExpiredException, NoClickTTException {
                            clickTTWrapper.readLigen(selectedKreis, MyApplication.saison);
                        }

                        @Override
                        protected boolean dataLoaded() {
                            return selectedBezirk.getLigen().size() > 0;
                        }

                        @Override
                        protected void startActivityAfterParsing() {
                            configLigAdapter();
                            configKategorienAdapter();
                        }
                    };
                    task.execute();
                } else {
                    configLigAdapter();
                    configKategorienAdapter();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    void configLigAdapter() {
        if (MyApplication.selectedVerband == null) {
            return;
        }
        final ListView listview = (ListView) findViewById(R.id.liga_detail_list);
        if (selectedBezirk != null && selectedBezirk.getUrl() != null) {
            if (selectedKreis != null && selectedKreis.getUrl() != null) {
                allLigaList = selectedKreis.getLigen();
            } else {
                allLigaList = selectedBezirk.getLigen();
            }
        } else {
            allLigaList = MyApplication.selectedVerband.getLigaList();
        }
        ligaList = filterLigenBySelectedKategorie();

        final ResultAdapter adapter = new ResultAdapter(this, R.layout.liga_home_spinner_selected_item, ligaList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                view.setSelected(true);
                MyApplication.setSelectedLiga((Liga) parent.getItemAtPosition(position));
                tabelle();
            }
        });
    }

    void configKategorienAdapter() {
        Spinner spinner = (Spinner) findViewById(R.id.kategorie_spinner);
        ArrayAdapter adapter = new ArrayAdapter<>(this,
                R.layout.liga_home_spinner_selected_item,
                filterKategorien());
        adapter.setDropDownViewResource(R.layout.liga_home_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new KategorieListener());
    }

    void configBezirkAdapter() {
        Spinner spinnerBezirke = (Spinner) findViewById(R.id.spinner_bezirk);
        BezirkAdapter bezirkAdapter = new BezirkAdapter(this,
                R.layout.liga_home_spinner_selected_item,
                getBezirkList());
        bezirkAdapter.setDropDownViewResource(R.layout.liga_home_spinner_item);
        spinnerBezirke.setAdapter(bezirkAdapter);
        spinnerBezirke.setOnItemSelectedListener(new BezirkListener());
    }

    void configKreisAdapter() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_kreise);
        KreisAdapter adapter = new KreisAdapter(this,
                R.layout.liga_home_spinner_selected_item,
                getKreisList());
        adapter.setDropDownViewResource(R.layout.liga_home_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new KreisListener());
    }

    private List<Liga> filterLigenBySelectedKategorie() {
        List<Liga> filteredList = new ArrayList<>();
        for (Liga liga : allLigaList) {
            if (selectedKategorie == null || selectedKategorie.equals(liga.getKategorie())) {
                filteredList.add(liga);
            }
        }
        return filteredList;
    }

    private class ResultAdapter extends ArrayAdapter<Liga> {
        private LayoutInflater layoutInflater;

        public ResultAdapter(Context context, int resource, List<Liga> list) {
            super(context, resource, list);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView label;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.liga_home_result_row, parent, false);
                label = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(label);
            } else {
                label = (TextView) convertView.getTag();
            }
            label.setText(getItem(position).getName());
            return convertView;
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

    private class SaisonAdapter extends ArrayAdapter<String> {
        public SaisonAdapter(Context context, int resource, List<String> list) {
            super(context, resource, list);
            setDropDownViewResource(R.layout.liga_home_spinner_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView label = (TextView) super.getView(position, convertView, parent);
            label.setText(getItem(position));
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            TextView label = (TextView) super.getDropDownView(position, convertView, parent);
            label.setText(getItem(position));
            return label;
        }
    }

    private class BezirkAdapter extends ArrayAdapter<Bezirk> {
        public BezirkAdapter(Context context, int resource, List<Bezirk> list) {
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

    private class KreisAdapter extends ArrayAdapter<Kreis> {
        public KreisAdapter(Context context, int resource, List<Kreis> list) {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.liga_home_actions, menu);
        //see http://stackoverflow.com/questions/7042958/android-adding-a-submenu-to-a-menuitem-where-is-addsubmenu
        SubMenu subm = menu.getItem(0).getSubMenu(); // get my MenuItem with placeholder submenu
        subm.clear(); // delete place holder
        List<Favorite> list = favoriteManager.getFavorites(Verein.class, Liga.class);
        int id = 0;
        for (Favorite favorite : list) {
            subm.add(0, id++, Menu.NONE, favorite.getName());
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        favoriteManager.startFavorite(item.getItemId(), Verein.class, Liga.class);
        return super.onOptionsItemSelected(item);

    }


    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }
}
