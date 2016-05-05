package com.midoconline.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.midoconline.app.Util.Constants;


/**
 * Created by tereha on 13.07.15.
 */
public class AutoStartServiceBroadcastReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Log.e("AutoStartService", "Receive Service");
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String login = sharedPreferences.getString(Constants.USER_LOGIN, null);
        String password = sharedPreferences.getString(Constants.USER_PASSWORD, null);

        if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)) {
            Intent serviceIntent = new Intent(context, IncomeCallListenerService.class);
            serviceIntent.putExtra(Constants.USER_LOGIN, login);
            serviceIntent.putExtra(Constants.USER_PASSWORD, password);
            serviceIntent.putExtra(Constants.START_SERVICE_VARIANT, Constants.AUTOSTART);
            context.startService(serviceIntent);
        }
    }
}

