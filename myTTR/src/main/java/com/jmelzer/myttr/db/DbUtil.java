package com.jmelzer.myttr.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by J. Melzer on 01.04.2015.
 * Some helper methods for db stuff.
 */
public class DbUtil {
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.GERMAN);
    public static Date getSafeDate(Cursor cursor, String fieldName) {
        String changed = cursor.getString(cursor.getColumnIndex(fieldName));
        Date changedAt = new Date(); //prevent NPE
        if (changed != null) {
            try {
                changedAt = formatter.parse(changed);
            } catch (ParseException e) {
                //ignore
            }
        }
        return changedAt;
    }
    public static boolean existsEntry(SQLiteDatabase db, String tableName, String keyname, String value) {
        Cursor cursor = db.query(tableName, null, keyname + " = '" + value + "'", null, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public static int deleteEntry(SQLiteDatabase db, String tableName, String keyname, String type) {
        String where = keyname + "=?";
        db.beginTransaction();
        int numberOFEntriesDeleted = db.delete(tableName, where, new String[]{type});
        db.setTransactionSuccessful();
        db.endTransaction();
        return numberOFEntriesDeleted;
    }

    public static void deleteAllEntries(SQLiteDatabase db, String tableName) {
        db.beginTransaction();
        db.execSQL("delete from " + tableName);
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
