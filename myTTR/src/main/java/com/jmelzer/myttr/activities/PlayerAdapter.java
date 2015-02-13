package com.jmelzer.myttr.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.Util;

import java.util.List;

/**
 * Adapter for displaying a list of players.
 * User: jmelzer
 * Date: 23.03.14
 * Time: 14:34
 */
public class PlayerAdapter extends ArrayAdapter<Player> {

    public PlayerAdapter(Context context, int resource, List<Player> players) {
        super(context, resource, players);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.playerrow, parent, false);
        Player player = getItem(position);

        TextView txtViewNumber = (TextView) rowView.findViewById(R.id.number);
        txtViewNumber.setText("" + (position + 1));

        TextView textView = (TextView) rowView.findViewById(R.id.firstname);
        TextView textViewFirstName = textView;
        String txt = player.getFirstname();
        String firstName = txt;
        //max 15 characters
        textView.setText(Util.abbreviate(txt, 0, 8));
        textView = (TextView) rowView.findViewById(R.id.lastname);
        txt = player.getLastname();
        String name = firstName + " " + txt;
        if (name.equals(MyApplication.getLoginUser().getRealName())) {
            textView.setTypeface(null, Typeface.BOLD);
            textViewFirstName.setTypeface(null, Typeface.BOLD);
        } else {
            textView.setTypeface(null, Typeface.NORMAL);
            textViewFirstName.setTypeface(null, Typeface.NORMAL);
        }

        if (player.getTtrPoints() > 0) {
            textView.setText(Util.abbreviate(txt, 0, 15));
        } else {
            textView.setText(Util.abbreviate(txt, 0, 30));
        }


        textView = (TextView) rowView.findViewById(R.id.points);
        if (player.getTtrPoints() > 0) {
            txt = "" + player.getTtrPoints();
        } else {
            txt = "";
        }
        textView.setText(txt);

        return rowView;
    }
}
