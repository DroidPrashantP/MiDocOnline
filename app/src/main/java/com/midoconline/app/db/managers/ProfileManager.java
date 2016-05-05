package com.midoconline.app.db.managers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.login.LoginManager;
import com.midoconline.app.Util.Constants;
import com.midoconline.app.Util.MultipartUtility;
import com.midoconline.app.Util.SharePreferences;
import com.midoconline.app.Util.StringUtils;
import com.midoconline.app.Util.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Prashant on 30/11/15.
 */
public class ProfileManager  implements Constants.UserInfoKeys{
    private static String UTF_CHARSET = "UTF-8";
    private static String TAG = "ProfileManager";
    private static String LOGOUT_TAG = "logout";
    private static String UNFOLLOW_TAG = "unFollow";
    private static String FOLLOW_TAG = "follow";
    private static String BECOMEFRIEND_TAG = "become_friend";
    private static Context mContext;
    private int mResponseStatusCode;
    private SharePreferences mSharedPreferences;

    public ProfileManager(Context mContext) {
        this.mContext = mContext;
        mSharedPreferences = new SharePreferences(mContext);
    }

    public int getResponseStatusCode() {
        return mResponseStatusCode;
    }

    public String getEditProfile(UserData userData, Context context) throws IOException {
        String response = null;
        response = executeMultipartUtilityWithHeader(Constants.URL.UPDATE_PROFILE, userData, context);
        return response;
    }


    /**
     * store user data into share Preference
     *
     * @param params
     * @param response
     * @throws JSONException
     */
    public void saveUserDataInPreference(UserData params, String response) throws JSONException {
//        mSharedPreferences.setDob(params.mBirthday);
//        mSharedPreferences.setGender(params.mGender);
//        mSharedPreferences.setOccupation(params.mOccupation);
//        JSONObject responseObject = new JSONObject(response);
//        if (responseObject.has("interests")) {
//            mSharedPreferences.setInterests(responseObject.getJSONArray("interests").toString());
//        }
//        mSharedPreferences.setWebsite(params.mWebsite);
//        mSharedPreferences.setBio(params.mBio);
//        String imagePath = params.mUserImagePath;
//        if (!imagePath.equals("")) {
//            JSONObject jsonObject = new JSONObject(response);
//            String newPath = jsonObject.getString(Constants.UserInfoKeys.IMAGE_LARGE);
//            mSharedPreferences.setProfileImage(newPath);
//        }
    }

    /**
     * Execute multipart request with header
     *
     * @param url
     * @param userData
     * @param context
     * @return
     * @throws IOException
     */
    public String executeMultipartUtilityWithHeader(String url, UserData userData, Context context) throws IOException {
        String response = null;
//        MultipartUtility builder = new MultipartUtility(url, UTF_CHARSET, mSharedPreferences.getDDAccessToken());
//        if (StringUtils.isNotEmpty(userData.mUserImagePath)) {
//            File file = new File(userData.mUserImagePath);
//            builder.addFilePart(Constants.UserInfoKeys.AVATAR, file);
//        }
//        builder.addFormField(DOB, userData.mBirthday);
//        builder.addFormField(GENDER, userData.mGender);
//        builder.addFormField(OCCUPATION, userData.mOccupation);
//        builder.addFormField(INTERESTS_LIST, userData.mInterest);
//        builder.addFormField(WEBSITE, userData.mWebsite);
//        builder.addFormField(BIO, userData.mBio);
//        response = builder.finish();
//        mResponseStatusCode = builder.getmResponseStatusCode();
        return response;
    }

}
