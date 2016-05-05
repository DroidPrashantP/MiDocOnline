package com.midoconline.app.ui.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.midoconline.app.R;
import com.midoconline.app.Util.Constants;

public class AskForRegistration extends AppCompatActivity {

    Button mBtnRegisteredDoctor, mWantToTegistered;
    private RelativeLayout mMainLayout;
    String mUserType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for_registration);
        setToolbar();

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            mUserType = bundle.getString(Constants.BundleKeys.USERTYPE);
        }
        mMainLayout = (RelativeLayout) findViewById(R.id.mainContainer);
        mBtnRegisteredDoctor = (Button) findViewById(R.id.btn_registered_doctor);
        mWantToTegistered = (Button) findViewById(R.id.btn_want_to_registered);

        if (mUserType.equalsIgnoreCase(Constants.BundleKeys.PATIENT)) {
            mBtnRegisteredDoctor.setText("LOG IN");
            mWantToTegistered.setText("SIGN UP");
        }else {
            mBtnRegisteredDoctor.setText("REGISTERED DOCTOR");
        }

        mBtnRegisteredDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AskForRegistration.this, SignInActivity.class);
                if (mUserType.equalsIgnoreCase(Constants.BundleKeys.PATIENT)) {
                    intent.putExtra(Constants.BundleKeys.USERTYPE,Constants.BundleKeys.PATIENT);
                    startActivity(intent);
                }else {
                    intent.putExtra(Constants.BundleKeys.USERTYPE,Constants.BundleKeys.DOCTOR);
                    startActivity(intent);
                }
            }
        });

        mWantToTegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AskForRegistration.this, SignUpActivity.class);
                if (mUserType.equalsIgnoreCase(Constants.BundleKeys.PATIENT)) {
                    intent.putExtra(Constants.BundleKeys.USERTYPE,Constants.BundleKeys.PATIENT);
                    startActivity(intent);
                }else {
                    intent.putExtra(Constants.BundleKeys.USERTYPE,Constants.BundleKeys.DOCTOR);
                    startActivity(intent);
                }
            }
        });
        mMainLayout.setBackgroundResource(R.drawable.bg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ask_for_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ImageView nav_drawer = (ImageView) findViewById(R.id.nav_drawer);
        nav_drawer.setImageResource(R.drawable.back_arrow);
        nav_drawer.setColorFilter(getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        nav_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
