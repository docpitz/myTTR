package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.TeamAppointment;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;

import java.util.List;

/**
 * Class showing the next appointments of the player.
 * User: jmelzer
 * Date: 22.03.14
 * Time: 13:06
 */
public class NextAppointmentsActivity extends BaseActivity {

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.teamAppointments != null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.nextappointments);

        final ListView listview = (ListView) findViewById(R.id.listview);
        final AppointmentAdapter adapter = new AppointmentAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.teamAppointments);
        listview.setAdapter(adapter);

//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//
//
//                view.setSelected(true);
//
//                if (position > -1 && position < MyApplication.teamAppointments.size()) {
//                    String teamid = MyApplication.teamAppointments.get(position).getId1();
//
//                    new ClubListAsyncTask(NextAppointmentsActivity.this,
//                            NextAppointmentPlayersActivity.class, teamid).execute();
//                }
//            }
//        });


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

    private static class ViewHolder {
        TextView textDate;
        TextView textC1;
        TextView textC2;
    }

    class AppointmentAdapter extends ArrayAdapter<TeamAppointment> {
        private LayoutInflater layoutInflater;

        public AppointmentAdapter(Context context, int resource, List<TeamAppointment> appointments) {
            super(context, resource, appointments);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
//            View rowView = inflater.inflate(R.layout.appointmentrow, parent, false);
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.appointmentrow, parent, false);
                holder = new ViewHolder();
                holder.textDate = (TextView) convertView.findViewById(R.id.date);
                holder.textC1 = (TextView) convertView.findViewById(R.id.clubname);
                holder.textC2 = (TextView) convertView.findViewById(R.id.clubname2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final TeamAppointment teamAppointment = MyApplication.teamAppointments.get(position);

            String txt = teamAppointment.getDate();
            holder.textDate.setText(txt);

            holder.textC1.setText(teamAppointment.getTeam1());
            holder.textC1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(NextAppointmentsActivity.this, teamAppointment.getTeam1(), Toast.LENGTH_SHORT).show();
                    selectTeam(v, teamAppointment.getId1());
                }
            });
            holder.textC2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(NextAppointmentsActivity.this, teamAppointment.getTeam2(), Toast.LENGTH_SHORT).show();
                    selectTeam(v, teamAppointment.getId2());
                }
            });
            if (!teamAppointment.isFoundTeam()) {
                holder.textC2.setText(teamAppointment.getTeam2());
            } else {
                holder.textC2.setText("");
            }


            return convertView;
        }
    }

    void selectTeam(View view, String teamid) {
        view.setSelected(true);

        new ClubListAsyncTask(NextAppointmentsActivity.this,
                NextAppointmentPlayersActivity.class, teamid).execute();
    }
}
