package com.midoconline.app.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class PaymentSignupActivity extends AppCompatActivity implements View.OnClickListener, Constants.UserInfoKeys {

    private static final String TAG = SignUpActivity.class.getName();
    private EditText mEdtEmail,mEdtPassword, mEdtConfPassword;
    private Button mSignUpBtn;
    private SharePreferences mSharePreferences;
    private RelativeLayout mainContainer;
    private RelativeLayout mMainLayout;
    private CallbackManager callbackManager;
    private Button mFacebookLoginButton;
    private TwitterLoginButton twitterLoginButton;
    private TextView alreadyRegistered;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_signup);
        mSharePreferences = new SharePreferences(this);
        IntiView();
        setToolbar();
        mainContainer.setBackgroundResource(R.drawable.bg);

        //create callback manager for facebook login
        callbackManager = CallbackManager.Factory.create();

        // twitter login button

        // twitter login button

        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls

                Log.e("Response", "" + result.data.getUserName());
                Log.e("Response", ""+result.data.getUserId());
                Log.e("Response", ""+result.data.getAuthToken());

                if (NetworkManager.isConnectedToInternet(PaymentSignupActivity.this)) {
                    Utils.showProgress(PaymentSignupActivity.this);
                    executeSocialLogin(Constants.UserInfoKeys.MODE_TWITTER,""+ result.data.getUserId(), "NA", result.data.getUserName(), ""+result.data.getAuthToken());
                } else {
                    Toast.makeText(PaymentSignupActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
                //  requestEmailAddress(getApplicationContext(), result.data);

            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });
    }

    private static void requestEmailAddress(final Context context, TwitterSession session) {
        new TwitterAuthClient().requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void IntiView() {
        mFacebookLoginButton = (Button) findViewById(R.id.facebookLoginButton);
        mainContainer = (RelativeLayout) findViewById(R.id.mainContainer);
        mEdtEmail = (EditText) findViewById(R.id.edt_email);
        mEdtPassword = (EditText) findViewById(R.id.edt_password);
        mEdtConfPassword = (EditText) findViewById(R.id.edt_ConfPassword);
        mSignUpBtn = (Button) findViewById(R.id.btn_sign_in);
        mSignUpBtn.setOnClickListener(this);
        mFacebookLoginButton.setOnClickListener(this);

        mMainLayout = (RelativeLayout) findViewById(R.id.mainContainer);
        mMainLayout.setBackgroundResource(R.drawable.bg);

        alreadyRegistered = (TextView) findViewById(R.id.alreadyRegister);
        String htmlString="Already Registered ?<u> Sign in</u>";
        alreadyRegistered.setText(Html.fromHtml(htmlString));
        alreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentSignupActivity.this, SignInActivity.class);
                intent.putExtra(Constants.BundleKeys.USERTYPE, Constants.BundleKeys.PATIENT);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result to the login button.
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if (v == mSignUpBtn){
            validation();
        }
        if (v == mFacebookLoginButton){
            facebookLogin();
        }
    }

    private void validation(){
        String email = mEdtEmail.getText().toString();
        String password = mEdtPassword.getText().toString();
        String confirmPassword = mEdtConfPassword.getText().toString();

        if (StringUtils.isNotEmpty(email) && StringUtils.isNotEmpty(password)  && StringUtils.isNotEmpty(confirmPassword)){
            if (NetworkManager.isConnectedToInternet(this)) {
                if (!isValidEmail(email)) {
                    Utils.ShowDialog(getString(R.string.email_pattern_error), this);
                }else {
                    if (password.equalsIgnoreCase(confirmPassword)) {
                        if (email.length() >= 8) {
                            Utils.showProgress(this);
                            ExecutePostRequest();
                        } else {
                            Utils.ShowDialog(getString(R.string.min_password_error),this);
                        }

                    }else {
                        Utils.ShowDialog(getString(R.string.confirm_password_error), PaymentSignupActivity.this);
                    }
                }
            }else {
                Toast.makeText(this,getString(R.string.no_internet_connection),Toast.LENGTH_SHORT).show();
            }
        }else {
            if (!StringUtils.isNotEmpty(email)){
                Utils.ShowDialog(getString(R.string.email_error), this);
            }else {
                if (!StringUtils.isNotEmpty(password)) {
                    Utils.ShowDialog(getString(R.string.password_error), this);
                }else {
                    if (!StringUtils.isNotEmpty(confirmPassword)) {
                        Utils.ShowDialog(getString(R.string.confirm_password_error), this);
                    }
                }
            }
        }

    }

    public void ExecutePostRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringrequest = new StringRequest(Request.Method.POST, Constants.URL.PATIENT_SIGNUP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.closeProgress();
                Log.d(TAG, response);
                ParseReponse(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeProgress();
                Log.d(TAG, "" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", "NA");
                params.put("email", mEdtEmail.getText().toString().trim());
                params.put("password", mEdtPassword.getText().toString().trim());
                params.put("mobile_no", "NA");
                params.put("dob", "NA");
                params.put("surname", "NA");
                params.put("gender", "male");
                params.put("terms_and_condition", "" + true);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        stringrequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringrequest);
    }

    private void ParseReponse(String response) {
        Log.e("Response", response);
        if (StringUtils.isNotEmpty(response)) {
            try {
                JSONObject mainobj = new JSONObject(response);
                String status = mainobj.getString("status");
                if (status.equalsIgnoreCase("Success")) {
                    JSONObject obj = mainobj.getJSONObject("user");
                        String user_type = obj.getString("type");
                        mSharePreferences.setLoggedIn(true);
                        mSharePreferences.setUserEmail(obj.getString("email"));
                        mSharePreferences.setUserName(obj.getString("username"));
                        mSharePreferences.setSurname(obj.getString("surname"));
                        mSharePreferences.setUserId(obj.getString("id"));
                        mSharePreferences.setAuthenticationToken(obj.getString("authentication_token"));
                        mSharePreferences.setKey(obj.getString("key"));
                        mSharePreferences.setSecretKey(obj.getString("secret_key"));
                        mSharePreferences.setMobile(obj.getString("mobile_no"));
                        mSharePreferences.setUserType(user_type);
                        mSharePreferences.setGender(obj.getString("gender"));
                        mSharePreferences.setBirthdate(obj.getString("dob"));

                        Intent intent = new Intent(PaymentSignupActivity.this, PaymentOptionActivity.class);
                        startActivity(intent);
                        finish();

                } else if (status.equalsIgnoreCase("Failure")) {
                    String message = mainobj.getString("message");
                    Utils.ShowDialog(message, PaymentSignupActivity.this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

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

                Utils.showProgress(PaymentSignupActivity.this);
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

                                if (NetworkManager.isConnectedToInternet(PaymentSignupActivity.this)) {
                                    Utils.showProgress(PaymentSignupActivity.this);
                                    executeSocialLogin(Constants.UserInfoKeys.MODE_FACEBOOK, mSharePreferences.getFacebookId(), mSharePreferences.getFacebookEmailId(), mSharePreferences.getUserName(), accessToken);
                                } else {
                                    Toast.makeText(PaymentSignupActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(PaymentSignupActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "onError: " + error.toString());
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
                }else {
                    params.put("email", "abcd"+socialId+"@abc.com");
                }
                params.put("username", username);
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
                    Intent intent = new Intent(PaymentSignupActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }else if(status.equalsIgnoreCase("Failure")){
                    String message = obj.getString("message");
                    Utils.ShowDialog(message, PaymentSignupActivity.this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            Utils.ShowDialog("Please Check your Email or Password",PaymentSignupActivity.this);
            Utils.closeProgress();
        }
    }


    public void clearData() {
        mSharePreferences.ClearSharepreference();
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

