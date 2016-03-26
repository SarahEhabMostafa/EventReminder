package com.sarahehabm.eventreminder.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.sarahehabm.eventreminder.model.UserCredential;

/**
 * Created by Sarah E. Mostafa on 19-Mar-16.
 */
public final class GoogleServicesUtility {
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    public static final void chooseAccount(Activity activity) {
        activity.startActivityForResult(
                UserCredential.getInstance(activity).getCredential().newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    public static final boolean isDeviceOnline(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    public static final boolean isGooglePlayServicesAvailable(Activity activity) {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(activity, connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    public static final void showGooglePlayServicesAvailabilityErrorDialog(Activity activity,
            final int connectionStatusCode) {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                connectionStatusCode,
                activity,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
}
