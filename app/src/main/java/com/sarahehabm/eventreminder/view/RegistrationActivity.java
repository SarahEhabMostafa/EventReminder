package com.sarahehabm.eventreminder.view;

import android.accounts.AccountManager;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.sarahehabm.eventreminder.R;
import com.sarahehabm.eventreminder.controller.PreferencesUtility;
import com.sarahehabm.eventreminder.controller.sync.SyncUtility;
import com.sarahehabm.eventreminder.model.UserCredential;

import java.util.Arrays;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = RegistrationActivity.class.getSimpleName();

    private GoogleAccountCredential mCredential;
    private CallbackManager callbackManager;

    private TextView textView_google, textView_facebook;
    private Button button_google, button_facebook, button_finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

            ActionBar actionBar = getActionBar();
            if (actionBar != null && actionBar.isShowing())
                actionBar.hide();
        }
        setContentView(R.layout.activity_registration);

        textView_google = (TextView) findViewById(R.id.textView_google);
        textView_facebook = (TextView) findViewById(R.id.textView_facebook);
        button_google = (Button) findViewById(R.id.button_google);
        button_facebook = (Button) findViewById(R.id.button_facebook);
        button_finish = (Button) findViewById(R.id.button_finish);

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker profileTracker;

            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.e(TAG, "FacebookCallback; onSuccess");
                String accessToken = loginResult.getAccessToken().getToken();
                PreferencesUtility.putString(RegistrationActivity.this,
                        PreferencesUtility.KEY_FACEBOOK_ACCESS_TOKEN, accessToken);

                final String userId = loginResult.getAccessToken().getUserId();
                PreferencesUtility.setFacebookUserId(RegistrationActivity.this, userId);

                if(Profile.getCurrentProfile() == null) {
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            Log.v("facebook - profile", profile2.getFirstName());
                            final String tempUsername = profile2.getName();
                            profileTracker.stopTracking();

                            button_facebook.setVisibility(View.INVISIBLE);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    textView_facebook.setVisibility(View.VISIBLE);
                                    textView_facebook.setText(tempUsername);
                                }
                            }, 200);
                        }
                    };
                    profileTracker.startTracking();
                } else {
                    Profile profile = Profile.getCurrentProfile();
                    Log.v("facebook - profile", profile.getFirstName());
                    final String username = profile.getName();

                    button_facebook.setVisibility(View.INVISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textView_facebook.setVisibility(View.VISIBLE);
                            textView_facebook.setText(username);
                        }
                    }, 200);
                }
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "FacebookCallback; onCancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e(TAG, "FacebookCallback; onError");
            }
        });
    }

    public void onGoogleClick(View view) {
        mCredential = UserCredential.getInstance(this).getCredential();
        GoogleServicesUtility.chooseAccount(this);
    }

    public void onFacebookClick(View view) {
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("public_profile", "user_events"));
    }

    public void onFinishClick(View view) {
        Intent intent = new Intent(this, EventsActivity.class);
        SyncUtility.requestSync();
        startActivity(intent);
        finish();
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {

        switch(requestCode) {
            case GoogleServicesUtility.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    GoogleServicesUtility.isGooglePlayServicesAvailable(this);
                }
                break;

            case GoogleServicesUtility.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    final String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        UserCredential.getInstance(this).getCredential().setSelectedAccountName(accountName);
                        PreferencesUtility.setGoogleAccount(this, accountName);

                        button_google.setVisibility(View.INVISIBLE);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                textView_google.setVisibility(View.VISIBLE);
                                textView_google.setText(accountName);
                            }
                        }, 200);
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    //No account selected
                }
                break;

            case EventsActivity.REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    GoogleServicesUtility.chooseAccount(this);
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        if(PreferencesUtility.getGoogleAccount(this) != null
                && PreferencesUtility.getFacebookUserId(this) != null) {
            button_finish.setEnabled(true);
        } else
            button_finish.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!GoogleServicesUtility.isGooglePlayServicesAvailable(this)) {
            Toast.makeText(this, "Google Play Services required: " +
                    "after installing, close and relaunch this app.", Toast.LENGTH_SHORT).show();
        }
    }
}
