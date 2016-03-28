package com.sarahehabm.eventreminder.view;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.sarahehabm.eventreminder.R;
import com.sarahehabm.eventreminder.controller.database.EventsContract.EventEntry;
import com.sarahehabm.eventreminder.model.UserCredential;

public class EventsActivity extends AppCompatActivity {
    private GoogleAccountCredential mCredential;

    static final int REQUEST_AUTHORIZATION = 1001;

    private static final String TAG = EventsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        mCredential = UserCredential.getInstance(this).getCredential();

        {
            Cursor cursor = getContentResolver().query(EventEntry.CONTENT_URI,
                    new String[]{"Distinct "+ EventEntry.COLUMN_EVENT_STATUS}, null, null, null);
            if(cursor!=null) {
                while (cursor.moveToNext()) {
                    Log.v("STATUS", cursor.getString(cursor.getColumnIndex(EventEntry.COLUMN_EVENT_STATUS)));
                }
            }
        }
    }
}
