package com.sarahehabm.eventreminder.controller;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sarah E. Mostafa on 19-Mar-16.
 */
public final class PreferencesUtility {
    private static SharedPreferences preferences;

    public static final String PREFERENCES_NAME = "events_preferences";
    public static final String KEY_GOOGLE_ACCOUNT = "google_account";
    public static final String KEY_FACEBOOK_ACCOUNT = "facebook_account";
    public static final String KEY_FACEBOOK_ACCESS_TOKEN = "facebook_access_token";
    public static final String KEY_FACEBOOK_USER_ID = "facebook_user_id";

    public static SharedPreferences getPreferences(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        return preferences;
    }

    public static String getString(Context context, String s) {
        getPreferences(context);

        return preferences.getString(s, null);
    }

    public static boolean putString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getGoogleAccount(Context context) {
        return getString(context, KEY_GOOGLE_ACCOUNT);
    }

    public static boolean setGoogleAccount(Context context, String accountName) {
        return putString(context, KEY_GOOGLE_ACCOUNT, accountName);
    }

    public static String getFacebookUserId(Context context) {
        return getString(context, KEY_FACEBOOK_USER_ID);
    }

    public static boolean setFacebookUserId(Context context, String accountName) {
        return putString(context, KEY_FACEBOOK_USER_ID, accountName);
    }
}
