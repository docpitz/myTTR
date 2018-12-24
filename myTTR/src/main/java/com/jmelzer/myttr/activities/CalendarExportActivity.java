package com.jmelzer.myttr.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.jmelzer.myttr.model.Saison;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.jmelzer.myttr.Constants.ACTUAL_SAISON;
import static com.jmelzer.myttr.MyApplication.selectedMannschaft;

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
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

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

        Date now = new Date();
        List<Mannschaftspiel> list = new ArrayList<>();

        for (Mannschaftspiel mannschaftspiel : selectedMannschaft.getSpiele()) {
            try {
                Date d = format.parse(mannschaftspiel.getDate());
                if (now.getTime() < d.getTime()) {
                    mannschaftspiel.setChecked(true);
                    list.add(mannschaftspiel);
                }
            } catch (ParseException e) {
                Log.e(Constants.LOG_TAG, e.getMessage());
            }
        }

        ListView listview = findViewById(R.id.cal_list);
        CalendarRowAdapter adapter = new CalendarRowAdapter(this,
                android.R.layout.simple_list_item_1,
                list);
        listview.setAdapter(adapter);


    }

    private boolean isEventAlreadyExist(String eventTitle, long startMillis, long endMillis) {
        final String[] INSTANCE_PROJECTION = new String[]{
                CalendarContract.Instances.EVENT_ID,      // 0
                CalendarContract.Instances.BEGIN,         // 1
                CalendarContract.Instances.TITLE          // 2
        };


        // The ID of the recurring event whose instances you are searching for in the Instances table
        String selection = CalendarContract.Instances.TITLE + " = ? AND " +
                CalendarContract.Instances.ORGANIZER + " = ?" ;
        String[] selectionArgs = new String[]{eventTitle, ORGANIZER};

        // Construct the query with the desired date range.
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);

        // Submit the query
        Cursor cur = getContentResolver().query(builder.build(), INSTANCE_PROJECTION, selection, selectionArgs, null);
        if (cur != null) {
            boolean b = cur.getCount() > 0;
            cur.close();
            return b;
        }
        return false;
    }

    ContentValues createEntry(long calID, long startMillis, long endMillis, String title, String location) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.EVENT_LOCATION, location.replace("\n", " "));
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

        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
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
        while (cur != null && cur.moveToNext()) {
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            names.add(displayName);
            ids.add(calID);

            // Do something with the values...
            String calendarInfo = String.format("Calendar ID: %s\nDisplay Name: %s\nAccount Name: %s\nOwner Name: %s", calID, displayName, accountName, ownerName);

            Log.d(Constants.LOG_TAG, calendarInfo);

        }
        cur.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Export. Bitte Kalender auswählen").setItems(names.toArray(new String[0]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                export(ids.get(which));
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
        int exists = 0;
        for (Mannschaftspiel mannschaftspiel : selectedMannschaft.getSpiele()) {
            if (!mannschaftspiel.getChecked())
                continue;

            try {
                Date d = format.parse(mannschaftspiel.getDate());
                if (now.getTime() < d.getTime()) {
                    Log.d(Constants.LOG_TAG, "Date=" + d);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(d);
                    cal.add(Calendar.HOUR, 3); // 3h
                    String title = String.format("%s - %s",
                            mannschaftspiel.getHeimMannschaft().getName(),
                            mannschaftspiel.getGastMannschaft().getName());
                    if (!isEventAlreadyExist(title, d.getTime(), cal.getTimeInMillis())) {
                        ContentValues values = createEntry(calID, d.getTime(), cal.getTimeInMillis(), title, mannschaftspiel.getActualSpellokal());
                        cr.insert(CalendarContract.Events.CONTENT_URI, values);
                        Log.d(Constants.LOG_TAG, "created=" + values);
                        counter++;
                    } else {
                        exists++;
                        Log.d(Constants.LOG_TAG, "event exists " + title);
                    }
                }
                //todo remove before online
                if (counter > 0 || exists > 0) break;
            } catch (ParseException e) {
                Log.e(Constants.LOG_TAG, e.getMessage());
            }

        }

        Toast.makeText(this,
                "Es wurden " + counter + " Einträge in den Kalender geschrieben\n" +
                        exists + " Einträge existierten bereits",
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
