package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.TeamAppointment;
import com.jmelzer.myttr.Util;

import java.util.List;

/**
 * TODO
 * User: jmelzer
 * Date: 22.03.14
 * Time: 13:06
 */
public class NextAppointmentsActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nextappointments);

        final ListView listview = (ListView) findViewById(R.id.listview);
        final AppointmentAdapter adapter = new AppointmentAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.teamAppointments);
        listview.setAdapter(adapter);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {


                view.setSelected(true);
                return true;
            }
        });
    }

    class AppointmentAdapter extends ArrayAdapter<TeamAppointment> {

        public AppointmentAdapter(Context context, int resource, List<TeamAppointment> appointments) {
            super(context, resource, appointments);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.appointmentrow, parent, false);
            TeamAppointment teamAppointment = MyApplication.teamAppointments.get(position);

            TextView textView = (TextView) rowView.findViewById(R.id.date);
            String txt = teamAppointment.getDate();
            textView.setText(txt);

            textView = (TextView) rowView.findViewById(R.id.clubname);
            textView.setText(teamAppointment.getTeam());


            return rowView;
        }
    }
}
