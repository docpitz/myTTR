package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    DetailHelper detailHelper;

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.currentDetail != null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.event_detail);

        currentDetail = MyApplication.currentDetail;
        final ListView listview = findViewById(R.id.event_detail_list);
        final EventDetailAdapter adapter = new EventDetailAdapter(this,
                android.R.layout.simple_list_item_1,
                currentDetail.getGames());
        listview.setAdapter(adapter);
        detailHelper = new DetailHelper(this, currentDetail.getGames(), listview);

    }

    private static class ViewHolder {
        TextView textName;
        TextView textResult;
        TextView textSets;
        int id;
    }

    class EventDetailAdapter extends ArrayAdapter<Game> {
        private LayoutInflater layoutInflater;

        public EventDetailAdapter(Context context, int resource, List<Game> list) {
            super(context, resource, list);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.eventdetailrow_linear, null);
                holder = new ViewHolder();
                holder.textName = convertView.findViewById(R.id.name);
                holder.textResult = convertView.findViewById(R.id.result);
                holder.textSets = convertView.findViewById(R.id.sets);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
//            EventDetailActivity.this.registerForContextMenu(convertView);
            Game game = getItem(position);
            if (game != null) {
                holder.id = position;
                holder.textName.setText(game.getPlayerWithPoints());
                holder.textResult.setText(game.getResult());
                holder.textSets.setText(game.getSetsInARow());
            }
            return convertView;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        detailHelper.createMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return detailHelper.onSelect(item);
    }

}