package com.jmelzer.myttr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Participant;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;

import java.util.List;

/**
 * Showing result of a player search
 * User: jmelzer
 */
public class ParticipantActivity extends BaseActivity {

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.selectedCompetition != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.participant_list);

        final ListView listview =  findViewById(R.id.participantList);
        final ParticipantAdapter adapter = new ParticipantAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.selectedCompetition.getParticipantList());
        listview.setAdapter(adapter);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                view.setSelected(true);
            }
        });

    }

    class ParticipantAdapter extends ArrayAdapter<Participant> {

        ParticipantAdapter(Context context, int resource, List<Participant> participants) {
            super(context, resource, participants);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.participant_row, parent, false);
            Participant participant = getItem(position);

            TextView textView = rowView.findViewById(R.id.name);
            textView.setText(participant.getName());
            textView = rowView.findViewById(R.id.club);
            textView.setText(participant.getClub());
            textView = rowView.findViewById(R.id.ttr);
            textView.setText(participant.getQttr());

            //todo context menu für ttr rechner & suche
            return rowView;
        }
    }
}
