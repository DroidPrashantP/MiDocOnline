package com.midoconline.app.ui.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.midoconline.app.R;
import com.midoconline.app.Util.Constants;
import com.midoconline.app.services.IncomeCallListenerService;
import com.quickblox.chat.QBChatService;
import com.quickblox.users.model.QBUser;


/**
 * Created by Prashant on 26.01.15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final String VERSION_NUMBER = "0.9.4.18062015";
    private static final String APP_VERSION = "App version";
    static ActionBar mActionBar;
    private Chronometer timerABWithTimer;
    private boolean isTimerStarted = false;
    protected QBUser loginedUser;
    private String login;
    private String password;
    protected NotificationManager notificationManager;
    private BroadcastReceiver wifiStateReceiver;
    private boolean isConnectivityExists;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWiFiManagerListener();
        QBChatService.setDebugEnabled(true);

        if (QBChatService.isInitialized()) {
            if (QBChatService.getInstance().isLoggedIn()) {
                loginedUser = QBChatService.getInstance().getUser();
            }
        }

//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
//        getSupportActionBar().setIcon(R.drawable.app_logo);
//        getSupportActionBar().setTitle(R.string.app_name);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    public void initActionBar() {
        if (loginedUser != null) {

//            mActionBar = getSupportActionBar();
//            mActionBar.setDisplayShowHomeEnabled(false);
//            mActionBar.setDisplayShowTitleEnabled(false);

//            LayoutInflater mInflater = LayoutInflater.from(this);
//            View mCustomView = mInflater.inflate(R.layout.actionbar_view, null);
//            TextView numberOfListAB = (TextView) mCustomView.findViewById(R.id.numberOfListAB);
//            numberOfListAB.setBackgroundResource(resourceSelector(
//                    DataHolder.getUserIndexByID(loginedUser.getId()) + 1));
//            numberOfListAB.setText(String.valueOf(
//                    DataHolder.getUserIndexByID(loginedUser.getId()) + 1));
//
//            numberOfListAB.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(BaseActivity.this);
//                    dialog.setTitle(APP_VERSION);
//                    dialog.setMessage(VERSION_NUMBER);
//                    dialog.show();
//                    return true;
//                }
//            });
//
//            TextView loginAsAB = (TextView) mCustomView.findViewById(R.id.loginAsAB);
//            loginAsAB.setText(R.string.logged_in_as);
//            TextView userNameAB = (TextView) mCustomView.findViewById(R.id.userNameAB);
//            userNameAB.setText(DataHolder.getUserNameByID(loginedUser.getId()));
//            mActionBar.setCustomView(mCustomView);
//            mActionBar.setDisplayShowCustomEnabled(true);
        }
    }

    public void initActionBarWithTimer() {
        if (loginedUser != null) {
//            mActionBar = getSupportActionBar();
//            mActionBar.setDisplayShowHomeEnabled(false);
//            mActionBar.setDisplayShowTitleEnabled(false);

//            LayoutInflater mInflater = LayoutInflater.from(this);
//
//            View mCustomView = mInflater.inflate(R.layout.actionbar_with_timer, null);
//
//            timerABWithTimer = (Chronometer) mCustomView.findViewById(R.id.timerABWithTimer);
//
//            TextView loginAsABWithTimer = (TextView) mCustomView.findViewById(R.id.loginAsABWithTimer);
//            loginAsABWithTimer.setText(R.string.logged_in_as);
//
//            TextView userNameAB = (TextView) mCustomView.findViewById(R.id.userNameABWithTimer);
//            userNameAB.setText(DataHolder.getUserNameByID(loginedUser.getId()));
//
//            mActionBar.setCustomView(mCustomView);
//            mActionBar.setDisplayShowCustomEnabled(true);
        }
    }

    /**
     * Set Toolbar : Add app name as title and app logo as toolbar logo
     */
    public void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ImageView nav_drawer = (ImageView) findViewById(R.id.nav_drawer);
        nav_drawer.setImageResource(R.drawable.ic_back);
        nav_drawer.setColorFilter(getResources().getColor(R.color.blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        nav_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void stopIncomeCallListenerService(){
        stopService(new Intent(this, IncomeCallListenerService.class));
    }

    protected void startListUsersActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    protected void showToast(final int message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), getString(message), Toast.LENGTH_SHORT).show();
            }
        });
    }


    protected String[] getUserDataFromPreferences(){
        String[] userData = new String[2];
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        login = sharedPreferences.getString(Constants.USER_LOGIN, null);
        password = sharedPreferences.getString(Constants.USER_PASSWORD, null);

        userData[0] = login;
        userData[1] = password;

        return userData;
    }

    protected boolean isUserDataEmpty(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        login = sharedPreferences.getString(Constants.USER_LOGIN, null);
        password = sharedPreferences.getString(Constants.USER_PASSWORD, null);

        return TextUtils.isEmpty(login) && TextUtils.isEmpty(password);
    }

    protected void saveUserDataToPreferences(String login, String password){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(Constants.USER_LOGIN, login);
        ed.putString(Constants.USER_PASSWORD, password);
        ed.commit();
    }

    protected void clearUserDataFromPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.remove(Constants.USER_LOGIN);
        ed.remove(Constants.USER_PASSWORD);
        ed.commit();
    }

    private void reloginToChat(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String login = sharedPreferences.getString(Constants.USER_LOGIN, null);
        String password = sharedPreferences.getString(Constants.USER_PASSWORD, null);

        if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)) {
            Intent serviceIntent = new Intent(this, IncomeCallListenerService.class);
            serviceIntent.putExtra(Constants.USER_LOGIN, login);
            serviceIntent.putExtra(Constants.USER_PASSWORD, password);
            serviceIntent.putExtra(Constants.START_SERVICE_VARIANT, Constants.RELOGIN);
            startService(serviceIntent);
        }
    }

    protected void minimizeApp(){
//        Intent startMain = new Intent(Intent.ACTION_MAIN);
//        startMain.addCategory(Intent.CATEGORY_HOME);
//        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(startMain);
    }

    private void initWiFiManagerListener() {
        wifiStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Connection state was changed");
                boolean isConnected = processConnectivityState(intent);
                updateStateIfNeed(isConnected);
            }

            private boolean processConnectivityState(Intent intent) {
                int connectivityType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1);
                // Check does connectivity equal mobile or wifi types
                boolean connectivityState = false;
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();

                if (networkInfo != null){
                    if (connectivityType == ConnectivityManager.TYPE_MOBILE
                            || connectivityType == ConnectivityManager.TYPE_WIFI
                            || networkInfo.getTypeName().equals("WIFI")
                            || networkInfo.getTypeName().equals("MOBILE")) {
                        //should check null because in air plan mode it will be null
                        if (networkInfo.isConnected()) {
                            // Check does connectivity EXISTS for connectivity type wifi or mobile internet
                            // Pay attention on "!" symbol  in line below
                            connectivityState = true;
                        }
                    }
                }
                return connectivityState;
            }

            private void updateStateIfNeed(boolean connectionState) {
                if (isConnectivityExists != connectionState) {
                    processCurrentConnectionState(connectionState);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
    }

    abstract void processCurrentConnectionState(boolean isConnected);

    @Override
    protected void onDestroy() {
        if (wifiStateReceiver != null) {
            unregisterReceiver(wifiStateReceiver);
        }
        super.onDestroy();
    }
}





