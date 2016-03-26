package com.sarahehabm.eventreminder.controller.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SyncService extends Service {
    public final String TAG = SyncService.class.getSimpleName();

    private SyncAdapter syncAdapter = null;

    public SyncService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "Service created.");

        if (syncAdapter == null)
            syncAdapter = new SyncAdapter(getApplicationContext(), true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "Service destroyed.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
