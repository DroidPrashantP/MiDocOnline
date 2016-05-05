package com.midoconline.app;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.midoconline.app.Util.Constants;
import com.quickblox.core.QBSettings;
import com.quickblox.users.model.QBUser;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Prashant on 6/10/15.
 */
public class MiDocOnlineApplication extends Application {
    public static final String TAG = MiDocOnlineApplication.class.getSimpleName();
    private static Context mContext;
    public static final int FIRST_USER_ID = 7315838;
    public static final String FIRST_USER_LOGIN = "androidtest1";
    public static final String FIRST_USER_PASSWORD = "android123";
    public static final int SECOND_USER_ID = 7315852;
    public static final String SECOND_USER_LOGIN = "androidtest2";
    public static final String SECOND_USER_PASSWORD = "android123";
    private QBUser currentUser;


    public MiDocOnlineApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        MiDocOnlineApplication.mContext = getApplicationContext();
        TwitterAuthConfig authConfig =  new TwitterAuthConfig("qlb68wuEKNxmDnbW6rYlUdnTS", "vKS5imKAttdmy7pvppnDAY8lOwAyEZFWA6L0Z00ptzZbUcP5J4");
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());

        // Set QuickBlox credentials here
        QBSettings.getInstance().fastConfigInit(Constants.APP_ID, Constants.AUTH_KEY,Constants.AUTH_SECRET);
       // MultiDex.install(this);
        }

    public static Context getAppContext() {
        return MiDocOnlineApplication.mContext;
    }
    public void setCurrentUser(int userId, String userPassword) {
        this.currentUser = new QBUser(userId);
        this.currentUser.setPassword(userPassword);
    }

    public QBUser getCurrentUser() {
        return currentUser;
    }

}
