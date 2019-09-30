package com.jmelzer.myttr.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.R;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.jmelzer.myttr.Constants.ACTUAL_SAISON;
import static com.jmelzer.myttr.MyApplication.getSpieleForActualMannschaft;

public class CalendarExportActivity extends BaseActivity {

    public static final String ORGANIZER = "myttrinfo@gmail.com";
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");

    // Projection array. Creating indices for this array instead of doing
// dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT,                  // 3
            CalendarContract.Calendars.CALENDAR_LOCATION              // 4
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_CALENDAR_LOCATION = 4;
    private List<Mannschaftspiel> gamesInFuture;

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.calendar);

        List<Mannschaftspiel> spiele = getSpieleForActualMannschaft();

        Date now = new Date();
        gamesInFuture = new ArrayList<>();

        for (Mannschaftspiel mannschaftspiel : spiele) {
            try {
                Date d = getDate(mannschaftspiel);
                if (now.getTime() < d.getTime()) {
                    mannschaftspiel.setChecked(true);
                    gamesInFuture.add(mannschaftspiel);
                }
            } catch (ParseException e) {
                Log.e(Constants.LOG_TAG, e.getMessage());
            }
        }

        ListView listview = findViewById(R.id.cal_list);
        CalendarRowAdapter adapter = new CalendarRowAdapter(this,
                android.R.layout.simple_list_item_1,
                gamesInFuture);
        listview.setAdapter(adapter);


    }

    private Date getDate(Mannschaftspiel mannschaftspiel) throws ParseException {
        String sd = mannschaftspiel.getDate();
//                "Fr 18.10.19 19:30"
        //cut the string if day of week in front
        if (StringUtils.countMatches(sd, ' ') > 1) {
            sd = sd.substring(sd.indexOf(' '));
        }
        return format.parse(sd);
    }

    private long findEventsByTitleAndLocation(String eventTitle, long starttime, String location, boolean checkAllParamms) {
        final String[] INSTANCE_PROJECTION = new String[]{
                CalendarContract.Instances.EVENT_ID,       // 0
                CalendarContract.Instances.BEGIN,         // 1
                CalendarContract.Instances.TITLE,        // 2
                CalendarContract.Instances.ORGANIZER,    //3
                CalendarContract.Instances.EVENT_LOCATION  // 4
        };

        // Specify the date range you want to search for recurring event instances
        Calendar beginTime = Calendar.getInstance();
        long startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MONTH, 6);
        long endMillis = endTime.getTimeInMillis();


        // The ID of the recurring event whose instances you are searching for in the Instances table
        String selection = CalendarContract.Instances.TITLE + " = ?";
        String[] selectionArgs = new String[]{eventTitle};

        // Construct the query with the desired date range.
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);

        // Submit the query
        Cursor cur = getContentResolver().query(builder.build(), INSTANCE_PROJECTION, selection, selectionArgs, null);

        long id = -1;
        if (cur != null) {
            if (cur.moveToNext()) {
                // Get the field values
                long timeStart = cur.getLong(1);
                Log.d(Constants.LOG_TAG, "Location " + location);
                if (location != null && checkAllParamms) {
                    if (timeStart != starttime) {
                        id = (cur.getLong(0));
                    } else if (!location.replaceAll("\n", " ").equals(cur.getString(PROJECTION_CALENDAR_LOCATION))) {
                        id = (cur.getLong(0));
                    }
                } else {
                    id = (cur.getLong(0));
                }
            }

            cur.close();
        }
        return id;
    }

    private void deleteEvent(long eventID) {
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        int rows = getContentResolver().delete(deleteUri, null, null);
        Log.i("Calendar", "Rows deleted: " + rows);
    }

    ContentValues createEntry(long calID, long startMillis, long endMillis, String title, String location) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        if (location != null) {
            String cleanedLocatiom = location.replace("\n", " ");
            values.put(CalendarContract.Events.EVENT_LOCATION, cleanedLocatiom);
        }
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.DESCRIPTION, saison());
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Berlin");
        values.put(CalendarContract.Events.ORGANIZER, ORGANIZER);
        return values;
    }

    @NonNull
    private String saison() {
        return "Saison " + ACTUAL_SAISON.getName();
    }

    private void checkPermissions(int callbackId, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
        }

        if (!permissions) {
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
        }
    }

    public void addAll(View view) {
        checkPermissions(42, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Keine ausreichenden Berechtigungen zum Eintragen",
                    Toast.LENGTH_LONG).show();
            return;
        }

        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = CalendarContract.Calendars.VISIBLE + " = 1 ";//AND " + CalendarContract.Calendars.IS_PRIMARY + "=1";
        Cursor cur = cr.query(uri, EVENT_PROJECTION, selection, null, null);

        long calID = 0;
        final List<String> names = new ArrayList<>();
        final List<Long> ids = new ArrayList<>();
        if (cur == null) {
            Toast.makeText(CalendarExportActivity.this, "Konnte den Kalender nicht lesen", Toast.LENGTH_LONG).show();
            return;
        }
        while (cur.moveToNext()) {
            calID = cur.getLong(PROJECTION_ID_INDEX);
            names.add(cur.getString(PROJECTION_DISPLAY_NAME_INDEX));
            ids.add(calID);
        }
        cur.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Export. Bitte Kalender auswählen").setItems(names.toArray(new String[0]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                export(ids.get(which));
                Intent target = new Intent(CalendarExportActivity.this, LigaMannschaftResultsActivity.class);
                startActivity(target);
            }

        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    @SuppressLint("MissingPermission")
    private void export(Long calID) {
        ContentResolver cr = getContentResolver();
        Date now = new Date();
        int counter = 0;
        int moved = 0;
        int exists = 0;
        for (Mannschaftspiel mannschaftspiel : gamesInFuture) {
            if (!mannschaftspiel.getChecked()) {
                continue;
            }

            try {
                Date startTime = getDate(mannschaftspiel);
                if (now.getTime() < startTime.getTime()) {
                    Log.d(Constants.LOG_TAG, "Date=" + startTime);
                    Calendar endTime = Calendar.getInstance();
                    endTime.setTime(startTime);
                    endTime.add(Calendar.HOUR, 3); // 3h
                    String title = String.format("%s - %s",
                            mannschaftspiel.getHeimMannschaft().getName(),
                            mannschaftspiel.getGastMannschaft().getName());
                    long id = findEventsByTitleAndLocation(title, startTime.getTime(), mannschaftspiel.getActualSpiellokal(), false);
                    if (id == -1) {
                        ContentValues values = createEntry(calID, startTime.getTime(), endTime.getTimeInMillis(), title, mannschaftspiel.getActualSpiellokal());
                        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                        Log.d(Constants.LOG_TAG, "created=" + values);
                        Log.d(Constants.LOG_TAG, "uri=" + uri);
                        counter++;
                    } else {
                        //Spielverlegung?
                        id = findEventsByTitleAndLocation(title, startTime.getTime(), mannschaftspiel.getActualSpiellokal(), true);
                        if (id > -1) {
                            deleteEvent(id);
                            ContentValues values = createEntry(calID, startTime.getTime(), endTime.getTimeInMillis(), title, mannschaftspiel.getActualSpiellokal());
                            cr.insert(CalendarContract.Events.CONTENT_URI, values);
                            moved++;
                        } else {
                            exists++;
                            Log.d(Constants.LOG_TAG, "event exists " + title);
                        }
                    }
                }
            } catch (ParseException e) {
                Log.e(Constants.LOG_TAG, e.getMessage());
            }

        }

        Toast.makeText(this,
                "Es wurden " + counter + " neue Einträge in den Kalender geschrieben\n" +
                        exists + " Einträge existierten bereits\n" +
                        moved + " Spiele aktualisiert.",
                Toast.LENGTH_LONG).show();
    }

    private static class ViewHolder {
        TextView date;
        TextView title;
        CheckBox checkbox;
    }

    class CalendarRowAdapter extends ArrayAdapter<Mannschaftspiel> {
        private LayoutInflater layoutInflater;

        public CalendarRowAdapter(Context context, int resource, List<Mannschaftspiel> list) {
            super(context, resource, list);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.calendar_row, parent, false);
                holder = new ViewHolder();
                holder.date = convertView.findViewById(R.id.cal_date);
                holder.title = convertView.findViewById(R.id.cal_name);
                holder.checkbox = convertView.findViewById(R.id.checkBoxCal);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Mannschaftspiel item = getItem(position);
            holder.date.setText(item.getDate());
            String title = String.format("%s - %s",
                    item.getHeimMannschaft().getName(),
                    item.getGastMannschaft().getName());
            holder.title.setText(title);

            holder.checkbox.setId(position);
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    changeButton();
                    item.setChecked(isChecked);
                }
            });

            return convertView;
        }
    }

    void changeButton() {
        Button button = findViewById(R.id.btnActionCal);
        button.setText("Ausgwählte Einträge in den Kalendar übernehmen");

    }
}
