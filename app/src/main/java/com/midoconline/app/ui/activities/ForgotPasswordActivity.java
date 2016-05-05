package com.midoconline.app.ui.activities;

import android.app.Dialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.midoconline.app.R;
import com.midoconline.app.Util.Constants;
import com.midoconline.app.Util.NetworkManager;
import com.midoconline.app.Util.StringUtils;
import com.midoconline.app.Util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {
    private  static String TAG = ForgotPasswordActivity.class.getName();
    private EditText mEdtEmail;
    private Button mBtnSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setToolbar();
        mEdtEmail = (EditText) findViewById(R.id.edt_email);
        mBtnSend = (Button) findViewById(R.id.btn_send);

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isNotEmpty(mEdtEmail.getText().toString().trim())){
                    if (NetworkManager.isConnectedToInternet(ForgotPasswordActivity.this)){
                        ForgotPassword(mEdtEmail.getText().toString().trim());
                    }else {
                        Utils.ShowDialog(getString(R.string.no_internet_connection),ForgotPasswordActivity.this);
                    }
                }else {
                    Utils.ShowDialog(getString(R.string.email_error),ForgotPasswordActivity.this);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forgot_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void ForgotPassword(final String email) {
        Utils.showProgress(ForgotPasswordActivity.this);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringrequest = new StringRequest(Request.Method.POST, Constants.URL.FORGOTPASSWORD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.closeProgress();
                Log.d(TAG, response);
                if (StringUtils.isNotEmpty(response)){
                    try {
                        JSONObject obj = new JSONObject(response);
                        String status = obj.getString("status");
                        if (status.equalsIgnoreCase("Success")) {
                            String message = obj.getString("message");
                            ShowDialog(message);
                        }else if(status.equalsIgnoreCase("Failure")){
                            String message = obj.getString("message");
                            Utils.ShowDialog(message,ForgotPasswordActivity.this);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    Utils.ShowDialog("Please Check your Email",ForgotPasswordActivity.this);
                    Utils.closeProgress();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.ShowDialog("Please Check your Email",ForgotPasswordActivity.this);
                Utils.closeProgress();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",email);
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

    private void ShowDialog(String message) {
        final Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.show_dialog_layout);
        mDialog.setCancelable(true);
        final TextView title = (TextView) mDialog.findViewById(R.id.text_maintitle);
        final TextView Subtitle = (TextView) mDialog.findViewById(R.id.text_subtitle);
        title.setText("Password Sent");
        Subtitle.setText(message);

        mDialog.findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                finish();
            }
        });
        mDialog.show();
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
