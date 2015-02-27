package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.ClickTTParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;

import java.util.List;

/**
 * Created by J. Melzer on 27.02.2015.
 *
 */
public class LigaMannschaftResultsFragment extends Fragment {
    Mannschaft mannschaft;
    SpielAdapter adapter;
    int pos = 0;


    public void setPos(int pos) {
        this.pos = pos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.liga_mannschaft_results, container, false);
//        TextView textView = (TextView)  rootView.findViewById(R.id.selected_mannschaft);
        mannschaft = MyApplication.selectedMannschaft;
//        textView.setText(mannschaft.getName());


        final ListView listview = (ListView)  rootView.findViewById(R.id.liga_mannschaft_detail_row);
        adapter = new SpielAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                MyApplication.selectedLiga.getSpieleFor(mannschaft.getName(), true));
        listview.setAdapter(adapter);


        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                MyApplication.selectedMannschaftSpiel = (Mannschaftspiel) parent.getItemAtPosition(position);
                callMannschaftSpielDetail();

                return false;
            }
        });

//        final Switch aSwitch = (Switch) rootView.findViewById(R.id.switch1);
//        aSwitch.setTextOff("Vorrunde");
//        aSwitch.setTextOn("Rückrunde");
//        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                configList(isChecked);
//            }
//        });

        configList(pos == 0);

        return rootView;
    }

    private void callMannschaftSpielDetail() {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(getActivity(), LigaMannschaftSpielAction.class) {

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

    class SpielAdapter extends ArrayAdapter<Mannschaftspiel> {

        public SpielAdapter(Context context, int resource, List<Mannschaftspiel> list) {
            super(context, resource, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.liga_mannschaft_results_row, parent, false);
            Mannschaftspiel spiel = getItem(position);

            TextView textView = (TextView) rowView.findViewById(R.id.date);
            textView.setText(spiel.getDate());

            textView = (TextView) rowView.findViewById(R.id.heim);
            textView.setText(spiel.getHeimMannschaft().getName());
            textView = (TextView) rowView.findViewById(R.id.gast);
            textView.setText(spiel.getGastMannschaft().getName());
            textView = (TextView) rowView.findViewById(R.id.result);
            textView.setText(spiel.getErgebnis());


            return rowView;
        }
    }

    void configList(boolean vr) {
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(MyApplication.selectedLiga.getSpieleFor(MyApplication.selectedMannschaft.getName(), vr));
            // fire the event
            adapter.notifyDataSetChanged();
        }
    }

}
