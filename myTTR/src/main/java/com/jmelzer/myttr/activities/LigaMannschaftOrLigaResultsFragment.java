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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.ClickTTParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 27.02.2015.
 * <p/>
 * Fragment for showing results of a liga or a club.
 * Depends what was set (mannschaft / liga)
 */
public class LigaMannschaftOrLigaResultsFragment extends Fragment {
    Mannschaft mannschaft;
    Liga liga;
    SpielAdapter adapter;
    int pos = 0;

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }

    public void setMannschaft(Mannschaft mannschaft) {
        this.mannschaft = mannschaft;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.liga_mannschaft_results, container, false);

        boolean vr = !existRRSpiele();
        final ListView listview = (ListView) rootView.findViewById(R.id.liga_mannschaft_detail_row);
        //create emtpty list
        adapter = new SpielAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                new ArrayList<Mannschaftspiel>());
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
        // the list will be filled
        configList(pos == 0);

        return rootView;
    }

    private boolean existRRSpiele() {
        return getSpiele(false).size() > 0;

    }

    List<Mannschaftspiel> getSpiele(boolean vr) {
        if (mannschaft != null) {
            return MyApplication.getSelectedLiga().getSpieleFor(mannschaft.getName(), vr);
        } else {
            return MyApplication.getSelectedLiga().getSpieleFor(null, vr);
        }
    }

    private void callMannschaftSpielDetail() {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(getActivity(), LigaSpielberichtActivity.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                new ClickTTParser().readDetail(MyApplication.getSelectedLiga(), MyApplication.selectedMannschaftSpiel);
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedMannschaftSpiel.getSpiele().size() > 0;
            }


        };
        task.execute();
    }

    class SpielAdapter extends ArrayAdapter<Mannschaftspiel> {

        public SpielAdapter(Context context, int resource, List<Mannschaftspiel> list) {
            super(context, resource, list);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.liga_mannschaft_results_row, parent, false);
            final Mannschaftspiel spiel = getItem(position);

            TextView textView = (TextView) rowView.findViewById(R.id.date);
            textView.setText(spiel.getDate());

            textView = (TextView) rowView.findViewById(R.id.heim);
            textView.setText(spiel.getHeimMannschaft().getName());
            textView = (TextView) rowView.findViewById(R.id.gast);
            textView.setText(spiel.getGastMannschaft().getName());
            textView = (TextView) rowView.findViewById(R.id.result);
            textView.setText(spiel.getErgebnis());

            final ImageView arrow = (ImageView) rowView.findViewById(R.id.arrow);
            arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.selectedMannschaftSpiel = spiel;
                    callMannschaftSpielDetail();
                }
            });
            return rowView;
        }
    }

    void configList(boolean vr) {
        if (adapter != null) {
            adapter.clear();
            //create a copy otherwise clear will remove all entries
            adapter.addAll(new ArrayList(getSpiele(vr)));
            // fire the event
            adapter.notifyDataSetChanged();
        }
    }

}
