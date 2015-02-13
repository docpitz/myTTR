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

import com.jmelzer.myttr.EventDetail;
import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

import java.util.List;

/**
 * User: jmelzer
 * Date: 22.03.14
 * Time: 13:06
 */
public class EventDetailActivity extends BaseActivity {
    EventDetail currentDetail;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);

        currentDetail = MyApplication.currentDetail;
        final ListView listview = (ListView) findViewById(R.id.event_detail_list);
        final EventDetailAdapter adapter = new EventDetailAdapter(this,
                android.R.layout.simple_list_item_1,
                currentDetail.getGames());
        listview.setAdapter(adapter);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                view.setSelected(true);
                if (position > -1 && position < MyApplication.events.size()) {
                    Game game = currentDetail.getGames().get(position);

                    new EventsAsyncTask(EventDetailActivity.this, EventsActivity.class, game).execute();
                    return true;
                }
                return false;
            }
        });


    }

    class EventDetailAdapter extends ArrayAdapter<Game> {

        public EventDetailAdapter(Context context, int resource, List<Game> list) {
            super(context, resource, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.eventdetailrow_linear, parent, false);
            Game game = MyApplication.currentDetail.getGames().get(position);

            TextView textView = (TextView) rowView.findViewById(R.id.name);
            String txt = game.getPlayerWithPoints();
            textView.setText(txt);

            textView = (TextView) rowView.findViewById(R.id.result);
            textView.setText(game.getResult());
            textView = (TextView) rowView.findViewById(R.id.sets);
            textView.setText(game.getSetsInARow());


            return rowView;
        }
    }

}
