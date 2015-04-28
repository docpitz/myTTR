/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */
package com.jmelzer.myttr.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jmelzer.myttr.Constants;

import java.util.ArrayList;
import java.util.List;


public class DataBaseHelper extends SQLiteOpenHelper {

    static final int DBVERSION = 15;

    static DataBaseHelper dataBaseHelper;

    public static DataBaseHelper getInstance(Context context) {
        if (dataBaseHelper == null) {
            dataBaseHelper = new DataBaseHelper(context.getApplicationContext());
        }
        return dataBaseHelper;
    }

    List<DbAdapter> adapterList = new ArrayList<>();

    private DataBaseHelper(Context context) {
        super(context, "myttr", null, DBVERSION);
    }

    // Called when no database exists in disk and the helper class needs
    // to create a new one.
    @Override
    public void onCreate(SQLiteDatabase db) {
        for (DbAdapter dbAdapter : adapterList) {
            dbAdapter.onCreate(db);
        }

    }

    // Called when there is a database version mismatch meaning that the version
    // of the database on disk needs to be upgraded to the current version.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Log the version upgrade.
        Log.i(Constants.LOG_TAG, "Upgrading from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

        // Upgrade the existing database to conform to the new version. Multiple
        // previous versions can be handled by comparing _oldVersion and _newVersion
        // values.
        // The simplest case is to drop the old table and create a new one.

        for (DbAdapter dbAdapter : adapterList) {
            dbAdapter.onUpgrade(db, oldVersion, newVersion);
        }
        // Create a new one.
        onCreate(db);
    }

    public void registerAdapter(DbAdapter adapter) {
        adapterList.add(adapter);
    }
}