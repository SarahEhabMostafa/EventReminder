package com.sarahehabm.eventreminder;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.sarahehabm.eventreminder.controller.sync.SyncUtility;

/**
 * Created by Sarah E. Mostafa on 25-Mar-16.
 */
public class EventReminderApplication extends Application {
    private static final String TAG = EventReminderApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.v("APPLICATION CLASS", "onCreate");

        SyncUtility.createSyncAccount(this);
//        Log.e(TAG, "Before syncing");
//        SyncUtils.requestSync();
//        Log.e(TAG, "After syncing");
        
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
