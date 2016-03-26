package com.sarahehabm.eventreminder.controller.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.sarahehabm.eventreminder.controller.database.EventsContract.EventEntry;

/**
 * Created by Sarah E. Mostafa on 17-Mar-16.
 */
public class EventsContentProvider extends ContentProvider {
    private EventsDbHelper dbHelper;

    public static final String AUTHORITY = EventsContract.CONTENT_AUTHORITY;

    public static final int EVENTS = 100;
    public static final int EVENT_WITH_ID = 101;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, EventEntry.TABLE_NAME, EVENTS);
        uriMatcher.addURI(AUTHORITY, EventEntry.TABLE_NAME + "/*", EVENT_WITH_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new EventsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case EVENTS: {
                cursor = dbHelper.getReadableDatabase().query(
                        EventEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case EVENT_WITH_ID: {
                if(selection == null)
                    selection = EventEntry.COLUMN_EVENT_ID + " = ?";
                else
                    selection += " AND " + EventEntry.COLUMN_EVENT_ID + " = ?";

                String[] newSelectionArgs = selectionArgs;
                if(newSelectionArgs == null)
                    newSelectionArgs = new String[] {EventEntry.COLUMN_EVENT_ID};
                else
                    newSelectionArgs = concatArrays(selectionArgs,
                            new String[] {EventEntry.COLUMN_EVENT_ID});

                cursor = dbHelper.getReadableDatabase().query(
                        EventEntry.TABLE_NAME,
                        projection,
                        selection,
                        newSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case EVENTS:
                return EventEntry.CONTENT_TYPE;

            case EVENT_WITH_ID:
                return EventEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case EVENTS:
                long id = database.insert(EventEntry.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = EventEntry.buildLocationUri(id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);
        int rowsDeleted = 0;

        if(selection == null)
            selection = "1";

        switch (match) {
            case EVENTS: {
                rowsDeleted = database.delete(EventEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        if(rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);

        if(selection == null)
            selection = "1";

        int rowsUpdated;

        switch (match) {
            case EVENTS:
                rowsUpdated =
                        database.update(EventEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsUpdated!=0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);

        switch (match) {
            case EVENTS:
                database.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues contentValues : values) {
                        long id = database.insert(EventEntry.TABLE_NAME, null, contentValues);
                        if(id != -1)
                            returnCount++;
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    public String[] concatArrays(String[] arr1, String[] arr2) {
        String[] returnArr = new String[] {};

        if(arr1 == null && arr2 == null)
            return null;
        else if(arr1 == null)
            return arr2;
        else if(arr2 == null)
            return arr1;

        returnArr = new String[arr1.length + arr2.length];
        int i=0;
        for (int j=0; j<arr1.length; j++)
            returnArr[j] = arr1[j];

        for (int j=i, k=0; j<arr2.length; j++, k++)
            returnArr[j] = arr2[k];

        return returnArr;
    }
}
