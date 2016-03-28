package com.sarahehabm.eventreminder.controller.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sarah E. Mostafa on 18-Mar-16.
 */
public class EventsDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "events.db";
    public static final int DATABASE_VERSION = 6;

    public EventsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EventsContract.EventEntry.SQL_CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EventsContract.EventEntry.TABLE_NAME);
        onCreate(db);
    }
}
