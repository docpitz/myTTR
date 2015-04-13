package com.jmelzer.myttr.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by J. Melzer on 02.04.2015.
 * Interface for all db adapters that shall be registered to the Databasehelper.
 */
public interface DbAdapter {

    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    void onCreate(SQLiteDatabase db);
}
