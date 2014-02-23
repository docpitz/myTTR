/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */

package com.jmelzer.myttr.activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.User;

public class LoginDataBaseAdapter {
    public static final int NAME_COLUMN = 1;
    static final String DATABASE_NAME = "login.db";
    static final int DATABASE_VERSION = 2;
    // SQL Statement to create a new database.
    static final String DATABASE_CREATE = "create table " + "LOGIN" +
                                          "( " + "ID" + " integer primary key autoincrement," +
                                          "USERNAME  text,PASSWORD text, POINTS NUMERIC); ";
    // Context of the application using the database.
    private final Context context;
    // Variable to hold the database instance
    public SQLiteDatabase db;
    // Database open/upgrade helper
    private DataBaseHelper dbHelper;

    public LoginDataBaseAdapter(Context _context) {
        context = _context;
        dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public LoginDataBaseAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }

    public long insertEntry(String userName, String password, int points) {
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("USERNAME", userName);
        newValues.put("PASSWORD", password);
        newValues.put("POINTS", points);

        // Insert the row into your table
        return db.insert("LOGIN", null, newValues);
    }

    public int deleteEntry(String UserName) {
        String where = "USERNAME=?";
        int numberOFEntriesDeleted = db.delete("LOGIN", where, new String[]{UserName});
        return numberOFEntriesDeleted;
    }

    public void deleteAllEntriesIfErrors() {
        Cursor cursor = db.query("LOGIN", null, " USERNAME is not null", null, null, null, null);
        if (cursor.getCount() > 1) {
            cursor.close();
            db.execSQL("delete from LOGIN");
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
        String password = cursor.getString(cursor.getColumnIndex("PASSWORD"));
        String username = cursor.getString(cursor.getColumnIndex("USERNAME"));
        int points = cursor.getInt(cursor.getColumnIndex("POINTS"));
        cursor.close();
        return new User(username, password, points);
    }

    public void updateEntry(String userName, String password) {
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put("USERNAME", userName);
        updatedValues.put("PASSWORD", password);

        String where = "USERNAME = ?";
        db.update("LOGIN", updatedValues, where, new String[]{userName});
    }
}