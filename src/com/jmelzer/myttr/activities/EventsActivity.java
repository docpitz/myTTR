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
import android.widget.Toast;

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

                    new DetailAsyncTask(event).execute();
                    return true;
                }
                return false;
            }
        });


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
            textView.setText(event.getPlayCount());
            textView = (TextView) rowView.findViewById(R.id.ak);
            textView.setText(event.getAk());
            textView = (TextView) rowView.findViewById(R.id.ttr);
            textView.setText(event.getTtrAsString());
            textView = (TextView) rowView.findViewById(R.id.diff);
            textView.setText("" + event.getSum());


            return rowView;
        }
    }

    class DetailAsyncTask extends AsyncTask<String, Void, Integer> {
        ProgressDialog progressDialog;

        Event event;
        String errorMessage;

        DetailAsyncTask(Event event) {
            this.event = event;
        }

        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(EventsActivity.this);
                progressDialog.setMessage("Lade die Details, bitte warten...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
            try {
                MyApplication.currentDetail = myTischtennisParser.readEventDetail(event);
            } catch (NetworkException e) {
                errorMessage = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (errorMessage != null) {
                Toast.makeText(EventsActivity.this, errorMessage,
                        Toast.LENGTH_SHORT).show();
            }else if (MyApplication.currentDetail == null) {
                Toast.makeText(EventsActivity.this, "Konnte Details nicht laden",
                        Toast.LENGTH_SHORT).show();
            } else {
                Intent target = new Intent(EventsActivity.this, EventDetailActivity.class);
                startActivity(target);
            }
        }
    }
}
