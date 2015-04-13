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
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FavoriteLigaDataBaseAdapter implements DbAdapter{

    static final String TABLE_NAME = "FAV_LIGA";

    static final String FAV_LIGA_CREATE = "create table " + TABLE_NAME +
            "( " + "ID" + " integer primary key autoincrement," +
            "LIGA_NAME text, URL  text, CHANGED_AT date); ";

    // Variable to hold the database instance
    static SQLiteDatabase db;
    // Database open/upgrade helper
    private DataBaseHelper dbHelper;

    public FavoriteLigaDataBaseAdapter(Context _context) {
        dbHelper = DataBaseHelper.getInstance(_context);
    }

    public FavoriteLigaDataBaseAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public long insertEntry(String name, String url) {
        db.beginTransaction();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("LIGA_NAME", name);
        newValues.put("URL", url);
        newValues.put("CHANGED_AT", DbUtil.formatter.format(new Date()));

        // Insert the row into your table
        long l = db.insert(TABLE_NAME, null, newValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        return l;
    }

    public int deleteEntry(String name) {
        String where = "LIGA_NAME=?";
        db.beginTransaction();
        int numberOFEntriesDeleted = db.delete(TABLE_NAME, where, new String[]{name});
        db.setTransactionSuccessful();
        db.endTransaction();
        return numberOFEntriesDeleted;
    }

    public void deleteAllEntries() {
        db.beginTransaction();
        db.execSQL("delete from " + TABLE_NAME);
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    public List<Liga> getAllEntries() {
        Cursor cursor = db.query(TABLE_NAME, null, " LIGA_NAME is not null", null, null, null, null);
        List<Liga> list = new ArrayList<>();
        if (cursor.getCount() < 1) {
            cursor.close();
            return list;
        }
        cursor.moveToFirst();
        do {
            String name = cursor.getString(cursor.getColumnIndex("LIGA_NAME"));
            String url = cursor.getString(cursor.getColumnIndex("URL"));
            list.add(new Liga(name, url));
        } while (cursor.moveToNext());

        cursor.close();
        return list;
    }

    public boolean existsEntry(String name) {
        Cursor cursor = db.query(TABLE_NAME, null, " LIGA_NAME = '" + name + "'", null, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FAV_LIGA_CREATE);
    }
}