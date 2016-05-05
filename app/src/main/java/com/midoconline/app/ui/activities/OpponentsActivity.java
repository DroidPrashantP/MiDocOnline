package com.midoconline.app.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.midoconline.app.api.UserApi;
import com.midoconline.app.beans.DataHolder;
import com.midoconline.app.ui.adapters.OpponentsAdapter;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.users.parsers.QBUserJsonParser;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OpponentsActivity extends BaseActivity implements View.OnClickListener, UserApi.UserEventListener {


    private static final String TAG = OpponentsActivity.class.getSimpleName();
    private static final int REQUEST_GET_CAMERA = 0;
    private static final int REQUEST_GET_GALALRY = 1;
    private OpponentsAdapter opponentsAdapter;
    private Button btnAudioCall;
    private Button btnVideoCall;
    private ProgressDialog progressDialog;
    private ListView opponentsListView;
    private ArrayList<QBUser> opponentsList = new ArrayList<QBUser>();
    private boolean isWifiConnected;
    private UserApi mUserApi;
    private SharePreferences mSharePreferences;
    private LinearLayout mMainLayout;
    private boolean isFromHistory = false;
    private String opponantHistoryEmail;
    private int mPageNumber = 1;
    private boolean hasMore = false;
    private int PAGE_SIZE = 100;
    private View footerView;
    private TextView mLoadingText;
    private ProgressBar mLoadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opponents);
        mSharePreferences = new SharePreferences(this);
        mUserApi = new UserApi(OpponentsActivity.this, this);
        opponentsListView = (ListView) findViewById(R.id.opponentsList);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isFromHistory = bundle.getBoolean("isFromHistory");
            opponantHistoryEmail = bundle.getString("opponentEmail");
        }

        setToolbar();
        initUI();
        initProgressDialog();
        if (isFromHistory){
            getUsersByEmail(opponantHistoryEmail);
        }else {
            fetchOpponentUser(mPageNumber);
        }

        footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_footer_view, null, false);
        mLoadingText = (TextView) footerView.findViewById(R.id.loadingText);
        mLoadingProgress = (ProgressBar) footerView.findViewById(R.id.loading);
      //  opponentsListView.addFooterView(footerView);

//        footerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mLoadingText.setVisibility(View.GONE);
//                mLoadingProgress.setVisibility(View.VISIBLE);
//                getNextUsers(mPageNumber);
//            }
//        });


    }

    private void fetchOpponentUser(int pageNumber) {

        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
        requestBuilder.setPerPage(100);
        requestBuilder.setPage(pageNumber);
        QBUsers.getUsers(requestBuilder, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                Log.e("UsersList", "" + qbUsers.size());
                ArrayList<QBUser> orderedUsers = reorderUsersByName(qbUsers);
                setOpponentsList(orderedUsers);
                ++mPageNumber;
                getNextUsers(mPageNumber);
//                prepareUserList(orderedUsers);
//                hideProgressDialog();
            }
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(List<String> strings) {
                for (String s : strings) {
                    Log.d(TAG, s);
                }
                OpponentsAdapter.i = 0;
                stopIncomeCallListenerService();
                clearUserDataFromPreferences();
                startListUsersActivity();
                finish();
            }
        });
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this) {
            @Override
            public void onBackPressed() {
                Toast.makeText(OpponentsActivity.this, getString(R.string.wait_until_loading_finish), Toast.LENGTH_SHORT).show();
            }
        };
        progressDialog.setMessage(getString(R.string.load_opponents));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void getNextUsers(final int pageNumber) {
        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
        requestBuilder.setPerPage(100);
        requestBuilder.setPage(pageNumber);

        // if (users == null) {
        QBUsers.getUsers(requestBuilder, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                Log.e("getNextUsers", "" + qbUsers.size());
//                mLoadingText.setVisibility(View.VISIBLE);
//                mLoadingProgress.setVisibility(View.GONE);
                ArrayList<QBUser> orderedUsers = reorderUsersByName(qbUsers);
                setOpponentsList(orderedUsers);
               // prepareUserList(orderedUsers);
                hasMore = qbUsers.size() < PAGE_SIZE ? true : false;
                if (hasMore){
                    hideProgressDialog();
                    prepareUserList(getOpponentsList());
                   // mLoadingText.setText("No More Users");
                }else {
                    ++mPageNumber;
                    getNextUsers(mPageNumber);
                }

            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(List<String> strings) {
                hideProgressDialog();
                for (String s : strings) {
                    Log.d(TAG, s);
                }
                OpponentsAdapter.i = 0;
                stopIncomeCallListenerService();
                clearUserDataFromPreferences();
                startListUsersActivity();
                finish();
            }
        });
    }

    public void setOpponentsList(ArrayList<QBUser> qbUsers) {
        this.opponentsList.addAll(qbUsers);
    }

    public ArrayList<QBUser> getOpponentsList() {
        return opponentsList;
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void sendRequestToAllUserData() {
        if (NetworkManager.isConnectedToInternet(this)) {
            Utils.showProgress(this);
            mUserApi.fetchAllUser(Constants.QuickBox.ALL_USER, Constants.ApiType.ALL_USER_API);
        } else {
            Toast.makeText(this, "No internet connected", Toast.LENGTH_SHORT).show();
        }
    }

    private void prepareUserList(List<QBUser> users) {


        QBUser currentUser = QBChatService.getInstance().getUser();
        if (users.contains(currentUser)) {
            users.remove(currentUser);
        }

        ArrayList<QBUser> newUserList = new ArrayList<QBUser>();
        for (int i = 0; i < users.size(); i++) {
            QBUser qbUser = users.get(i);
            if (isFromHistory) {
                if (opponantHistoryEmail.equalsIgnoreCase(qbUser.getEmail())) {
                    newUserList.add(qbUser);
                }
            } else {
                if (mSharePreferences.getEmergencyTag().equals(Constants.KID_EMERGENCY)) {
                    Log.e("Tag", "" + qbUser.getTags());
                    if (qbUser.getTags().toString().replace("[", "").replace("]", "").equals("kids")) {
                        newUserList.add(qbUser);
                    }
                }
                if (mSharePreferences.getEmergencyTag().equals(Constants.ADULT_EMERGENCY)) {
                    Log.e("Tag", "" + qbUser.getTags());
                    if (qbUser.getTags().toString().replace("[", "").replace("]", "").equals("adult")) {
                        newUserList.add(qbUser);
                    }
                }
                if (mSharePreferences.getEmergencyTag().equals(Constants.NORMAL_CALL)) {
                    if (mSharePreferences.getUserType().equals(Constants.BundleKeys.DOCTOR)) {
                        if (StringUtils.isNotEmpty(qbUser.getCustomData())) {
                            if (qbUser.getCustomData().equalsIgnoreCase(Constants.BundleKeys.PATIENT)) {
                                newUserList.add(qbUser);
                            }
                        }
                    } else {
                        if (StringUtils.isNotEmpty(qbUser.getCustomData())) {
                            if ((qbUser.getCustomData().replace("ó", "o").equalsIgnoreCase(mSharePreferences.getMedicalSpecialist().replace("ó", "o")) && qbUser.getLogin().equalsIgnoreCase(mSharePreferences.getSelectedDoctor())) || qbUser.getLogin().equalsIgnoreCase(mSharePreferences.getSelectedDoctor())) {
                                newUserList.add(qbUser);
                            }
                        }
                    }
                }
            }
        }
       // if (mPageNumber == 1) {
            setOpponantAdapter(newUserList);
//            ++mPageNumber;
//        } else {
//            addItemToList(newUserList);
//            ++mPageNumber;
//        }

        if (DataHolder.usersList.size() < 0) {
            btnAudioCall.setVisibility(View.GONE);
            btnVideoCall.setVisibility(View.GONE);
        } else {
            btnAudioCall.setVisibility(View.VISIBLE);
            btnVideoCall.setVisibility(View.VISIBLE);
        }
    }

    private void addItemToList(ArrayList<QBUser> newUserList) {

        //Loop through the new items and add them to the adapter
        if (newUserList != null && newUserList.size() > 0) {
            opponentsAdapter.addItem(newUserList);
        }
    }

    public void setOpponantAdapter(ArrayList<QBUser> newUserList) {
        //add the footer before adding the adapter, else the footer will not load!

        DataHolder.usersList = newUserList;
        opponentsAdapter = new OpponentsAdapter(this, newUserList);
        opponentsListView.setAdapter(opponentsAdapter);
    }

    private void initUI() {
        btnAudioCall = (Button) findViewById(R.id.btnVideoCall);
        btnAudioCall.setOnClickListener(this);

        btnVideoCall = (Button) findViewById(R.id.btnVideoCall);
        btnVideoCall.setOnClickListener(this);
        opponentsListView = (ListView) findViewById(R.id.opponentsList);
        mMainLayout = (LinearLayout) findViewById(R.id.mainContainer);
        mMainLayout.setBackgroundResource(R.drawable.bg);
    }

    @Override
    public void onClick(View v) {

        if (opponentsAdapter.getSelected().size() == 1) {
            QBRTCTypes.QBConferenceType qbConferenceType = null;
            //Init conference type
            switch (v.getId()) {
                case R.id.btnAudioCall:
                    qbConferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
                    setActionButtonsClickable(false);
                    break;

                case R.id.btnVideoCall:
                    qbConferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;
                    // setActionButtonsClickable(false);
                    break;
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestGetAccountsPermission();
            } else {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestGallaryPermission();
                } else {
                    HashMap<String, String> userInfo = new HashMap<>();
                    userInfo.put(Constants.BundleKeys.OPPONANT_NAME, mSharePreferences.getUserFullName());

                    Log.d(TAG, "QBChatService.getInstance().isLoggedIn() = " + String.valueOf(QBChatService.getInstance().isLoggedIn()));

                    if (!isWifiConnected) {
                        showToast(R.string.internet_not_connected);
                        setActionButtonsClickable(true);
                    } else if (!QBChatService.getInstance().isLoggedIn()) {
                        showToast(R.string.initializing_in_chat);
                        setActionButtonsClickable(true);
                    } else if (isWifiConnected && QBChatService.getInstance().isLoggedIn()) {
                        CallActivity.start(this, qbConferenceType, getOpponentsIds(opponentsAdapter.getSelected()),
                                userInfo, Constants.CALL_DIRECTION_TYPE.OUTGOING);
                        finish();  /// to close activity
                    }

                }
            }

        } else if (opponentsAdapter.getSelected().size() > 1) {
            Toast.makeText(this, getString(R.string.only_peer_to_peer_calls), Toast.LENGTH_LONG).show();
        } else if (opponentsAdapter.getSelected().size() < 1) {
            Toast.makeText(this, getString(R.string.choose_one_opponent), Toast.LENGTH_LONG).show();
        }
    }


    /**
     * request permission for google accounts
     */
    private void requestGetAccountsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.GET_ACCOUNTS)) {
            Snackbar.make(mMainLayout, R.string.camera_permission_required_msg,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.okay_text), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(OpponentsActivity.this,
                                    new String[]{Manifest.permission.GET_ACCOUNTS},
                                    REQUEST_GET_CAMERA);
                        }
                    })
                    .show();
        } else {

            // GET_ACCOUNTS permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_GET_CAMERA);
        }
    }

    /**
     * request permission for google accounts
     */
    private void requestGallaryPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mMainLayout, R.string.galary_permission_required_msg,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.okay_text), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(OpponentsActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_GET_GALALRY);
                        }
                    })
                    .show();
        } else {

            // GET_ACCOUNTS permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_GET_GALALRY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_GET_CAMERA) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for contacts permission.
            Log.i(TAG, "Received response for Get Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mMainLayout, R.string.camera_permission_granted_msg,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mMainLayout, R.string.camera_permission_not_granted_msg,
                        Snackbar.LENGTH_SHORT).show();

            }
            // END_INCLUDE(permission_result)

        } else if (requestCode == REQUEST_GET_GALALRY) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for contacts permission.
            Log.i(TAG, "Received response for Get gallery permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mMainLayout, "Gallery Permission granted,",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mMainLayout, "Gallery Permission not granted.",
                        Snackbar.LENGTH_SHORT).show();
            }
            // END_INCLUDE(permission_result)

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setActionButtonsClickable(boolean isClickable) {
        btnVideoCall.setClickable(isClickable);
        btnAudioCall.setClickable(isClickable);
    }

    public static ArrayList<Integer> getOpponentsIds(List<QBUser> opponents) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (QBUser user : opponents) {
            ids.add(user.getId());
        }
        return ids;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (OpponentsAdapter.i > 0) {
            opponentsListView.setSelection(OpponentsAdapter.i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setActionButtonsClickable(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null && progressDialog.isShowing()) {
            hideProgressDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opponent_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                showLogOutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private ArrayList<QBUser> reorderUsersByName(ArrayList<QBUser> qbUsers) {
        // Make clone collection to avoid modify input param qbUsers
        ArrayList<QBUser> resultList = new ArrayList<>(qbUsers.size());
        resultList.addAll(qbUsers);

        // Rearrange list by user IDs
        Collections.sort(resultList, new Comparator<QBUser>() {
            @Override
            public int compare(QBUser firstUsr, QBUser secondUsr) {
                if (firstUsr.getId().equals(secondUsr.getId())) {
                    return 0;
                } else if (firstUsr.getId() < secondUsr.getId()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        return resultList;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showLogOutDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle(R.string.log_out_dialog_title);
        quitDialog.setMessage(R.string.log_out_dialog_message);

        quitDialog.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OpponentsAdapter.i = 0;
                stopIncomeCallListenerService();
                clearUserDataFromPreferences();
                startListUsersActivity();
                finish();
            }
        });

        quitDialog.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        quitDialog.show();
    }

    @Override
    void processCurrentConnectionState(boolean isConncted) {
        if (!isConncted) {
            Log.d(TAG, "Internet is turned off");
            isWifiConnected = false;
        } else {
            Log.d(TAG, "Internet is turned on");
            isWifiConnected = true;
        }
    }

    private void initConnectionErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OpponentsActivity.this);
        builder.setMessage(R.string.NETWORK_ABSENT)
                .setCancelable(false)
                .setNegativeButton(R.string.ok_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                setActionButtonsClickable(false);
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onComplete(JSONObject jsonObject, String ApiType) {
        try {
            if (ApiType.equals(Constants.ApiType.ALL_USER_API)) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.closeProgress();
    }

    @Override
    public void onError(VolleyError volleyError, String ApiType) {

    }


    private void getUsersByEmail(String email) {

        QBUsers.getUserByEmail(email, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                Log.e("GetUser", user.toString());
                ArrayList<QBUser> orderedUsers = new ArrayList<QBUser>();
                orderedUsers.add(user);
                setOpponentsList(orderedUsers);
                prepareUserList(orderedUsers);
                hideProgressDialog();
            }

            @Override
            public void onError(List<String> errors) {
                Log.e("User Error", errors.toString());
                hideProgressDialog();
            }
        });
    }
}
