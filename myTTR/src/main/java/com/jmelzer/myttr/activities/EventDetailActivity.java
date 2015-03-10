package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.EventDetail;
import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.Mannschaft;
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

    private static class ViewHolder {
        TextView textName;
        TextView textResult;
        TextView textSets;
    }

    class EventDetailAdapter extends ArrayAdapter<Game> {
        private LayoutInflater layoutInflater;

        public EventDetailAdapter(Context context, int resource, List<Game> list) {
            super(context, resource, list);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.eventdetailrow_linear, null);
                holder = new ViewHolder();
                holder.textName = (TextView) convertView.findViewById(R.id.name);
                holder.textResult = (TextView) convertView.findViewById(R.id.result);
                holder.textSets = (TextView) convertView.findViewById(R.id.sets);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Game game =  getItem(position);

            holder.textName.setText(game.getPlayerWithPoints());
            holder.textResult.setText(game.getResult());
            holder.textSets.setText(game.getSetsInARow());

            return convertView;
        }
    }

}
