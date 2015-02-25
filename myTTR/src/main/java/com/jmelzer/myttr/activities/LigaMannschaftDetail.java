package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

import java.util.List;

/**
 * Created by J. Melzer on 21.02.2015.
 */
public class LigaMannschaftDetail extends BaseActivity {
    Liga liga;
    SpielAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liga_mannschaft_detail);

        liga = MyApplication.selectedLiga;
        configList(true);

        TextView textView = (TextView) findViewById(R.id.selected_liga);
        textView.setText(liga.getName());


        final ListView listview = (ListView) findViewById(R.id.liga_mannschaft_detail_row);
        adapter = new SpielAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.selectedLiga.getSpieleFor(MyApplication.selectedMannschaft.getName(), true));
        listview.setAdapter(adapter);


        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                //todo
                return false;
            }
        });

        final Switch aSwitch = (Switch) findViewById(R.id.switch1);
        aSwitch.setTextOff("Vorrunde");
        aSwitch.setTextOn("Rückrunde");
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configList(isChecked);
            }
        });
    }

    void configList(boolean vr) {
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(MyApplication.selectedLiga.getSpieleFor(MyApplication.selectedMannschaft.getName(), !vr));
            // fire the event
            adapter.notifyDataSetChanged();
        }
    }

    class SpielAdapter extends ArrayAdapter<Mannschaftspiel> {

        public SpielAdapter(Context context, int resource, List<Mannschaftspiel> list) {
            super(context, resource, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.liga_mannschaft_detail_row, parent, false);
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

}
