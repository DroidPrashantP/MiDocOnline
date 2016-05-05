package com.midoconline.app.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.midoconline.app.R;
import com.midoconline.app.Util.SharePreferences;

public class AnswerEmeregencyCallScreen extends AppCompatActivity implements View.OnClickListener{

    private  Button mBtnAnsEmergencyCall;
    private  Button mBtnAnswerCall;
    private  Button mBtnHistory;
    private  ImageView mImgCallCircle;
    private  ImageView mImgCallReceive;
    private  CheckBox  mAcceptPaymentTerms;
    private Spinner mMenuSpinner;
    private String[] mMenuspinnerValues = { "Account", "History"};
    private SharePreferences mSharePreferences;
    private RelativeLayout mMainLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_emeregency_call_screen);
        mSharePreferences = new SharePreferences(this);
        setToolbar();
        mMainLayout = (RelativeLayout) findViewById(R.id.mainContainer);
        mBtnAnsEmergencyCall = (Button) findViewById(R.id.btn_emergency_call);
        mBtnAnswerCall = (Button) findViewById(R.id.btn_answercall);
        mBtnHistory = (Button) findViewById(R.id.btn_history);
        mImgCallCircle = (ImageView) findViewById(R.id.img_circle_blue);
        mImgCallReceive = (ImageView) findViewById(R.id.img_call_receive);
        mAcceptPaymentTerms = (CheckBox) findViewById(R.id.checkbox_Accept);

        mBtnAnsEmergencyCall.setOnClickListener(this);
        mBtnAnswerCall.setOnClickListener(this);
        mBtnHistory.setOnClickListener(this);
        mImgCallCircle.setOnClickListener(this);
        mImgCallReceive.setOnClickListener(this);

        mAcceptPaymentTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        mMainLayout.setBackgroundResource(R.drawable.bg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer_emeregency_call_screen, menu);
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
            ShowLogoutDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.btn_emergency_call:
               break;
           case R.id.btn_answercall:
               break;
           case R.id.btn_history:
               Intent intent = new Intent(AnswerEmeregencyCallScreen.this, DoctorHistoryActivity.class);
               startActivity(intent);
               break;
           case R.id.img_circle_blue:
               break;
           case R.id.img_call_receive:
               break;


       }
    }

    public void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();

//        MenuSpinnerAdapter spinnerAdapter = new MenuSpinnerAdapter();
//        mMenuSpinner = (Spinner) findViewById(R.id.toolbar_spinner);
//        mMenuSpinner.setAdapter(spinnerAdapter);

        ImageView nav_drawer = (ImageView) findViewById(R.id.nav_drawer);
        nav_drawer.setImageResource(R.drawable.ic_navigation);
//        nav_drawer.setColorFilter(getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        nav_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSharePreferences.isLoggedIn()) {
                    mMenuSpinner.performClick();
                }
            }
        });
    }

    public class MenuSpinnerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mMenuspinnerValues.length;
        }

        @Override
        public Object getItem(int position) {
            return mMenuspinnerValues[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getDropDownView(final int position, View view, ViewGroup parent) {
            if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
                view = getLayoutInflater().inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
                view.setTag("DROPDOWN");
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getTitle(position));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        Intent i = new Intent(AnswerEmeregencyCallScreen.this, MyProfileActivity.class);
                        startActivity(i);
                        mMenuSpinner.clearFocus();
                    } else if (position == 1){
                        Intent i = new Intent(AnswerEmeregencyCallScreen.this, DoctorHistoryActivity.class);
                        startActivity(i);
                        mMenuSpinner.clearFocus();
                        mMenuSpinner.setVisibility(View.GONE);
                    }
                }
            });

            return view;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
                view = getLayoutInflater().inflate(R.layout.
                        toolbar_spinner_item_actionbar, parent, false);
                view.setTag("NON_DROPDOWN");
            }
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getTitle(position));


            return view;
        }

        private String getTitle(int position) {
            return position >= 0 && position < mMenuspinnerValues.length ? mMenuspinnerValues[position] : "";
        }
    }

    public void Logout(){
        mSharePreferences.ClearSharepreference();
        Intent intent = new Intent(AnswerEmeregencyCallScreen.this , MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * show logout dialog
     */
    private void ShowLogoutDialog() {
        final Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_custom_logout_dialog);
        mDialog.setCancelable(true);
        mDialog.findViewById(R.id.cancel_logout_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.logout_yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
}
