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
import com.jmelzer.myttr.model.Favorite;
import com.jmelzer.myttr.model.Verein;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FavoriteDataBaseAdapter implements DbAdapter {

    static final String TABLE_NAME = "FAVS";
    static final String NAME = "NAME";

    static final String FAV_LIGA_CREATE = "create table " + TABLE_NAME +
            "( " + "ID" + " integer primary key autoincrement," +
            NAME + " text, URL  text, CLZ, text,  CHANGED_AT date); ";

    // Variable to hold the database instance
    static SQLiteDatabase db;
    // Database open/upgrade helper
    private DataBaseHelper dbHelper;

    public FavoriteDataBaseAdapter(Context _context) {
        dbHelper = DataBaseHelper.getInstance(_context.getApplicationContext());
    }

    public FavoriteDataBaseAdapter open() throws SQLException {
        if (db == null) {
            db = dbHelper.getWritableDatabase();
        }
        return this;
    }

    public long insertEntry(String name, String url, String clz) {
        db.beginTransaction();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put(NAME, name);
        newValues.put("URL", url);
        newValues.put("CLZ", clz);
        newValues.put("CHANGED_AT", DbUtil.formatter.format(new Date()));

        // Insert the row into your table
        long l = db.insert(TABLE_NAME, null, newValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        return l;
    }

    public int deleteEntry(String name) {
        String where = NAME + "=?";
        db.beginTransaction();
        int numberOFEntriesDeleted = db.delete(TABLE_NAME, where, new String[]{name});
        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d(Constants.LOG_TAG, "removed " + numberOFEntriesDeleted + " from the table " + TABLE_NAME);
        return numberOFEntriesDeleted;
    }

    public void deleteAllEntries() {
        db.beginTransaction();
        db.execSQL("delete from " + TABLE_NAME);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<Favorite> getAllEntries() {
        Cursor cursor = db.query(TABLE_NAME, null, NAME + " is not null", null, null, null, null);
        List<Favorite> list = new ArrayList<>();
        if (cursor.getCount() < 1) {
            cursor.close();
            return list;
        }
        cursor.moveToFirst();
        do {
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String url = cursor.getString(cursor.getColumnIndex("URL"));
            String ds = cursor.getString(cursor.getColumnIndex("CHANGED_AT"));
            String clzName = cursor.getString(cursor.getColumnIndex("CLZ"));
            try {
                Date d = DbUtil.formatter.parse(ds);
                Class clz = Class.forName(clzName);
                Favorite fav = (Favorite) clz.newInstance();
                fav.setName(name);
                fav.setUrl(url);
                fav.setChangedAt(d);
                list.add(fav);

            } catch (Exception e) {
                Log.e(Constants.LOG_TAG, "", e);
            }
        } while (cursor.moveToNext());

        cursor.close();
        return list;
    }

    public boolean existsEntry(String name) {
        Cursor cursor = db.query(TABLE_NAME, null, NAME + " = '" + name + "'", null, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //todo make a real upgrade
        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion)
        {
            switch (upgradeTo)
            {
                case 16:
                    db.execSQL(FAV_LIGA_CREATE);
                    db.execSQL("INSERT INTO FAVS (NAME , URL  , CLZ,  CHANGED_AT) " +
                            "select LIGA_NAME as name, url as url , 'com.jmelzer.myttr.Liga' as clz, CHANGED_AT as CHANGED_AT from FAV_LIGA");
                    break;
            }
            upgradeTo++;
        }
//            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FAV_LIGA_CREATE);
    }
}