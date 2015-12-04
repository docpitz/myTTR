package com.jmelzer.myttr.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;

import java.util.List;

/**
 * Adapter for displaying a list of players for the ttr calculation.
 * User: jmelzer
 * Date: 23.03.14
 * Time: 14:34
 */
public class EntryPlayerAdapter extends ArrayAdapter<Player> {

    public EntryPlayerAdapter(Context context, int resource, List<Player> players) {
        super(context, resource, players);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.entryrow, parent, false);
        final Player player = getItem(position);


        TextView textView = (TextView) rowView.findViewById(R.id.nameandclub);
        textView.setText(player.nameAndClub());
        textView = (TextView) rowView.findViewById(R.id.ttr);
        textView.setText("" + player.getTtrPoints());

        final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkBox);
        checkBox.setChecked(player.isChecked());
        checkBox.setId(position);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyApplication.getTtrCalcPlayer().get(buttonView.getId()).setChecked(isChecked);
            }
        });
        final ImageButton deleteBtn = (ImageButton) rowView.findViewById(R.id.imageButton);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), R.string.player_removed_from_list, Toast.LENGTH_SHORT).show();
                MyApplication.removePlayer(player);
                notifyDataSetChanged();
            }
        });

        return rowView;
    }
}
