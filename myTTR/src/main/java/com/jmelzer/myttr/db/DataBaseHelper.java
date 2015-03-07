/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */
package com.jmelzer.myttr.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;


public class DataBaseHelper extends SQLiteOpenHelper {

    static final int DBVERSION = 10;

    // SQL Statement to create a new database.
    static final String LOGIN_CREATE = "create table " + "LOGIN" +
            "( " + "ID" + " integer primary key autoincrement," +
            "REALNAME text, USERNAME  text,PASSWORD text, " +
            "POINTS NUMERIC, AK NUMERIC, CLUB_NAME text, CHANGED_AT date); ";
    static final String FAV_LIGA_CREATE = "create table " + "FAV_LIGA" +
            "( " + "ID" + " integer primary key autoincrement," +
            "LIGA_NAME text, URL  text, CHANGED_AT date); ";

    public DataBaseHelper(Context context) {
        super(context, "myttr", null, DBVERSION);
    }

    // Called when no database exists in disk and the helper class needs
    // to create a new one.
    @Override
    public void onCreate(SQLiteDatabase _db) {
        _db.execSQL(LOGIN_CREATE);
        _db.execSQL(FAV_LIGA_CREATE);

    }

    // Called when there is a database version mismatch meaning that the version
    // of the database on disk needs to be upgraded to the current version.
    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
        // Log the version upgrade.
        Log.i(Constants.LOG_TAG, "Upgrading from version " + _oldVersion + " to " + _newVersion + ", which will destroy all old data");

        // Upgrade the existing database to conform to the new version. Multiple
        // previous versions can be handled by comparing _oldVersion and _newVersion
        // values.
        // The simplest case is to drop the old table and create a new one.
        _db.execSQL("DROP TABLE IF EXISTS " + "LOGIN");
        // Create a new one.
        onCreate(_db);
    }

}