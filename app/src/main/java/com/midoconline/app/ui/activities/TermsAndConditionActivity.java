package com.midoconline.app.ui.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.midoconline.app.R;
import com.midoconline.app.Util.Constants;

public class TermsAndConditionActivity extends AppCompatActivity {

    private RelativeLayout mMainLayout;
    private ImageView mRightBtn, mLeftBtn;
    private String mUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            mUserType = bundle.getString(Constants.BundleKeys.USERTYPE);
        }

        setToolbar();
        mMainLayout = (RelativeLayout) findViewById(R.id.mainContainer);
        mMainLayout.setBackgroundResource(R.drawable.bg);

//        mRightBtn = (ImageView) findViewById(R.id.forward_arrow);
//        mLeftBtn = (ImageView) findViewById(R.id.backward_arrow);
//
//        mRightBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if (mUserType.equalsIgnoreCase(Constants.BundleKeys.PATIENT)) {
////                    Intent i = new Intent(TermsAndConditionActivity.this, AskForRegistration.class);
////                    i.putExtra(Constants.BundleKeys.USERTYPE,Constants.BundleKeys.PATIENT);
////                    startActivity(i);
////                }
//
//            }
//        });
//        mLeftBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_terms_and_condition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
