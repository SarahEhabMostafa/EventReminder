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
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.sarahehabm.eventreminder.R;
import com.sarahehabm.eventreminder.controller.PreferencesUtility;
import com.sarahehabm.eventreminder.controller.sync.SyncUtility;
import com.sarahehabm.eventreminder.model.UserCredential;

import java.util.Arrays;

public class RegistrationActivity extends AppCompatActivity {

    public static final String DEMO_ACCOUNT = "Demo Account";
    private static final String TAG = RegistrationActivity.class.getSimpleName();
    private GoogleAccountCredential mCredential;
    private CallbackManager callbackManager;

//    private Button button_google;
//    private TextInputLayout layout_google;

    private TextView textView_google;
    private Button button_google, button_finish;
    private LoginButton facebook_login_button;

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
        button_google = (Button) findViewById(R.id.button_google);
        button_finish = (Button) findViewById(R.id.button_finish);
        facebook_login_button = (LoginButton) findViewById(R.id.facebook_login_button);
        facebook_login_button.setReadPermissions(Arrays.asList(/*"user_status",*/ /*"user_friends",*/
                "public_profile", "user_events"));

        callbackManager = CallbackManager.Factory.create();
        facebook_login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "FacebookCallback; onSuccess");
                String accessToken = loginResult.getAccessToken().getToken();
                PreferencesUtility.putString(RegistrationActivity.this,
                        PreferencesUtility.KEY_FACEBOOK_ACCESS_TOKEN, accessToken);

                String userId = loginResult.getAccessToken().getUserId();
                PreferencesUtility.setFacebookUserId(RegistrationActivity.this, userId);
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
        Intent intent = new Intent(this, FacebookActivity.class);
        startActivity(intent);
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
