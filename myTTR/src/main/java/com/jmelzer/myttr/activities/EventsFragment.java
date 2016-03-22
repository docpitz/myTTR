package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.Event;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

import java.util.List;

/**
 * User: jmelzer
 * Date: 22.03.14
 * Time: 13:06
 */
public class EventsFragment extends Fragment {
    List<Event> events;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.events, container, false);


        final ListView listview = (ListView) rootView.findViewById(R.id.eventlist);
        final EventAdapter adapter = new EventAdapter(rootView.getContext(),
                android.R.layout.simple_list_item_1,
                MyApplication.getEvents());
        listview.setAdapter(adapter);
        events = MyApplication.getEvents();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                view.setSelected(true);
                if (position > -1 && position < MyApplication.getEvents().size()) {
                    Event event = events.get(position);
                    new DetailAsyncTask(event, getActivity(), EventDetailActivity.class).execute();
                }
            }
        });

        TextView textView = (TextView) rootView.findViewById(R.id.selected_player);
        textView.setText(getActivity().getString(R.string.statistic_text) + MyApplication.getStatistikTextForPlayer());

        return rootView;
    }

    private static class ViewHolder {
        TextView textDate;
        TextView textEvent;
        TextView textSp;
        TextView textAk;
        TextView textTtr;
        TextView textDiff;
    }

    class EventAdapter extends ArrayAdapter<Event> {
        private LayoutInflater layoutInflater;

        public EventAdapter(Context context, int resource, List<Event> appointments) {
            super(context, resource, appointments);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.eventrow_linear, null);
                holder = new ViewHolder();
                holder.textDate = (TextView) convertView.findViewById(R.id.date);
                holder.textEvent = (TextView) convertView.findViewById(R.id.event);
                holder.textSp = (TextView) convertView.findViewById(R.id.sp);
                holder.textAk = (TextView) convertView.findViewById(R.id.ak);
                holder.textTtr = (TextView) convertView.findViewById(R.id.ttr);
                holder.textDiff = (TextView) convertView.findViewById(R.id.diff);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Event event = getItem(position);

            holder.textDate.setText(event.getDate());
            holder.textEvent.setText(event.getEvent());
            holder.textSp.setText(event.getBilanz());
            //todo
            holder.textAk.setText("");
            holder.textTtr.setText(event.getTtrAsString());
            holder.textDiff.setText("" + event.getSum());

            return convertView;
        }
    }

}
