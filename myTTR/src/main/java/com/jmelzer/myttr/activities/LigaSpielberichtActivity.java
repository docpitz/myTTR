package com.jmelzer.myttr.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.Spielbericht;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.impl.MytClickTTWrapper;
import com.jmelzer.myttr.model.MyTTPlayerIds;

import java.util.List;

/**
 * Created by J. Melzer on 26.02.2015.
 * Show the Detail of an game.
 */
public class LigaSpielberichtActivity extends BaseActivity {

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.selectedMannschaftSpiel != null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.liga_spielbericht);

        final ListView listview = (ListView) findViewById(R.id.liga_spielbericht_row);
        final SpielberichtAdapter adapter = new SpielberichtAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.selectedMannschaftSpiel.getSpiele());
        listview.setAdapter(adapter);
        TextView tv = (TextView) findViewById(R.id.textHeader);
        tv.setText(MyApplication.selectedMannschaftSpiel.getHeimMannschaft().getName() + " - " +
                MyApplication.selectedMannschaftSpiel.getGastMannschaft().getName() + "    " + MyApplication.selectedMannschaftSpiel.getErgebnis());
    }

    private static class ViewHolder {
        TextView textPaarung;
        TextView textHeim;
        TextView textGast;
        TextView textSets;
        ImageView arrow;
    }

    class SpielberichtAdapter extends ArrayAdapter<Spielbericht> {
        private LayoutInflater layoutInflater;

        public SpielberichtAdapter(Context context, int resource, List<Spielbericht> list) {
            super(context, resource, list);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.liga_spielbericht_row, null);
                holder = new ViewHolder();
                holder.textPaarung = (TextView) convertView.findViewById(R.id.paarung);
                holder.textHeim = (TextView) convertView.findViewById(R.id.heim);
                holder.textGast = (TextView) convertView.findViewById(R.id.gast);
                holder.textSets = (TextView) convertView.findViewById(R.id.set_result);
                holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final Spielbericht spielbericht = getItem(position);

            holder.textPaarung.setText(spielbericht.getName());
            holder.textHeim.setText(spielbericht.getSpieler1Name());
            if (!spielbericht.getName().startsWith("D")) {
                holder.textHeim.setTypeface(null, Typeface.ITALIC);
                holder.textHeim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startSpielerDetail(spielbericht.getSpieler1Name(),
                                spielbericht.getSpieler1Url(),
                                spielbericht.getMyTTPlayerIdsForPlayer1());
                    }
                });
            }
            holder.textGast.setText(spielbericht.getSpieler2Name());
            if (!spielbericht.getName().startsWith("D")) {
                holder.textGast.setTypeface(null, Typeface.ITALIC);
                holder.textGast.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startSpielerDetail(spielbericht.getSpieler2Name(),
                                spielbericht.getSpieler2Url(),
                                spielbericht.getMyTTPlayerIdsForPlayer2());
                    }
                });
            }
            holder.textSets.setText(spielbericht.getResult());

            holder.arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, spielbericht);
                }
            });
            return convertView;
        }
    }

    private void startSpielerDetail(final String name, final String url, final MyTTPlayerIds myTTPlayerIdsForPlayer) {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, LigaSpielerResultsActivity.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                MyApplication.selectedLigaSpieler = new MytClickTTWrapper().readSpielerDetail(MyApplication.saison,
                        name, url, myTTPlayerIdsForPlayer);
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedLigaSpieler != null;
            }


        };
        task.execute();
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
