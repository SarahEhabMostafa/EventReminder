package com.sarahehabm.eventreminder.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.sarahehabm.eventreminder.R;
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
    }
}
