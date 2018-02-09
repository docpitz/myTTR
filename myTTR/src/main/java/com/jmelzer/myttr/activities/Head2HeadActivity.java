package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.model.Head2HeadResult;
import com.jmelzer.myttr.tasks.Head2HeadAsyncTask;

import java.util.List;

/**
 * User: jmelzer
 */
public class Head2HeadActivity extends BaseActivity {

    private List<Head2HeadResult> head2HeadResults;

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.getHead2Head() != null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.head2head);

        head2HeadResults = MyApplication.getHead2Head();
        final ListView listview = findViewById(R.id.head2head_detail_list);
        TextView nameView = findViewById(R.id.playerName);
        if (head2HeadResults.size() > 0) {
            nameView.setText(head2HeadResults.get(0).getOpponentName());
        }
        final Head2HeadAdapter adapter = new Head2HeadAdapter(this,
                android.R.layout.simple_list_item_1,
                head2HeadResults);
        listview.setAdapter(adapter);

    }

    private static class ViewHolder {
        TextView textDate;
        TextView textType;
        TextView textResult;
        TextView textSets;
        int id;
    }

    class Head2HeadAdapter extends ArrayAdapter<Head2HeadResult> {
        private LayoutInflater layoutInflater;

        public Head2HeadAdapter(Context context, int resource, List<Head2HeadResult> list) {
            super(context, resource, list);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.head2head_row, null);
                holder = new ViewHolder();
                holder.textDate = convertView.findViewById(R.id.date);
                holder.textType = convertView.findViewById(R.id.type);
                holder.textResult = convertView.findViewById(R.id.result);
                holder.textSets = convertView.findViewById(R.id.sets);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Head2HeadActivity.this.registerForContextMenu(convertView);
            Head2HeadResult result = getItem(position);
            if (result != null) {
                holder.id = position;
                holder.textDate.setText(result.getDate());
                holder.textType.setText(result.getType());
                holder.textResult.setText(result.getGame().getResult());
                holder.textSets.setText(result.getGame().getSetsInARow());
            }
            return convertView;
        }
    }


}