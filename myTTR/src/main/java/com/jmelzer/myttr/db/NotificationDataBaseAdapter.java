package com.jmelzer.myttr.db;

import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.model.LastNotification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by J. Melzer on 01.04.2015.
 * Stores and rtrieves data for notifications.
 *
 * @see com.jmelzer.myttr.model.LastNotification
 */
public class NotificationDataBaseAdapter implements DbAdapter {
    static final String TABLE_NAME = "LAST_NOTICFICATION";

    static final String TABLE_CREATE = "create table " + TABLE_NAME +
            "( " + "ID" + " integer primary key autoincrement," +
            "CHANGED_AT date, TYPE  text, JSON_DATA text ); ";
    private static final String TYPE_FIELD = "TYPE";
    public static final String JSON_DATA_FIELD = "JSON_DATA";
    public static final String CHANGED_AT = "CHANGED_AT";
    // Variable to hold the database instance
    static SQLiteDatabase db;
    // Database open/upgrade helper
    private DataBaseHelper dbHelper;

    public NotificationDataBaseAdapter(Context _context) {
        dbHelper = DataBaseHelper.getInstance(_context);
    }

    public NotificationDataBaseAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public long insertEntry(String type, String data) {
        db.beginTransaction();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("CHANGED_AT", DbUtil.formatter.format(new Date()));
        newValues.put(TYPE_FIELD, type);
        newValues.put(JSON_DATA_FIELD, data);

        // Insert the row into your table
        long l = db.insert(TABLE_NAME, null, newValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        return l;
    }

    public int deleteEntry(String type) {
        return DbUtil.deleteEntry(db, TABLE_NAME, TYPE_FIELD, type);
    }

    public void deleteAllEntries() {
        DbUtil.deleteAllEntries(db, TABLE_NAME);
    }

    public boolean existsEntry(String name) {
        return DbUtil.existsEntry(db, TABLE_NAME, TYPE_FIELD, name);
    }

    public LastNotification getEntryByType(String type) {
        Cursor cursor = db.query(TABLE_NAME, null, TYPE_FIELD + " = '" + type + "'", null, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        LastNotification notification;
        do {
            String dbType = cursor.getString(cursor.getColumnIndex(TYPE_FIELD));
            String jsonData = cursor.getString(cursor.getColumnIndex(JSON_DATA_FIELD));
            Date changedAt = DbUtil.getSafeDate(cursor, CHANGED_AT);
            notification = new LastNotification(changedAt, dbType, jsonData);
        } while (cursor.moveToNext());

        cursor.close();
        return notification;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }
}