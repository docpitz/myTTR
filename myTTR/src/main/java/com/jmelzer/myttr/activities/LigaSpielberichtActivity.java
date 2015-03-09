package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.Spielbericht;

import java.util.List;

/**
 * Created by J. Melzer on 26.02.2015.
 * Show the Detail of an game.
 */
public class LigaSpielberichtActivity extends BaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liga_spielbericht);

        final ListView listview = (ListView) findViewById(R.id.liga_spielbericht_row);
        final SpielberichtAdapter adapter = new SpielberichtAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.selectedMannschaftSpiel.getSpiele());
        listview.setAdapter(adapter);
        TextView tv = (TextView) findViewById(R.id.textHeader);
        tv.setText(MyApplication.selectedMannschaftSpiel.getHeimMannschaft().getName() + " - " +
                MyApplication.selectedMannschaftSpiel.getGastMannschaft().getName() +  "    " + MyApplication.selectedMannschaftSpiel.getErgebnis());
    }
    class SpielberichtAdapter extends ArrayAdapter<Spielbericht> {

        public SpielberichtAdapter(Context context, int resource, List<Spielbericht> list) {
            super(context, resource, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.liga_spielbericht_row, parent, false);
            final Spielbericht spielbericht = getItem(position);

            TextView textView = (TextView) rowView.findViewById(R.id.paarung);
            textView.setText(spielbericht.getName());

            textView = (TextView) rowView.findViewById(R.id.heim);
            textView.setText(spielbericht.getSpieler1Name());
            textView = (TextView) rowView.findViewById(R.id.gast);
            textView.setText(spielbericht.getSpieler2Name());
            textView = (TextView) rowView.findViewById(R.id.set_result);
            textView.setText(spielbericht.getResult());
            final ImageView arrow = (ImageView) rowView.findViewById(R.id.arrow);
            arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, spielbericht);
                }
            });
            return rowView;
        }
    }
    public void showPopup(View v, Spielbericht spielbericht) {
        PopupMenu popup = new PopupMenu(this, v);
        Menu menu = popup.getMenu();
        String m = "";
        for (String s : spielbericht.getSets()) {
            m += s + "  ";
        }
        menu.add(Menu.NONE, 1, Menu.NONE, m);
        popup.show();
    }
}
