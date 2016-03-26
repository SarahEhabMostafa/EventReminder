package com.sarahehabm.eventreminder.model;

import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.sarahehabm.eventreminder.controller.PreferencesUtility;

import java.util.Arrays;

/**
 * Created by Sarah E. Mostafa on 19-Mar-16.
 */
public class UserCredential {
    private static UserCredential instance;

    private GoogleAccountCredential mCredential;
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };


    private UserCredential(Context context) {
//        SharedPreferences settings = PreferencesUtility.getPreferences(context);

        mCredential = GoogleAccountCredential.usingOAuth2(
                context, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(PreferencesUtility.getGoogleAccount(context));
    }

    public static UserCredential getInstance(Context context) {
        if(instance == null)
            instance = new UserCredential(context);

        return instance;
    }

    public GoogleAccountCredential getCredential() {
        return mCredential;
    }

    public void setCredential(GoogleAccountCredential mCredential) {
        this.mCredential = mCredential;
    }
}
