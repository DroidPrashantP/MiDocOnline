package com.midoconline.app.ui.activities;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import com.midoconline.app.R;
import com.midoconline.app.Util.Constants;
import com.midoconline.app.Util.HttpsTrustManager;
import com.midoconline.app.Util.NetworkManager;
import com.midoconline.app.Util.SharePreferences;
import com.midoconline.app.Util.StringUtils;
import com.midoconline.app.Util.Utils;
import com.midoconline.app.beans.SignUpBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String SIMPLE_DATE_FORMAT = "dd-MM-yyyy";
    private static final String TAG = SignUpActivity.class.getName();
    private static int mYear, mMonth, mDate;
    public String mGender = "male";
    public String mBirthday;
    String[] mMedicalSpecialitySpinnerValues = {"Medical Specialist", "Alergólogo", "Cardiólogo", "Cirujano General", "Dentista", "Dermatólogo", "Endocrinólogo", "Gastroenterólogo", "Geriatra", "Ginecólogo", "Hematólogo", "Hepatólogo", "Medicina Interna", "Nefrólogo", "Neumólogo", "Neurólogo", "Nutriólogo", "Oftalmólogo", "Oncólogo", "Ortopedista", "Otorrinolaringólogo", "Pediatra", "Proctólogo", "Psicólogo", "Psiquiatra", "Reumatólogo", "Urgenciólogo", "Urólogo"};
    private EditText mEdtName, mEdtEmail, mEdtPassword, mEdtConfirmPassword, mEdtLicence, mEdtCity, mEdtMobile, mEdtCountry, mEdtBirthday, mEdtSurname;
    private Button mRequestApproval;
    private SignUpBean mSignUpBean;
    private LinearLayout mMainView;
    private SharePreferences mSharePreferences;
    private RelativeLayout mainContainer;
    private RelativeLayout maleWrapper, femaleWrapper;
    private TextView maleTextview, femaleTextview;
    private String mUserType;
    private TableRow mGenderLayout;
    private View mView;
    private RelativeLayout mSpecialityWrapper;
    private Spinner mSpecialitySpinner;
    private String SpecialityStr;
    private RelativeLayout mCountryWrapper;
    private Spinner mCountrySpinner;
    private String CountryStr;
    // private String[] mMedicalSpecialitySpinnerValues = {"MEDICAL SPECIALIST", "Alergologo", "Cardiologo", "Cirujano General", "Dermatologo", "Dentista", "Endocrinologo", "Gastroenterologo", "Geriatra", "Ginecólogo", "Hematologo", "Hepatologo", "Medicina Interna", "Nefrologo", "Neumologo", "Neurologo", "Oftalmologo", "Oncologo", "Ortopedista", "Otorrinolaringologo", "Pediatra", "Proctologo", "Psiquiatra", "Reumatologo", "Urologo", "Urgenciologo"};
    private String[] mCountryList = {"Country", "Afghanistan", "Algeria", "Australia", "Bahrain", "Bangladesh", "Bhutan", "Brazil", "Canada", "China", "Denmark", "Djibouti", "Egypt", "Finland", "France", "Germany", "Ghana ", "Greece", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Japan", "Jordon", "Nepal", "Netherlands", "New Zealand", "Oman", "Pakistan", "Peru", "Poland", "Qatar", "Russia", "Saudi Arabia", "South Africa", "South Korea", "Sri Lanka", "Tanzania", "Thailand", "Turkey", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "Vietnam", "Yeman", "Zambia", "Zimbabwe"};
    private CheckBox mAgreePaymentTerms;
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.set(year, monthOfYear, dayOfMonth);
            Calendar calendar2 = Calendar.getInstance(Locale.getDefault());
            if (calendar.getTimeInMillis() < calendar2.getTimeInMillis()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
                String strDate = dateFormat.format(calendar.getTime());
                mEdtBirthday.setText(strDate);
                mBirthday = strDate;
            } else {
                Toast.makeText(SignUpActivity.this, "Please select valid birthday", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = getLayoutInflater().inflate(R.layout.activity_sign_up, null);
        setContentView(mView);
        mSharePreferences = new SharePreferences(this);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUserType = bundle.getString(Constants.BundleKeys.USERTYPE);
        }
        setCurrentValuesOfDateAndTime();
        intiView();
        setToolbar();

    }

    public void setCurrentValuesOfDateAndTime() {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDate = calendar.get(Calendar.DATE);
    }

    private void intiView() {
        mAgreePaymentTerms = (CheckBox) findViewById(R.id.checkAgreePayterms);
        mainContainer = (RelativeLayout) findViewById(R.id.mainContainer);
        mMainView = (LinearLayout) findViewById(R.id.MainLayoutWrapper);
        mEdtName = (EditText) findViewById(R.id.edt_name);
        mEdtSurname = (EditText) findViewById(R.id.edt_surname);
        mEdtEmail = (EditText) findViewById(R.id.edt_email);
        mEdtPassword = (EditText) findViewById(R.id.edt_password);
        mEdtConfirmPassword = (EditText) findViewById(R.id.edt_Confirmpassword);
        mSpecialityWrapper = (RelativeLayout) findViewById(R.id.speciality_wrapper);
        mSpecialitySpinner = (Spinner) findViewById(R.id.MS_spinner);
        mEdtLicence = (EditText) findViewById(R.id.edt_speciality_licence);
        mEdtCity = (EditText) findViewById(R.id.edt_city);
        mEdtMobile = (EditText) findViewById(R.id.edt_mobile);
        mCountryWrapper = (RelativeLayout) findViewById(R.id.countryWrapper);
        mCountrySpinner = (Spinner) findViewById(R.id.countrySpinner);
        //  mEdtCountry = (EditText) findViewById(R.id.edt_country);
        mEdtBirthday = (EditText) findViewById(R.id.edt_dob);
        mRequestApproval = (Button) findViewById(R.id.btn_request_approval);
        mRequestApproval.setOnClickListener(this);
        mGenderLayout = (TableRow) findViewById(R.id.genderLayout);
        maleWrapper = (RelativeLayout) findViewById(R.id.maleWrapper);
        femaleWrapper = (RelativeLayout) findViewById(R.id.femaleWrapper);
        maleTextview = (TextView) findViewById(R.id.maleTextview);
        femaleTextview = (TextView) findViewById(R.id.femaleTextview);
        mainContainer.setBackgroundResource(R.drawable.bg);


        if (mUserType.equalsIgnoreCase(Constants.BundleKeys.PATIENT)) {
            mRequestApproval.setText(getString(R.string.sign_up_text));
            mSpecialityWrapper.setVisibility(View.GONE);
            mEdtLicence.setVisibility(View.GONE);
            mEdtCity.setVisibility(View.GONE);
            mCountryWrapper.setVisibility(View.GONE);
            mEdtSurname.setVisibility(View.VISIBLE);
            mEdtBirthday.setVisibility(View.VISIBLE);
            mGenderLayout.setVisibility(View.VISIBLE);
        } else {
            mRequestApproval.setText(getString(R.string.request_approval_text));
            mSpecialityWrapper.setVisibility(View.VISIBLE);
            mEdtCity.setVisibility(View.VISIBLE);
            mCountryWrapper.setVisibility(View.VISIBLE);
            mEdtSurname.setVisibility(View.GONE);
            mEdtBirthday.setVisibility(View.GONE);
            mGenderLayout.setVisibility(View.GONE);
            mEdtLicence.setVisibility(View.VISIBLE);
        }

        maleWrapper.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                try {
                    mGender = "male";
                    mGenderLayout.setBackgroundResource(R.drawable.oval_bg);
                    maleWrapper.setBackgroundColor(getResources().getColor(R.color.royal_blue));
                    femaleWrapper.setBackground(null);
                    maleTextview.setTextColor(getResources().getColor(R.color.white));
                    femaleTextview.setTextColor(getResources().getColor(R.color.blue));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        });
        femaleWrapper.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                mGenderLayout.setBackgroundResource(R.drawable.oval_bg);
                maleWrapper.setBackground(null);
                femaleWrapper.setBackgroundColor(getResources().getColor(R.color.royal_blue));
                maleTextview.setTextColor(getResources().getColor(R.color.blue));
                femaleTextview.setTextColor(getResources().getColor(R.color.white));
                mGender = "female";
            }
        });

        mEdtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });

        mSpecialitySpinner.setAdapter(new MyAdapter(this, R.layout.custom_spinner_view, mMedicalSpecialitySpinnerValues));
        mSpecialitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    SpecialityStr = mMedicalSpecialitySpinnerValues[position];
                } else {
                    SpecialityStr = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCountrySpinner.setAdapter(new MyAdapter(this, R.layout.custom_spinner_view, mCountryList));
        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    CountryStr = mCountryList[position];
                } else {
                    CountryStr = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == mRequestApproval) {
            validation();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, dateSetListener, mYear, mMonth, mDate);
    }

    private void validation() {
        if (mUserType.equalsIgnoreCase(Constants.BundleKeys.DOCTOR)) {
            DoctorValidation();
        } else {
            PatientValidation();
        }

    }

    public void PatientValidation() {
        if (StringUtils.isNotEmpty(mEdtName.getText().toString()) && StringUtils.isNotEmpty(mEdtSurname.getText().toString()) && StringUtils.isNotEmpty(mEdtEmail.getText().toString()) && StringUtils.isNotEmpty(mEdtPassword.getText().toString()) && StringUtils.isNotEmpty(mEdtBirthday.getText().toString()) && StringUtils.isNotEmpty(mGender) && StringUtils.isNotEmpty(mEdtMobile.getText().toString())) {
            if (mEdtPassword.getText().toString().equals(mEdtConfirmPassword.getText().toString())) {
                if (mEdtPassword.getText().toString().length() >= 8) {
                    SendRequest();
                } else {
                    Utils.ShowDialog(getString(R.string.min_password_error), this);
                }
            } else {
                Utils.ShowDialog(getString(R.string.confirm_password_error), this);
            }
        } else {

            if (!StringUtils.isNotEmpty(mEdtName.getText().toString())) {
                Utils.ShowDialog(getString(R.string.name_error), this);
            } else {
                if (!StringUtils.isNotEmpty(mEdtSurname.getText().toString())) {
                    Utils.ShowDialog(getString(R.string.surname_error), this);
                } else {
                    if (!StringUtils.isNotEmpty(mEdtEmail.getText().toString())) {
                        Utils.ShowDialog(getString(R.string.email_error), this);
                    } else {
                        if (!isValidEmail(mEdtEmail.getText().toString())) {
                            Utils.ShowDialog(getString(R.string.email_pattern_error), this);
                        } else {
                            if (!StringUtils.isNotEmpty(mEdtPassword.getText().toString())) {
                                Utils.ShowDialog(getString(R.string.password_error), this);
                            } else {
                                if (!StringUtils.isNotEmpty(mEdtBirthday.getText().toString())) {
                                    Utils.ShowDialog(getString(R.string.birthday_error), this);
                                } else {
                                    if (!StringUtils.isNotEmpty(mEdtMobile.getText().toString())) {
                                        Utils.ShowDialog(getString(R.string.mobile_error), this);
                                    } else {
                                        if (!mAgreePaymentTerms.isChecked()) {
                                            Utils.ShowDialog(getString(R.string.accept_terms_text), this);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    public void DoctorValidation() {
        if (StringUtils.isNotEmpty(mEdtName.getText().toString()) && StringUtils.isNotEmpty(mEdtEmail.getText().toString()) && StringUtils.isNotEmpty(mEdtPassword.getText().toString()) && StringUtils.isNotEmpty(SpecialityStr) && StringUtils.isNotEmpty(mEdtCity.getText().toString()) && StringUtils.isNotEmpty(CountryStr) && StringUtils.isNotEmpty(mEdtMobile.getText().toString())) {
            if (mEdtPassword.getText().toString().equals(mEdtConfirmPassword.getText().toString())) {
                if (mEdtPassword.getText().toString().length() >= 8) {
                    SendRequest();
                } else {
                    Utils.ShowDialog(getString(R.string.min_password_error), this);
                }
            } else {
                Utils.ShowDialog(getString(R.string.confirm_password_error), SignUpActivity.this);
            }

        } else {
            if (!StringUtils.isNotEmpty(mEdtName.getText().toString())) {
                Utils.ShowDialog(getString(R.string.name_error), this);
            } else {
                if (!StringUtils.isNotEmpty(mEdtEmail.getText().toString())) {
                    Utils.ShowDialog(getString(R.string.email_error), this);
                } else {
                    if (!isValidEmail(mEdtEmail.getText().toString())) {
                        Utils.ShowDialog(getString(R.string.email_pattern_error), this);
                    } else {
                        if (!StringUtils.isNotEmpty(mEdtPassword.getText().toString())) {
                            Utils.ShowDialog(getString(R.string.password_error), this);
                        } else {
                            if (!StringUtils.isNotEmpty(SpecialityStr)) {
                                Utils.ShowDialog(getString(R.string.speciality_licence_error), this);
                            } else {
                                if (!StringUtils.isNotEmpty(mEdtCity.getText().toString())) {
                                    Utils.ShowDialog(getString(R.string.city_error), this);
                                } else {
                                    if (!StringUtils.isNotEmpty(mEdtMobile.getText().toString()) && mEdtMobile.getText().toString().length() > 10) {
                                        Utils.ShowDialog(getString(R.string.mobile_error), this);
                                    } else {
                                        if (!StringUtils.isNotEmpty(CountryStr)) {
                                            Utils.ShowDialog(getString(R.string.country_error), this);
                                        } else {
                                            if (!StringUtils.isNotEmpty(mEdtMobile.getText().toString())) {
                                                Utils.ShowDialog(getString(R.string.mobile_error), this);
                                            } else {
                                                if (!mAgreePaymentTerms.isChecked()) {
                                                    Utils.ShowDialog(getString(R.string.accept_terms_text), this);
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void SendRequest() {
        mSignUpBean = new SignUpBean(mEdtName.getText().toString(), mEdtSurname.getText().toString(), mEdtCity.getText().toString(), mEdtEmail.getText().toString(), CountryStr, mEdtMobile.getText().toString(), mEdtPassword.getText().toString(), SpecialityStr, mGender, mBirthday, mEdtLicence.getText().toString());
        if (NetworkManager.isConnectedToInternet(this)) {

            if ((isValidEmail(mEdtEmail.getText().toString())) && mEdtMobile.getText().toString().length() == 10) {
                Utils.showProgress(this);
                ExecutePostRequest();
            } else {
                if (!isValidEmail(mEdtEmail.getText().toString())) {
                    Utils.ShowDialog(getString(R.string.email_pattern_error), this);
                }
                if (mEdtMobile.getText().toString().length() > 10 || mEdtMobile.getText().toString().length() < 10) {
                    Utils.ShowDialog(getString(R.string.mobile_pattern_error), this);
                }
            }
        } else {
            Utils.ShowSnackBar(mMainView, getString(R.string.no_internet_connection));
        }
    }

    public void ExecutePostRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = mUserType.equals(Constants.BundleKeys.DOCTOR) ? Constants.URL.DOCTOR_SIGNUP_URL : Constants.URL.PATIENT_SIGNUP_URL;
        HttpsTrustManager.allowAllSSL();
        StringRequest stringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        JSONObject obj = new JSONObject(res);
                        if (obj.has("message")) {
                            String message = obj.getString("message");
                            Utils.ShowSnackBar(mView, message);
                        }
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", mSignUpBean.getName());
                params.put("email", mSignUpBean.getEmail());
                params.put("password", mSignUpBean.getPassword());
                params.put("mobile_no", mSignUpBean.getMobilenumber());
                params.put("device_token", Utils.getDeviceId(SignUpActivity.this));
                params.put("device_type", Constants.DEVICE_TYPE);

                if (mUserType.equalsIgnoreCase(Constants.BundleKeys.DOCTOR)) {
                    params.put("specialize", SpecialityStr);
                    params.put("city", mSignUpBean.getcity());
                    params.put("country", CountryStr);
                    params.put("licence", mSignUpBean.getLincence());
                    params.put("terms_and_condition", "" + true);
                } else {
                    params.put("dob", mSignUpBean.getBirthday());
                    params.put("surname", mSignUpBean.getSurname());
                    params.put("gender", mSignUpBean.getGender());
                    params.put("terms_and_condition", "" + true);
                }

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
        if (StringUtils.isNotEmpty(response)) {
            try {
                JSONObject mainobj = new JSONObject(response);
                String status = mainobj.getString("status");
                if (status.equalsIgnoreCase(Constants.SUCCESS)) {
                    JSONObject obj = mainobj.getJSONObject("user");
                    String user_type = obj.getString("type");
                    if (user_type.equalsIgnoreCase(Constants.BundleKeys.DOCTOR)) {
                        clearData();
                        ShowDialog();
                    } else {
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
                        mSharePreferences.setQBID(obj.getString("qb_user_id"));
                        mSharePreferences.setQBQBEmail(obj.getString("qb_login"));
                        mSharePreferences.setQBQBPassword(obj.getString("qb_password"));
                        mSharePreferences.setQBLoginName(obj.getString("qb_name"));

//                        QBUser user = new QBUser();
//                        user.setLogin(obj.getString("username"));
//                        user.setPassword("android1234");
//                        user.setEmail(obj.getString("email"));
//                        QBApi.SignupWithQuicBox(SignUpActivity.this,mSharePreferences,user);

                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else if (status.equalsIgnoreCase("Failure")) {
                    String message = mainobj.getString("message");
                    Utils.ShowDialog(message, SignUpActivity.this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

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

    public void ShowDialog() {
        final Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_custom_logout_dialog);
        mDialog.setCancelable(true);
        mDialog.show();
        TextView message = (TextView) mDialog.findViewById(R.id.text);
        message.setText("Thank you,\n You will be reached by email");
        LinearLayout mainWrapper = (LinearLayout) mDialog.findViewById(R.id.mainWrapper);
        mDialog.findViewById(R.id.cancel_logout_btn).setVisibility(View.GONE);
        Button okBtn = (Button) mDialog.findViewById(R.id.logout_yes_btn);
        okBtn.setText("Ok");
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void clearData() {
        mSharePreferences.ClearSharepreference();
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
