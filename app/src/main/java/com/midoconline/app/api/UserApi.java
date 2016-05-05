package com.midoconline.app.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.midoconline.app.MiDocOnlineApplication;
import com.midoconline.app.Util.SharePreferences;
import com.midoconline.app.Util.Utils;
import com.midoconline.app.Util.VolleyController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Prashant on 28/12/15.
 */
public class UserApi {

    SharePreferences sharePreferences;
    public interface UserEventListener {
        void onComplete(JSONObject jsonObject, String ApiType);

        void onError(VolleyError volleyError, String ApiType);

    }

    private static final String TAG = UserApi.class.getSimpleName();
    private UserEventListener mUserEventListener;

    /**
     * Parametrized constructor
     *
     * @param context
     * @param userEventListener
     */
    public UserApi(Context context, UserEventListener userEventListener) {
        mUserEventListener = userEventListener;
        sharePreferences = new SharePreferences(context);

    }

    /**
     * fetch all user of current user account
     *
     * @param url
     */
    public void fetchAllUser(String url, final String apiType) {

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Log.d("response", response.toString());
                try {
                    mUserEventListener.onComplete(new JSONObject(response), apiType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  Log.e("response", error.toString());
                mUserEventListener.onError(error, apiType);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return Utils.setHeaders(sharePreferences.getVideoSessionToken());
            }
        };
        VolleyController.getInstance().addToRequestQueue(request);
    }

    /**
     * fetch all user of current user account
     *
     * @param url
     */
    public void createSession(String url, final String apiType) {

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Log.d("response", response.toString());
                try {
                    mUserEventListener.onComplete(new JSONObject(response), apiType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  Log.e("response", error.toString());
                mUserEventListener.onError(error, apiType);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return Utils.setHeaders(sharePreferences.getVideoSessionToken());
            }
        };
        VolleyController.getInstance().addToRequestQueue(request);
    }
}
