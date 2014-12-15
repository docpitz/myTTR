package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.TeamAppointment;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;

import java.util.List;

/**
 * TODO
 * User: jmelzer
 * Date: 22.03.14
 * Time: 13:06
 */
public class EventsActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events);

        final ListView listview = (ListView) findViewById(R.id.eventlist);
        final EventAdapter adapter = new EventAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.games);
        listview.setAdapter(adapter);
        setTitle(MyApplication.getTitle());



    }

    class EventAdapter extends ArrayAdapter<Game> {

        public EventAdapter(Context context, int resource, List<Game> appointments) {
            super(context, resource, appointments);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.eventrow_linear, parent, false);
            Game game = MyApplication.games.get(position);

            TextView textView = (TextView) rowView.findViewById(R.id.date);
            String txt = game.getDate();
            textView.setText(txt);

            textView = (TextView) rowView.findViewById(R.id.event);
            textView.setText(game.getEvent());
            textView = (TextView) rowView.findViewById(R.id.ak);
            textView.setText(game.getAk());
            textView = (TextView) rowView.findViewById(R.id.ttr);
            textView.setText(game.getTtrAsString());


            return rowView;
        }
    }
}
