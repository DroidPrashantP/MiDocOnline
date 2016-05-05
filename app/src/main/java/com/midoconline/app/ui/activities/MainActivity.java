package com.midoconline.app.ui.activities;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.midoconline.app.R;
import com.midoconline.app.Util.Constants;
import com.midoconline.app.Util.HttpsTrustManager;
import com.midoconline.app.Util.SessionManager;
import com.midoconline.app.Util.SharePreferences;
import com.midoconline.app.Util.StringUtils;
import com.midoconline.app.Util.Utils;
import com.midoconline.app.beans.Doctor;
import com.midoconline.app.services.IncomeCallListenerService;
import com.midoconline.app.ui.adapters.DoctorListAdapter;
import com.midoconline.app.ui.fragments.NavigationDrawerFragment;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBSettings;
import com.twitter.sdk.android.Twitter;

import org.jivesoftware.smack.SmackException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int NAV_ITEMS_MAIN = R.id.group_main;
    private static final String TAG = MainActivity.class.getName();
    private static MenuItem mPreviousMenuItem;
    public String mSpecialization = "";
    public String mSelectedDoctor;
    //  String[] mMedicalSpecialitySpinnerValues = {"MEDICAL SPECIALIST", "Alergologo", "Cardiologo", "Cirujano General", "Dermatologo", "Dentista", "Endocrinologo", "Gastroenterologo", "Geriatra", "Ginecólogo", "Hematologo", "Hepatologo", "Medicina Interna", "Nefrologo", "Neumologo", "Neurologo", "Oftalmologo", "Oncologo", "Ortopedista", "Otorrinolaringologo", "Pediatra", "Proctologo", "Psiquiatra", "Reumatologo", "Urologo", "Urgenciologo"};
    String[] mMedicalSpecialitySpinnerValues = {"Medical Specialist", "Alergólogo", "Cardiólogo", "Cirujano General", "Dentista", "Dermatólogo", "Endocrinólogo", "Gastroenterólogo", "Geriatra", "Ginecólogo", "Hematólogo", "Hepatólogo", "Medicina Interna", "Nefrólogo", "Neumólogo", "Neurólogo", "Nutriólogo", "Oftalmólogo", "Oncólogo", "Ortopedista", "Otorrinolaringólogo", "Pediatra", "Proctólogo", "Psicólogo", "Psiquiatra", "Reumatólogo", "Urgenciólogo", "Urólogo"};
    ArrayList<Doctor> mDoctorspinnerValues = new ArrayList<Doctor>();
    Spinner mMenuSpinner;
    private DrawerLayout mDrawerLayout;
    private Button mDoctorLogIn, mPatientLogIn;
    private String[] mMenuspinnerValues = {"Account", "History"};
    private SharePreferences mSharePreferences;
    private RelativeLayout mMainLayout, mcallBtnWrapper, mVisitAndHistoryWrapper, mSpecialityWrapper, mDoctorsWrapper;
    private View mView;
    private CheckBox mAgreePaymentTerms;
    private Button mAdultEmergency, mKidsEmergency;
    private RelativeLayout mAgreeMentLayout;
    private TextView mTextViewAgreement;
    private NavigationView navigationView;
    private Toolbar mToolbar;
    private NavigationDrawerFragment navigationDrawerFragment;
    private Spinner doctorSpinner;
    private String stripTokenID;
    private QBChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(mView);
        QBSettings.getInstance().fastConfigInit(Constants.APP_ID, Constants.AUTH_KEY, Constants.AUTH_SECRET);
        mSharePreferences = new SharePreferences(this);
        mMainLayout = (RelativeLayout) findViewById(R.id.content);
        initializeNavigationDrawer();
        IniTiView();
        // getDoctorList();
        if (mSharePreferences.isLoggedIn()) {
            UserLoginWithQB(Constants.BundleKeys.CREATE_USER_SESSION_WITH_QB);
        }
    }


    private void IniTiView() {
        mDoctorLogIn = (Button) findViewById(R.id.btn_doctor_login);
        mDoctorLogIn.setOnClickListener(this);

        mPatientLogIn = (Button) findViewById(R.id.btn_patient_login);
        mPatientLogIn.setOnClickListener(this);

        mcallBtnWrapper = (RelativeLayout) findViewById(R.id.callBtn_wrapper);
        mcallBtnWrapper.setOnClickListener(this);

        mVisitAndHistoryWrapper = (RelativeLayout) findViewById(R.id.visit_and_history_wrapper);
        mVisitAndHistoryWrapper.setOnClickListener(this);

        mKidsEmergency = (Button) findViewById(R.id.btn_kidsemergency);
        mAdultEmergency = (Button) findViewById(R.id.btn_adultemergency);
        mKidsEmergency.setOnClickListener(this);
        mAdultEmergency.setOnClickListener(this);

        mAgreeMentLayout = (RelativeLayout) findViewById(R.id.agreement_wrapper);
        mTextViewAgreement = (TextView) findViewById(R.id.text_agree);
        String htmlString = "<u>Agree Payment Terms</u>";
        mTextViewAgreement.setText(Html.fromHtml(htmlString));
        mTextViewAgreement.setOnClickListener(this);

        Spinner mySpinner = (Spinner) findViewById(R.id.MS_spinner);
        mySpinner.setAdapter(new MyAdapter(this, R.layout.custom_spinner_view, mMedicalSpecialitySpinnerValues));

        doctorSpinner = (Spinner) findViewById(R.id.Doctor_spinner);
        mMainLayout.setBackgroundResource(R.drawable.bg);
        mAgreePaymentTerms = (CheckBox) findViewById(R.id.checkAgreePayterms);
        mSpecialityWrapper = (RelativeLayout) findViewById(R.id.speciality_wrapper);
        mDoctorsWrapper = (RelativeLayout) findViewById(R.id.doctor_main_wrapper);
        mDoctorsWrapper.setVisibility(View.GONE);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    mSpecialization = mMedicalSpecialitySpinnerValues[position];
                    mSharePreferences.setMedicalSpecialist(mMedicalSpecialitySpinnerValues[position]);
                    getDoctorList();
                } else {
                    mSharePreferences.setMedicalSpecialist("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        doctorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // if (position = 0) {
                mSelectedDoctor = mDoctorspinnerValues.get(position).qb_login;
                mSharePreferences.setSelectedDoctor(mSelectedDoctor);
//                } else {
//                    mSharePreferences.setSelectedDoctor("");
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setToolbar();
        HidesLayout();
        invalidateOptionsMenu();
        Utils.closeProgress();
    }

    /**
     * Initialize navigation drawer
     */
    protected void initializeNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }
    }


    public void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer_emeregency_call_screen, menu);
        if (!mSharePreferences.isLoggedIn()) {
            menu.findItem(R.id.action_settings).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ShowLogoutDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == mDoctorLogIn) {
            Intent intent = new Intent(MainActivity.this, AskForRegistration.class);
            intent.putExtra(Constants.BundleKeys.USERTYPE, Constants.BundleKeys.DOCTOR);
            startActivity(intent);
        }
        if (v == mPatientLogIn) {
            Intent i = new Intent(MainActivity.this, AskForRegistration.class);
            i.putExtra(Constants.BundleKeys.USERTYPE, Constants.BundleKeys.PATIENT);
            startActivity(i);
        }
        if (v == mVisitAndHistoryWrapper) {
            if (mSharePreferences.isLoggedIn()) {
                Intent intent = new Intent(MainActivity.this, DoctorHistoryActivity.class);
                intent.putExtra(Constants.BundleKeys.USERTYPE, Constants.BundleKeys.PATIENT);
                startActivity(intent);
            } else {
                Utils.ShowSnackBar(mView, "Please Login First");
            }
        }
        if (v == mcallBtnWrapper) {
            if (mAgreePaymentTerms.isChecked()) {
                if (mSharePreferences.isLoggedIn()) {
                    if (mSharePreferences.getUserType().equalsIgnoreCase(Constants.BundleKeys.PATIENT)) {
                        if (StringUtils.isNotEmpty(mSpecialization)) {
                            if (mDoctorspinnerValues.size() > 0) {
                                mSharePreferences.setEmergencyTag(Constants.NORMAL_CALL);
                                if (StringUtils.isNotEmpty(mSharePreferences.getStripeKey())) {
                                    goForCall(Constants.BundleKeys.NORMAL_CALL);
                                } else {
                                    ShowDialog();
                                }
                            } else {
                                Utils.ShowSnackBar(mView, " No doctor available for this Specialization!");
                            }

                        } else {
                            Utils.ShowSnackBar(mView, "Please select Specialization");
                        }
                    } else {
                        mSharePreferences.setEmergencyTag(Constants.NORMAL_CALL);
                        goForCall(Constants.BundleKeys.NORMAL_CALL);
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                }
            } else {
                Utils.ShowSnackBar(mView, "Please accept Payment Terms");
            }

        }
        if (v == mKidsEmergency) {
            mSharePreferences.setEmergencyTag(Constants.KID_EMERGENCY);
            if (mSharePreferences.isLoggedIn()) {
                if (StringUtils.isNotEmpty(mSharePreferences.getStripeKey())) {
                    goForCall(Constants.BundleKeys.NORMAL_CALL);
                } else {
                    ShowDialog();
                }
            } else {
                Intent intent = new Intent(MainActivity.this, PaymentSignupActivity.class);
                startActivity(intent);
            }
        }
        if (v == mAdultEmergency) {
            mSharePreferences.setEmergencyTag(Constants.ADULT_EMERGENCY);
            if (mSharePreferences.isLoggedIn()) {
                if (StringUtils.isNotEmpty(mSharePreferences.getStripeKey())) {
                    goForCall(Constants.BundleKeys.NORMAL_CALL);
                } else {
                    ShowDialog();
                }
            } else {
                Intent intent = new Intent(MainActivity.this, PaymentSignupActivity.class);
                startActivity(intent);
            }
        }
        if (v == mTextViewAgreement) {
            //if (mSharePreferences.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, TermsAndConditionActivity.class);
            startActivity(intent);
            // }
        }
    }

    public void goForCall(String tag) {
        Utils.showProgress(MainActivity.this);
        String login = mSharePreferences.getQBEmail();
        String password = "password";
        startIncomeCallListenerService(login, password, Constants.LOGIN, tag);
    }

    public void UserLoginWithQB(String tag) {
        String login = mSharePreferences.getQBEmail();
        String password = "password";
        startIncomeCallListenerService(login, password, Constants.LOGIN, tag);
    }

    public void Logout() {
        mSharePreferences.ClearSharepreference();
        logoutFromFacebook();
        TwitterLogout();
        try {
            QBChatService.getInstance().logout();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        SessionManager.setCurrentSession(null);

        Intent intent = new Intent(MainActivity.this, MainActivity.class);
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

    @Override
    protected void onPause() {
        super.onPause();
        Utils.closeProgress();
    }

    /**
     * reset user facebook profile data
     */
    private void logoutFromFacebook() {
        LoginManager.getInstance().logOut();
        mSharePreferences.setFacebookEmailId("");
        mSharePreferences.setFacebookId("");
        mSharePreferences.setFacebookName("");
        mSharePreferences.setFBAccessToken("");
        mSharePreferences.setFacebookCoverPic("");
        mSharePreferences.setFacebookProfilePicture("");
    }

    public void TwitterLogout() {
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        Twitter.getSessionManager().clearActiveSession();
        Twitter.logOut();
    }

    public void HidesLayout() {
        if (mSharePreferences.isLoggedIn()) {
            if (mSharePreferences.getUserType().equalsIgnoreCase(Constants.BundleKeys.DOCTOR)) {
                mPatientLogIn.setVisibility(View.GONE);
                mDoctorLogIn.setVisibility(View.GONE);
                //      mAgreeMentLayout.setVisibility(View.GONE);
                mAdultEmergency.setVisibility(View.INVISIBLE);
                mKidsEmergency.setVisibility(View.INVISIBLE);
                mSpecialityWrapper.setVisibility(View.INVISIBLE);
                mDoctorsWrapper.setVisibility(View.INVISIBLE);
            } else {
                mPatientLogIn.setVisibility(View.GONE);
                mDoctorLogIn.setVisibility(View.GONE);
                //  mAgreeMentLayout.setVisibility(View.GONE);
            }
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            mPatientLogIn.setVisibility(View.VISIBLE);
            mDoctorLogIn.setVisibility(View.VISIBLE);
            mAgreeMentLayout.setVisibility(View.VISIBLE);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    private void getDoctorList() {
        Utils.showProgress(this, "Please wait...");
        RequestQueue queue = Volley.newRequestQueue(this);
        HttpsTrustManager.allowAllSSL();
        String url = "";
        try {
            url = Constants.URL.GET_DOCTOR_LIST + "?authentication_token=" + mSharePreferences.getAuthenticationToken() + "&key=" + mSharePreferences.getKey() + "&specialize=" + URLEncoder.encode(mSpecialization, "utf-8") + "&page=1&per_page=1000";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d("UEL", url);
        StringRequest stringrequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.closeProgress();
                Log.d(TAG, response);
                getDoctorListResponse(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeProgress();
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        JSONObject obj = new JSONObject(res);
                        //   {"message":"Another account is already using this email address","status":"Failure"}
                        Log.e("Error", obj.toString());
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        stringrequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringrequest);
    }

    private void getDoctorListResponse(String response) {
        if (StringUtils.isNotEmpty(response)) {
            try {
                JSONObject mainObj = new JSONObject(response);
                String status = mainObj.getString("status");
                if (status.equalsIgnoreCase(Constants.SUCCESS)) {
                    JSONArray doctorList = mainObj.getJSONArray("doctors");
                    Log.e("DocList", "" + doctorList.length());
                    UpdateDoctorList(doctorList);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void UpdateDoctorList(JSONArray doctorList) {
        mDoctorspinnerValues.clear();
        for (int i = 0; i < doctorList.length(); i++) {
            try {
                mDoctorspinnerValues.add(new Doctor(doctorList.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (mDoctorspinnerValues.size() > 0) {
            mDoctorsWrapper.setVisibility(View.VISIBLE);
            DoctorListAdapter adapter = new DoctorListAdapter(this, R.layout.custom_spinner_view, mDoctorspinnerValues);
            doctorSpinner.setAdapter(adapter);
        } else {
            mDoctorspinnerValues.clear();
            DoctorListAdapter adapter = new DoctorListAdapter(this, R.layout.custom_spinner_view, mDoctorspinnerValues);
            doctorSpinner.setAdapter(adapter);
            mDoctorsWrapper.setVisibility(View.INVISIBLE);
        }
    }

    public void ShowDialog() {

        final Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_custom_logout_dialog);
        mDialog.setCancelable(true);
        mDialog.show();
        TextView message = (TextView) mDialog.findViewById(R.id.text);
        message.setText(R.string.call_charge_alert_msg);
        LinearLayout mainWrapper = (LinearLayout) mDialog.findViewById(R.id.mainWrapper);
        Button cancelBtn = (Button) mDialog.findViewById(R.id.cancel_logout_btn);
        Button okBtn = (Button) mDialog.findViewById(R.id.logout_yes_btn);
        okBtn.setText("Ok");
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (!mSharePreferences.isCardDetailsAvailable()) {
                    startActivity(new Intent(MainActivity.this, PaymentOptionActivity.class));
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

    }

    public void startIncomeCallListenerService(String login, String password, int startServiceVariant, String normalCall) {
        Intent tempIntent = new Intent(this, IncomeCallListenerService.class);
        PendingIntent pendingIntent = createPendingResult(Constants.LOGIN_TASK_CODE, tempIntent, 0);
        Intent intent = new Intent(this, IncomeCallListenerService.class);
        intent.putExtra(Constants.USER_LOGIN, login);
        intent.putExtra(Constants.USER_PASSWORD, password);
        intent.putExtra(Constants.START_SERVICE_VARIANT, startServiceVariant);
        intent.putExtra(Constants.PARAM_PINTENT, pendingIntent);
        intent.putExtra(Constants.BundleKeys.CALL_TYPE, normalCall);
        startService(intent);
    }

    public class MyAdapter extends ArrayAdapter<String> {
        private String[] spinnervalue;

        public MyAdapter(Context ctx, int txtViewResourceId, String[] objects) {
            super(ctx, txtViewResourceId, objects);
            this.spinnervalue = objects;
        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getCustomView(position, cnvtView, prnt);
        }

        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.custom_spinner_view, parent, false);
            TextView main_text = (TextView) mySpinner.findViewById(R.id.speciality_textView);
            main_text.setText(spinnervalue[position]);
            return mySpinner;
        }
    }
}
