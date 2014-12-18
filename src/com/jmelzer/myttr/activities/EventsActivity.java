package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Event;
import com.jmelzer.myttr.EventDetail;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;

import java.util.List;

/**
 * User: jmelzer
 * Date: 22.03.14
 * Time: 13:06
 */
public class EventsActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events);

        final ListView listview = (ListView) findViewById(R.id.eventlist);
        final EventAdapter adapter = new EventAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.events);
        listview.setAdapter(adapter);
        setTitle(MyApplication.getTitle());

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                view.setSelected(true);
                if (position > -1 && position < MyApplication.events.size()) {
                    Event event = MyApplication.events.get(position);
                    new DetailAsyncTask(event, EventsActivity.this, EventDetailActivity.class).execute();
                    return true;
                }
                return false;
            }
        });

        TextView textView = (TextView) findViewById(R.id.selected_player);
        if (MyApplication.selectedPlayer != null) {
            //todo spieler namen verbessern (Melzer, Jürgen (1611) etc.
            textView.setText("Statistiken für den Spieler " + MyApplication.selectedPlayer);
        }
    }

    class EventAdapter extends ArrayAdapter<Event> {

        public EventAdapter(Context context, int resource, List<Event> appointments) {
            super(context, resource, appointments);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.eventrow_linear, parent, false);
            Event event = MyApplication.events.get(position);

            TextView textView = (TextView) rowView.findViewById(R.id.date);
            String txt = event.getDate();
            textView.setText(txt);

            textView = (TextView) rowView.findViewById(R.id.event);
            textView.setText(event.getEvent());
            textView = (TextView) rowView.findViewById(R.id.sp);
            textView.setText(event.getWon() + "/" + event.getPlayCount());
            textView = (TextView) rowView.findViewById(R.id.ak);
            textView.setText(event.getAk());
            textView = (TextView) rowView.findViewById(R.id.ttr);
            textView.setText(event.getTtrAsString());
            textView = (TextView) rowView.findViewById(R.id.diff);
            textView.setText("" + event.getSum());


            return rowView;
        }
    }

}
