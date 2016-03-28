package com.sarahehabm.eventreminder.controller.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sarah E. Mostafa on 17-Mar-16.
 */
public class EventsContract {
    public static final String CONTENT_AUTHORITY = "com.sarahehabm.eventreminder.contentprovider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";

        public static final String COLUMN_EVENT_ID = "_id";
        public static final String COLUMN_EVENT_TITLE = "title";
        public static final String COLUMN_EVENT_START_DATE = "start_date";
        public static final String COLUMN_EVENT_END_DATE = "end_date";
        public static final String COLUMN_EVENT_STATUS = "status";
        public static final String COLUMN_EVENT_COLOR = "colorId";
        public static final String COLUMN_EVENT_CREATED_DATE = "created";
        public static final String COLUMN_EVENT_CREATOR = "creator";
        public static final String COLUMN_EVENT_LOCATION = "location";
        public static final String COLUMN_EVENT_ORGANIZER = "organizer";
        public static final String COLUMN_EVENT_UPDATED = "updated";


//        public static final String COLUMN_END = "end";
//        public static final String COLUMN_ETAG = "etag";
//        public static final String COLUMN_HTML_LINK = "htmlLink";
//        public static final String COLUMN_iCalUID = "iCalUID";
//        public static final String COLUMN_KIND = "kind";
//        public static final String COLUMN_REMINDERS = "reminders";
//        public static final String COLUMN_SEQUENCE = "sequence";
//        public static final String COLUMN_START = "start";
//        public static final String COLUMN_TRANSPARENCY = "transparency";


        public static final String SQL_CREATE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_EVENT_ID + " TEXT PRIMARY KEY NOT NULL, "
                + COLUMN_EVENT_TITLE + " TEXT, "
                + COLUMN_EVENT_START_DATE + " INTEGER, "
                + COLUMN_EVENT_END_DATE + " INTEGER, "
                + COLUMN_EVENT_STATUS + " TEXT, "
                + COLUMN_EVENT_LOCATION + " TEXT, "
                + COLUMN_EVENT_CREATED_DATE + " TEXT, "
                + COLUMN_EVENT_CREATOR + " TEXT, "
                + COLUMN_EVENT_ORGANIZER + " TEXT, "
                + COLUMN_EVENT_UPDATED + " TEXT, "
                + COLUMN_EVENT_COLOR + " TEXT"
                + ");";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
