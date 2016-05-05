package com.midoconline.app.api;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.midoconline.app.Util.NetworkManager;
import com.midoconline.app.Util.SharePreferences;
import com.midoconline.app.Util.Utils;
import com.midoconline.app.ui.activities.MainActivity;
import com.midoconline.app.ui.activities.SignUpActivity;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.List;

/**
 * Created by sandeep on 30/12/15.
 */
public class QBApi {

    public static void CreateQBSession(Context context, final SharePreferences mSharePreferences) {
        if (NetworkManager.isConnectedToInternet(context)) {
            QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {
                @Override
                public void onSuccess(QBSession session, Bundle params) {

                    Log.e("session", "" + session.getToken());
                    if (session.getToken() != null)
                        mSharePreferences.setQBSessionToken(session.getToken());
                }

                @Override
                public void onError(List<String> errors) {
                    // errors
                }
            });
        }
    }


    public static void SignupWithQuicBox(final Context context, final SharePreferences mSharePreferences, QBUser qbUser) {
        if (NetworkManager.isConnectedToInternet(context)) {
            QBUsers.signUp(qbUser, new QBEntityCallbackImpl<QBUser>() {
                @Override
                public void onSuccess(QBUser user, Bundle args) {

                    mSharePreferences.setQBLoginName(user.getLogin());
                    mSharePreferences.setQBQBPassword(user.getPassword());
                    Utils.closeProgress();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    ((SignUpActivity) context).finish();
                }

                @Override
                public void onError(List<String> errors) {
                    Log.e("Error", errors.toString());
                }
            });
        }
    }

    public static void SignINWithQuicBox(final Context context, final SharePreferences mSharePreferences, QBUser user) {
        if (NetworkManager.isConnectedToInternet(context)) {
            QBUsers.signIn(user, new QBEntityCallbackImpl<QBUser>() {
                @Override
                public void onSuccess(QBUser user, Bundle args) {

                }

                @Override
                public void onError(List<String> errors) {
                    // error
                }
            });
        }
    }
}
