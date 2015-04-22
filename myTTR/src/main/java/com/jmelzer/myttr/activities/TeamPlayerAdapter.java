package com.jmelzer.myttr.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.UIUtil;

import java.util.List;

/**
 * Adapter for displaying a list of players from aforeign team.
 * User: jmelzer
 * Date: 23.03.14
 * Time: 14:34
 */
public class TeamPlayerAdapter extends ArrayAdapter<Player> {

    public TeamPlayerAdapter(Context context, int resource, List<Player> players) {
        super(context, resource, players);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.teamplayerrow, parent, false);
        Player player = getItem(position);


        TextView textView = (TextView) rowView.findViewById(R.id.firstname);
        TextView textViewFirstName = textView;
        String txt = player.getFirstname();
        //max 15 characters
        textView.setText(UIUtil.abbreviate(txt, 0, 20));
        textView = (TextView) rowView.findViewById(R.id.lastname);
        txt = player.getLastname();
        textView.setTypeface(null, Typeface.NORMAL);
        textViewFirstName.setTypeface(null, Typeface.NORMAL);
        textView.setText(UIUtil.abbreviate(txt, 0, 15));
        final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkBox);
        checkBox.setId(position);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyApplication.foreignTeamPlayers.get(buttonView.getId()).setChecked(isChecked);
            }
        });
        return rowView;
    }
}
