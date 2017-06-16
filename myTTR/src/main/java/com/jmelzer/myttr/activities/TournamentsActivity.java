package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.Tournament;

import java.util.List;

/**
 * Created by cicgfp on 15.06.2017.
 */

public class TournamentsActivity extends BaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.tournaments);

        final ListView listview = (ListView) findViewById(R.id.tournament_row);
        final TournamentsActivity.RowAdapter adapter = new TournamentsActivity.RowAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.myTournaments);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                MyApplication.selectedMannschaft = (Mannschaft) parent.getItemAtPosition(position);
//                callMannschaftDetail(LigaMannschaftResultsActivity.class);

            }
        });


    }

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.myTournaments != null;
    }


    private static class ViewHolder {
        TextView textDate;
        TextView textName;
        TextView textRegion;
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
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Tournament tournament = getItem(position);

            holder.textName.setText(tournament.getName());
            holder.textDate.setText(tournament.getDate());
            holder.textRegion.setText(tournament.getRegion());

            return convertView;
        }
    }
}