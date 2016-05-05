package com.midoconline.app.ui.activities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.midoconline.app.R;
import com.midoconline.app.Util.SharePreferences;


public class SplashScreenActivity extends Activity {

    private SharePreferences mSharePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        mSharePreference = new SharePreferences(this);

       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
                   Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                   startActivity(i);
                   finish();
           }
       },3000);
    }
}
