package com.jmelzer.myttr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

import java.util.List;

/**
 * Showing other teams
 * User: jmelzer
 */
public class SelectOtherTeamActivity extends BaseActivity {

    public static final String INTENT_BACK_TO = "INTENT_BACK_TO";
    Class goBackToClass;

    @Override
    protected boolean checkIfNeccessryDataIsAvailable() {
        return MyApplication.otherTeams != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.select_other_team);

        Intent i = getIntent();
        goBackToClass = TTRCalculatorActivity.class;
        if (i != null && i.getExtras() != null) {
            goBackToClass = (Class) i.getExtras().getSerializable(INTENT_BACK_TO);
        }

        final ListView listview = findViewById(R.id.otherteamview);
        final MannschaftAdapter adapter = new MannschaftAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.otherTeams);
        listview.setAdapter(adapter);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        listview.setOnItemClickListener((parent, view, position, id) -> {
            view.setSelected(true);
            MyApplication.selectedOtherTeam = MyApplication.otherTeams.get(position);
            Intent target = new Intent(SelectOtherTeamActivity.this,goBackToClass);
            finish();
            SelectOtherTeamActivity.this.startActivity(target);
        });

    }

    class MannschaftAdapter extends ArrayAdapter<Mannschaft> {

        public MannschaftAdapter(Context context, int resource, List<Mannschaft> mannschaftList) {
            super(context, resource, mannschaftList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.other_team_row, parent, false);
            Mannschaft mannschaft = getItem(position);


            TextView textView = (TextView) rowView.findViewById(R.id.name);
            textView.setText(mannschaft.getName());
            return rowView;
        }
    }
}
