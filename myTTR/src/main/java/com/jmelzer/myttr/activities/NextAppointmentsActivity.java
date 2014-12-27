package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
import com.jmelzer.myttr.logic.ClubParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;

import java.util.List;

/**
 * TODO
 * User: jmelzer
 * Date: 22.03.14
 * Time: 13:06
 */
public class NextAppointmentsActivity extends BaseActivity {

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

                if (position > -1 && position < MyApplication.teamAppointments.size()) {
                    String teamid = MyApplication.teamAppointments.get(position).getId();

                    new ClubListAsyncTask(NextAppointmentsActivity.this,
                            NextAppointmentPlayersActivity.class, teamid).execute();
                    return true;
                }
                return false;
            }
        });


    }

    class ClubListAsyncTask extends BaseAsyncTask {

        String id;

        ClubListAsyncTask(Activity parent, Class targetClz, String id) {
            super(parent, targetClz);
            this.id = id;
        }

        @Override
        protected void callParser() throws NetworkException, LoginExpiredException {
            MyApplication.foreignTeamPlayers = new MyTischtennisParser().readPlayersFromTeam(id);
        }

        @Override
        protected boolean dataLoaded() {
            return MyApplication.foreignTeamPlayers != null;
        }

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
