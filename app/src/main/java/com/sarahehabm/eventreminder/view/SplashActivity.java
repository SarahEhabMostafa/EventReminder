package com.sarahehabm.eventreminder.view;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.sarahehabm.eventreminder.R;
import com.sarahehabm.eventreminder.controller.PreferencesUtility;

public class SplashActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String googleAccount = PreferencesUtility.getGoogleAccount(SplashActivity.this),
                        facebookUserId = PreferencesUtility.getFacebookUserId(SplashActivity.this);

                Intent intent;
                if(googleAccount == null || facebookUserId == null)
                    intent = new Intent(SplashActivity.this, RegistrationActivity.class);
                else
                    intent = new Intent(SplashActivity.this, EventsActivity.class);

//                intent = new Intent(SplashActivity.this, RegistrationActivity.class);


                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}
