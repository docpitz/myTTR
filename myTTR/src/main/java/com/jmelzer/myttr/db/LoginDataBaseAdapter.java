/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */

package com.jmelzer.myttr.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginDataBaseAdapter {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");


    // Variable to hold the database instance
    static SQLiteDatabase db;
    // Database open/upgrade helper
    private static DataBaseHelper dbHelper;

    public LoginDataBaseAdapter(Context _context) {
        if (dbHelper == null) {
            dbHelper = new DataBaseHelper(_context);
        }
    }

    public LoginDataBaseAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
//        db.close();
    }

    public long insertEntry(String realName, String userName, String password, int points, String clubName, int ak) {
        db.beginTransaction();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("REALNAME", realName);
        newValues.put("USERNAME", userName);
        newValues.put("PASSWORD", password);
        newValues.put("POINTS", points);
        newValues.put("AK", ak);
        newValues.put("CLUB_NAME", clubName);
        newValues.put("CHANGED_AT", formatter.format(new Date()));

        // Insert the row into your table
        long l = db.insert("LOGIN", null, newValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        return l;
    }

    public void storeClub(String clubName) {
//        if (db == null || !db.isOpen()) {
//            open();
//        }
        db.beginTransaction();
        db.execSQL("update LOGIN set CLUB_NAME='" + clubName + "'");
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public int deleteEntry(String userName) {
        String where = "USERNAME=?";
        db.beginTransaction();
        int numberOFEntriesDeleted = db.delete("LOGIN", where, new String[]{userName});
        db.setTransactionSuccessful();
        db.endTransaction();
        return numberOFEntriesDeleted;
    }

    public void deleteAllEntries() {
        db.beginTransaction();
        db.execSQL("delete from LOGIN");
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void deleteAllEntriesIfErrors() {
        Cursor cursor = db.query("LOGIN", null, " USERNAME is not null", null, null, null, null);
        if (cursor.getCount() > 1) {
            cursor.close();
            db.beginTransaction();
            db.execSQL("delete from LOGIN");
            db.setTransactionSuccessful();
            db.endTransaction();
            Log.i(Constants.LOG_TAG, "cleanuped table");
        }
    }

    public User getSinlgeEntry() {
        Cursor cursor = db.query("LOGIN", null, " USERNAME is not null", null, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        String realName = cursor.getString(cursor.getColumnIndex("REALNAME"));
        String password = cursor.getString(cursor.getColumnIndex("PASSWORD"));
        String username = cursor.getString(cursor.getColumnIndex("USERNAME"));
        String clubName = cursor.getString(cursor.getColumnIndex("CLUB_NAME"));
        int points = cursor.getInt(cursor.getColumnIndex("POINTS"));
        int ak = cursor.getInt(cursor.getColumnIndex("AK"));
        String changed = cursor.getString(cursor.getColumnIndex("CHANGED_AT"));
        Date changedAt = new Date(); //prevent NPE
        if (changed != null) {
            try {
                changedAt = formatter.parse(changed);
            } catch (ParseException e) {
                //ignore
            }
        }
        cursor.close();
        User u = new User(realName, username, password, points, changedAt, clubName, ak);
        return u;
    }

    public void storeAk(int ak) {
        db.beginTransaction();
        db.execSQL("update LOGIN set AK=" + ak);
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}