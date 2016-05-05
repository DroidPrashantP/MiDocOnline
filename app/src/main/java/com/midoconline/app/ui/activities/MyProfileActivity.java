package com.midoconline.app.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.midoconline.app.R;
import com.midoconline.app.Util.Constants;
import com.midoconline.app.Util.NetworkManager;
import com.midoconline.app.Util.SharePreferences;
import com.midoconline.app.Util.StringUtils;
import com.midoconline.app.Util.Utils;
import com.midoconline.app.Util.widget.CircularNetworkImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MyProfileActivity extends AppCompatActivity {
    private static final String TAG = MyProfileActivity.class.getName();
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private SharePreferences mSharePreferences;
    private CircularNetworkImageView mUserImage;
    private EditText mEdtEmail, mEdtSpeciality, mEdtMobile;
    private TextView mTxtName, mTxtSpecialby;
    private RelativeLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        mTxtName = (TextView) findViewById(R.id.txt_name);
        mEdtEmail = (EditText) findViewById(R.id.edt_email);
        mEdtSpeciality = (EditText) findViewById(R.id.edt_speciality);
        mEdtMobile = (EditText) findViewById(R.id.edt_phone);
        mTxtSpecialby = (TextView) findViewById(R.id.txt_speciality);
        setToolbar();
        mSharePreferences = new SharePreferences(this);
        mUserImage = (CircularNetworkImageView) findViewById(R.id.user_image);
        mUserImage.setImageResource(R.drawable.profilepicture);
        mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        if (NetworkManager.isConnectedToInternet(this)) {
            // ExecutePostRequest();
        } else {
            Utils.ShowDialog("Please check your net connection!", this);
        }

        mMainLayout = (RelativeLayout) findViewById(R.id.mainContainer);
        mMainLayout.setBackgroundResource(R.drawable.bg);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setView();
    }

    private void setView() {
        if (mSharePreferences.getUserType().equalsIgnoreCase(Constants.BundleKeys.DOCTOR)) {
            mTxtName.setText(mSharePreferences.getUserName());
            mEdtEmail.setText(mSharePreferences.getUserEmail());
            mEdtSpeciality.setText(mSharePreferences.getUserSpecialist());
            mEdtMobile.setText(mSharePreferences.getMobile());
        } else {
            mTxtName.setText(mSharePreferences.getUserName());
            mEdtEmail.setText(mSharePreferences.getUserEmail());
            mEdtMobile.setText(mSharePreferences.getMobile());
            mEdtSpeciality.setVisibility(View.GONE);
            mTxtSpecialby.setVisibility(View.GONE);

        }

        if (mSharePreferences.getUserThumbnail() != null) {
            Picasso.with(this)
                    .load(new File(mSharePreferences.getUserThumbnail()))
                    .resize(300, 300)
                    .placeholder(R.drawable.profilepicture).into(mUserImage);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_profile, menu);

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
            Intent i = new Intent(MyProfileActivity.this, EditProfileActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    public void setToolbar() {
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

    public void ExecutePostRequest() {
        String url = Constants.URL.PATIENT_PROFILE_URL + "key=" + mSharePreferences.getKey() + "&authentication=" + mSharePreferences.getAuthenticationToken() + "&id=" + mSharePreferences.getUserId();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringrequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
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
//                params.put("key",mSharePreferences.getKey());
//                params.put("authentication",mSharePreferences.getAuthenticationToken());
//                params.put("id", mSharePreferences.getUserId());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        queue.add(stringrequest);
    }

    private void ParseReponse(String response) {
        Utils.closeProgress();
        Log.d(TAG, response);
        if (StringUtils.isNotEmpty(response)) {
            try {
                JSONObject obj = new JSONObject(response);
                String status = obj.getString("status");
                if (status.equalsIgnoreCase("Success")) {
                    mSharePreferences.setUserName(obj.getString("full_name"));
                    mSharePreferences.setUserSpecialist(obj.getString("specility"));
                    mSharePreferences.setUserEmail(obj.getString("email"));
                    mSharePreferences.setMobile(obj.getString("phone_no"));
                    mSharePreferences.setUserThumbnail(obj.getString("user_image_url"));
                    setView();

                } else if (status.equalsIgnoreCase("Failure")) {
                    Utils.closeProgress();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Utils.closeProgress();
        }
    }


    /**
     * choose profile image
     */
    private void chooseImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        } else {
            startActivityForResult(Utils.getPickImageChooserIntent(this), 200);
        }

    }

    /**
     * request permission for image upload
     */
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mMainLayout, R.string.storage_permission_required_msg,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.okay_text), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MyProfileActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {

            // WRITE_EXTERNAL_STORAGE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.showSnackBar(mMainLayout, getString(R.string.storage_permission_granted_msg));
            } else {
                Utils.showSnackBar(mMainLayout, getString(R.string.storage_permission_not_granted_msg));
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = Utils.getPickImageResultUri(data, this);
            String userImagePath = Utils.getRealPathFromURI(imageUri, this);
            if (userImagePath != null) {
                Log.d(TAG, "userImagePath: " + userImagePath);
                Picasso.with(this)
                        .load(new File(userImagePath))
                        .resize(300, 300)
                        .placeholder(R.drawable.app_logo).into(mUserImage);
            }
        }
    }
}
