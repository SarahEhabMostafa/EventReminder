package com.sarahehabm.eventreminder.controller.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {
    private EventsAuthenticator authenticator;

    public AuthenticatorService() {
        super();
        authenticator = new EventsAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(authenticator == null)
            authenticator = new EventsAuthenticator(this);

        return authenticator.getIBinder();
    }
}
