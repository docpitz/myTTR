package com.jmelzer.myttr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Participant;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.Spieler;

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

        final ListView listview = findViewById(R.id.participantList);
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

    class ViewHolder {
        TextView textName;
        TextView textClub;
        TextView textTTR;
        Participant participant;
    }

    class ParticipantAdapter extends ArrayAdapter<Participant> {

        ParticipantAdapter(Context context, int resource, List<Participant> participants) {
            super(context, resource, participants);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.participant_row, null);

                holder = new ViewHolder();
                holder.textName = convertView.findViewById(R.id.name);
                holder.textClub = convertView.findViewById(R.id.club);
                holder.textTTR = convertView.findViewById(R.id.ttr);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Participant participant = getItem(position);
            holder.participant = participant;
            holder.textName.setText(participant.getName());
            holder.textClub.setText(participant.getClub());
            holder.textTTR.setText(participant.getQttr());
            ParticipantActivity.this.registerForContextMenu(convertView);
            //todo context menu für ttr rechner & suche
            return convertView;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Aktionen");
        ViewHolder holder = (ViewHolder) v.getTag();
        MyApplication.selectedParticipant = holder.participant;
        menu.add(1, 1, 1, "Spieler Historie anzeigen");
        menu.add(2, 2, 2, "In TTR-Rechner übernehmen");
    }

    private void startSearch() {
        MyApplication.selectedLigaSpieler = new Spieler(MyApplication.selectedParticipant.getName());
        MyApplication.selectedLigaSpieler.setClubName(MyApplication.selectedParticipant.getClub());
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.INTENT_LIGA_PLAYER, true);
        intent.putExtra(SearchActivity.BACK_TO, ParticipantActivity.class);
        startActivity(intent);
    }

    private void startTTR() {
        MyApplication.selectedLigaSpieler = new Spieler(MyApplication.selectedParticipant.getName());
        MyApplication.selectedLigaSpieler.setClubName(MyApplication.selectedParticipant.getClub());
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.INTENT_LIGA_PLAYER, true);
        intent.putExtra(SearchActivity.BACK_TO, TTRCalculatorActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getGroupId()) {
            case 1:
                startSearch();
                break;
            case 2:
                startTTR();
                break;

        }
        return super.onContextItemSelected(item);
    }
}
