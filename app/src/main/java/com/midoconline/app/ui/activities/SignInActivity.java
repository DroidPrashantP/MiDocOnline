package com.midoconline.app.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.midoconline.app.R;
import com.midoconline.app.Util.Constants;
import com.midoconline.app.Util.NetworkManager;
import com.midoconline.app.Util.SharePreferences;
import com.midoconline.app.Util.StringUtils;
import com.midoconline.app.Util.Utils;
import com.midoconline.app.Util.VolleyController;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, Constants.UserInfoKeys {

    private static final String TAG = SignUpActivity.class.getName();
    private EditText mEdtEmail,mEdtPassword;
    private Button mSignInBtn;
    private SharePreferences mSharePreferences;
    private RelativeLayout mainContainer;
    private RelativeLayout mMainLayout;
    private TextView mForgotPassword;
    private CallbackManager callbackManager;
    private Button mFacebookLoginButton;
    private TwitterLoginButton twitterLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mSharePreferences = new SharePreferences(this);
        IntiView();
        setToolbar();
        mainContainer.setBackgroundResource(R.drawable.bg);

        //create callback manager for facebook login
        callbackManager = CallbackManager.Factory.create();

        getFbKeyHash( "com.midoconline.app");


//        // twitter login button
//
//        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.login_button);
//        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
//            @Override
//            public void success(Result<TwitterSession> result) {
//                // Do something with result, which provides a TwitterSession for making API calls
//
//                Log.e("Response", ""+result.data.getUserName());
//                Log.e("Response", ""+result.data.getUserId());
//                Log.e("Response", ""+result.data.getAuthToken());
//
//                if (NetworkManager.isConnectedToInternet(SignInActivity.this)) {
//                    Utils.showProgress(SignInActivity.this);
//                    executeSocialLogin(Constants.UserInfoKeys.MODE_TWITTER,""+ result.data.getUserId(), "NA", result.data.getUserName(), ""+result.data.getAuthToken());
//                } else {
//                    Toast.makeText(SignInActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
//                }
//              //  requestEmailAddress(getApplicationContext(), result.data);
//
//            }
//
//            @Override
//            public void failure(TwitterException exception) {
//                // Do something on failure
//            }
//        });
    }

//    private static void requestEmailAddress(final Context context, TwitterSession session) {
//        new TwitterAuthClient().requestEmail(session, new Callback<String>() {
//            @Override
//            public void success(Result<String> result) {
//                Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void failure(TwitterException exception) {
//                Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
    private void IntiView() {
        mFacebookLoginButton = (Button) findViewById(R.id.facebookLoginButton);
        mainContainer = (RelativeLayout) findViewById(R.id.mainContainer);
        mEdtEmail = (EditText) findViewById(R.id.edt_email);
        mEdtPassword = (EditText) findViewById(R.id.edt_password);
        mSignInBtn = (Button) findViewById(R.id.btn_sign_in);
        mForgotPassword = (TextView) findViewById(R.id.forgot_password);
        String htmlString="<u>Forgot Password?</u>";
        mForgotPassword.setText(Html.fromHtml(htmlString));
        mSignInBtn.setOnClickListener(this);
        mForgotPassword.setOnClickListener(this);
        mFacebookLoginButton.setOnClickListener(this);

        mMainLayout = (RelativeLayout) findViewById(R.id.mainContainer);
        mMainLayout.setBackgroundResource(R.drawable.bg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result to the login button.
       // twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if (v == mSignInBtn){
            validation();
        }
        if (v == mForgotPassword){
           Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        }
        if (v == mFacebookLoginButton){
            facebookLogin();
        }
    }

    private void validation(){
        if (StringUtils.isNotEmpty(mEdtEmail.getText().toString()) && StringUtils.isNotEmpty(mEdtPassword.getText().toString())){
            if (NetworkManager.isConnectedToInternet(this)) {
                if (!isValidEmail(mEdtEmail.getText().toString())) {
                    Utils.ShowDialog(getString(R.string.email_pattern_error), this);
                }else {
                ExecutePostRequestForKey();
                }
            }else {
                Toast.makeText(this,getString(R.string.no_internet_connection),Toast.LENGTH_SHORT).show();
            }
        }else {
            if (!StringUtils.isNotEmpty(mEdtEmail.getText().toString())){
                Utils.ShowDialog(getString(R.string.email_error), this);
            }else {
                if (!StringUtils.isNotEmpty(mEdtPassword.getText().toString())) {
                    Utils.ShowDialog(getString(R.string.password_error), this);
                }
            }
        }

    }

    public void ExecutePostRequestForKey(){
        Utils.showProgress(this);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringrequest = new StringRequest(Request.Method.POST,Constants.URL.SIGNINSTEPONE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (StringUtils.isNotEmpty(response)){
                    Log.d(TAG, response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        String secret_key = obj.getString("secret_key");
                        String key = obj.getString("key");
                        mSharePreferences.setKey(secret_key);
                        mSharePreferences.setSecretKey(key);
                        ExecutePostRequestForLogin(secret_key,key);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof AuthFailureError) {
                    try {
                        String responseBody = new String( error.networkResponse.data, "utf-8" );
                        JSONObject jsonObject = new JSONObject( responseBody );
                        String msg = jsonObject.getString("message");
                        Utils.ShowDialog(msg,SignInActivity.this);
                        Utils.closeProgress();
                    } catch ( JSONException e ) {
                        //Handle a malformed json response
                    } catch (UnsupportedEncodingException error1){
                        error1.printStackTrace();
                    }
                }

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",mEdtEmail.getText().toString().trim());
                params.put("password", mEdtPassword.getText().toString().trim());

                return params;
            }

            @Override
            public Map<String, String> getHeaders()  {
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }
        };
        stringrequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringrequest);
    }
    public void ExecutePostRequestForLogin(final String secret_key, final String key){

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringrequest = new StringRequest(Request.Method.POST, Constants.URL.SIGNINSTEPTWO_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.closeProgress();
                Log.d(TAG, response);
                if (StringUtils.isNotEmpty(response)){
                    try {
                        JSONObject mainobj = new JSONObject(response);
                        String status = mainobj.getString("status");
                        JSONObject obj = mainobj.getJSONObject("user") ;
                        if (status.equalsIgnoreCase("Success")) {
                            mSharePreferences.setLoggedIn(true);
                            mSharePreferences.setUserId(obj.getString("id"));
                            mSharePreferences.setUserEmail(obj.getString("email"));
                            mSharePreferences.setUserName(obj.getString("username"));
                            mSharePreferences.setGender(obj.getString("gender"));
                            mSharePreferences.setSecretKey(obj.getString("secret_key"));
                            mSharePreferences.setKey(obj.getString("key"));
                            mSharePreferences.setAuthenticationToken(obj.getString("authentication_token"));
                            mSharePreferences.setBirthdate(obj.getString("dob"));
                            mSharePreferences.setMobile(obj.getString("mobile_no"));
                            mSharePreferences.setUserType(obj.getString("type"));
                            mSharePreferences.setUserSpecialist(obj.getString("specialize"));
                            mSharePreferences.setSurname(obj.getString("surname"));
                            mSharePreferences.setUserLicence(obj.getString("licence"));
                            mSharePreferences.setCity(obj.getString("city"));
                            mSharePreferences.setCountry(obj.getString("country"));
                            mSharePreferences.setTermsAndCondition(obj.getString("terms_and_condition"));
                            mSharePreferences.setUserHeight(obj.getString("height"));
                            mSharePreferences.setUserWeight(obj.getString("weight"));
                            mSharePreferences.setUserBloodGroup(obj.getString("blood_group"));
                            mSharePreferences.setUserZipCode(obj.getString("zip_code"));
                            mSharePreferences.setUserStreet(obj.getString("street"));
                            mSharePreferences.setUserNotes(obj.getString("med_notes"));
                            mSharePreferences.setUserState(obj.getString("state"));
                            mSharePreferences.setUserThumbnail(obj.getString("image_url"));
                            mSharePreferences.setUserFullName(obj.getString("full_name"));

                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        }else if(status.equalsIgnoreCase("Failure")){
                            String message = obj.getString("message");
                            Utils.ShowDialog(message, SignInActivity.this);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    Utils.ShowDialog("Please Check your Email or Password",SignInActivity.this);
                    Utils.closeProgress();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.ShowDialog("Please Check your Email or Password",SignInActivity.this);
                Utils.closeProgress();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("secret_key",secret_key);
                params.put("key", key);
                params.put("device_token", Utils.getDeviceId(SignInActivity.this));
                return params;
            }

            @Override
            public Map<String, String> getHeaders()  {
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }
        };
        stringrequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringrequest);
    }

    public void setToolbar(){
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

    public static void twitterLogin(){

    }

    /**
     * facebook login implementation
     */
    private void facebookLogin() {
        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Utils.closeProgress();
                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Utils.closeProgress();
                        JSONObject json = response.getJSONObject();
                        try {
                            Log.d(TAG, "onSuccess: " + json.toString());
                            saveFacebookUserInfo(json);
                            if (StringUtils.isNotEmpty(mSharePreferences.getFacebookEmailId())) {
                                String accessToken = AccessToken.getCurrentAccessToken().getToken();
                                mSharePreferences.setFBAccessToken(accessToken);

                                if (NetworkManager.isConnectedToInternet(SignInActivity.this)) {
                                    Utils.showProgress(SignInActivity.this);
                                    executeSocialLogin(Constants.UserInfoKeys.MODE_FACEBOOK, mSharePreferences.getFacebookId(), mSharePreferences.getFacebookEmailId(), mSharePreferences.getUserName(), accessToken);
                                } else {
                                    Toast.makeText(SignInActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SignInActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString(Constants.BundleKeys.FIELDS, Constants.BundleKeys.FACEBOOK_PARAM_KEYS);
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "onCancel");
                Utils.closeProgress();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "onError: " + error.toString());
                Utils.closeProgress();
            }
        });
    }

    /**
     * Social login
     *
     * @param loginMode Login Mode
     * @param socialId  Social Id
     * @param emailId   Email Address
     */
    public void executeSocialLogin(final String loginMode, final String socialId, final String emailId, final String username, final String socialAccessToken) {
        Log.e("socialId", socialId);
        String url = Constants.URL.FACEBOOK_URL;

        StringRequest request = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Utils.closeProgress();
                Log.e("Response", response);
                parseSocialResponse(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeProgress();
                Log.e("Response", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (loginMode.equals(Constants.UserInfoKeys.MODE_FACEBOOK)) {
                    params.put("email", emailId);
                }
                if (StringUtils.isNotEmpty(username)) {
                    params.put("username", username);
                }else {
                    params.put("username", "--");
                }
                params.put("dob", "NA");
                params.put("gender", "male");
                params.put("uid", socialId);
                params.put("access_token",socialAccessToken);
                params.put("mobile_no", "NA");
                params.put("image_url", "");
                params.put("terms_and_condition",""+true);
                return params;
            }
        };

        // Adding request to request queue
        request.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyController.getInstance().addToRequestQueue(request, TAG);

    }

    /**
     * call method to save facebook user information in SharedPreference
     *
     * @param userInfoJSon Facebook User Info JSON
     */
    public void saveFacebookUserInfo(JSONObject userInfoJSon) throws JSONException {


        if (!userInfoJSon.isNull(FACEBOOK_ID)) {
            mSharePreferences.setFacebookId(userInfoJSon.getString(FACEBOOK_ID));
            mSharePreferences.setFacebookProfilePicture(userInfoJSon.getJSONObject(FACEBOOK_PROFILE_PIC).getJSONObject(DATA).getString(URL));
            if (userInfoJSon.has(FACEBOOK_COVER_PIC_KEY))
                mSharePreferences.setFacebookCoverPic(userInfoJSon.getJSONObject(FACEBOOK_COVER_PIC_KEY).getString(Constants.UserInfoKeys.SOURCE));
            if (!userInfoJSon.isNull(FACEBOOK_EMAIL_ID)) {
                mSharePreferences.setFacebookEmailId(userInfoJSon.getString(FACEBOOK_EMAIL_ID));
            }
            if (!userInfoJSon.isNull(FACEBOOK_NAME)) {
                mSharePreferences.setFacebookName(userInfoJSon.getString(FACEBOOK_NAME));
            }
        }
    }
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void getFbKeyHash(String packageName) {

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("YourKeyHash :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                System.out.println("YourKeyHash: "+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    public void parseSocialResponse(String response){
        Log.d(TAG, response);
        if (StringUtils.isNotEmpty(response)){
            try {
                JSONObject mainobj = new JSONObject(response);
                String status = mainobj.getString("status");
                JSONObject obj = mainobj.getJSONObject("user") ;
                if (status.equalsIgnoreCase("Success")) {
                    mSharePreferences.setLoggedIn(true);
                    mSharePreferences.setUserId(obj.getString("id"));
                    mSharePreferences.setAuthenticationToken(obj.getString("authentication_token"));
                    mSharePreferences.setUserEmail(obj.getString("email"));
                    mSharePreferences.setUserType(obj.getString("type"));
                    mSharePreferences.setUserThumbnail(obj.getString("image_url"));
                    mSharePreferences.setUserName(obj.getString("full_name"));
                    mSharePreferences.setUserName(obj.getString("username"));
                    mSharePreferences.setSurname(obj.getString("surname"));
                    mSharePreferences.setMobile(obj.getString("mobile_no"));
                    mSharePreferences.setGender(obj.getString("gender"));
                    mSharePreferences.setBirthdate(obj.getString("dob"));
                    mSharePreferences.setTermsAndCondition(obj.getString("terms_and_condition"));
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }else if(status.equalsIgnoreCase("Failure")){
                    String message = obj.getString("message");
                    Utils.ShowDialog(message, SignInActivity.this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            Utils.ShowDialog("Please Check your Email or Password",SignInActivity.this);
            Utils.closeProgress();
        }
    }

}
