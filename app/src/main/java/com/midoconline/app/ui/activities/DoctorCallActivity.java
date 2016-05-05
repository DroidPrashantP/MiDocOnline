package com.midoconline.app.ui.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.midoconline.app.beans.History;
import com.midoconline.app.ui.adapters.OpponentsAdapter;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorCallActivity extends BaseActivity implements View.OnClickListener, UserApi.UserEventListener{

    private static final String TAG = OpponentsActivity.class.getSimpleName();
    private OpponentsAdapter opponentsAdapter;
    private Button btnAudioCall;
    private Button btnVideoCall;
    private ProgressDialog progressDialog;
    private ListView opponentsListView;
    private ArrayList<QBUser> opponentsList;
    private boolean isWifiConnected;
    private UserApi mUserApi;
    private SharePreferences mSharePreferences;
    private LinearLayout mMainLayout;
    private TextView emptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_call);
        mSharePreferences = new SharePreferences(this);
        mUserApi = new UserApi(DoctorCallActivity.this, this);
        opponentsListView = (ListView) findViewById(R.id.opponentsList);

        setToolbar();
        initUI();
        initProgressDialog();
        //  initOpponentListAdapter();
        getDoctorHistroy();
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this) {
            @Override
            public void onBackPressed() {
                Toast.makeText(DoctorCallActivity.this, getString(R.string.wait_until_loading_finish), Toast.LENGTH_SHORT).show();
            }
        };
        progressDialog.setMessage(getString(R.string.load_opponents));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void getDoctorHistroy() {
        Utils.showProgress(this);
        RequestQueue queue = Volley.newRequestQueue(this);
        HttpsTrustManager.allowAllSSL();
        String url = mSharePreferences.getUserType().equalsIgnoreCase(Constants.BundleKeys.DOCTOR) ? Constants.URL.DOCOTR_HISTORY : Constants.URL.PATIENT_HISTORY;
        url = url + "?authentication_token=" + mSharePreferences.getAuthenticationToken() + "&key=" + mSharePreferences.getKey();
        StringRequest stringrequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.closeProgress();
                Log.d("Response", response);
                parseHistorResponse(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeProgress();
                Log.e("Error", error.toString());
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

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

    private void parseHistorResponse(String response) {
        if (StringUtils.isNotEmpty(response)) {
            try {
                String status = new JSONObject(response).getString("status");
                JSONArray historyArray = new JSONObject(response).getJSONArray("history");
                if (historyArray.length() > 0) {
                    for (int i = 0; i < historyArray.length(); i++) {
                        JSONObject object = historyArray.getJSONObject(i);
                        History history = new History(object.getString("receiver_id"), object.getString("caller_id"), object.getString("duration"), object.getString("chat_type"), object.getString("call_status"), object.getString("amount"), object.getString("currency"), object.getString("qb_caller_id"), object.getString("qb_receiver_id"), object.getString("doctor_id"), object.getString("patient_id"), object.getString("doctor_email"), object.getString("patient_email"), object.getString("doctor_full_name"), object.getString("patient_full_name"), object.getString("doctor_specialization"), object.getString("call_date"));
                        //addAmount(object.getString("amount"));
                    }
                } else {
                    emptyTextView.setVisibility(View.VISIBLE);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initOpponentListAdapter() {
        final ListView opponentsList = (ListView) findViewById(R.id.opponentsList);
        List<QBUser> users = getOpponentsList();
        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
        requestBuilder.setPerPage(100);

        if (users == null) {
            QBUsers.getUsers(requestBuilder, new QBEntityCallback<ArrayList<QBUser>>() {
                @Override
                public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                    Log.e("Respose", qbUsers.toString());
                    ArrayList<QBUser> orderedUsers = reorderUsersByName(qbUsers);
                    setOpponentsList(orderedUsers);
                    prepareUserList(opponentsList, orderedUsers);
                    hideProgressDialog();
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

        } else {
            ArrayList<QBUser> userList = getOpponentsList();
            prepareUserList(opponentsListView, userList);
            hideProgressDialog();
        }
    }

    public void setOpponentsList(ArrayList<QBUser> qbUsers) {
        this.opponentsList = qbUsers;
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

    private void prepareUserList(ListView opponentsList, List<QBUser> users) {
        QBUser currentUser = QBChatService.getInstance().getUser();
        if (users.contains(currentUser)) {
            users.remove(currentUser);
        }

        Log.e("DoctorList", users.toString());
        ArrayList<QBUser> newUserList = new ArrayList<QBUser>();
        for (int i = 0; i< users.size(); i++){
            QBUser qbUser = users.get(i);
            if (mSharePreferences.getEmergencyTag().equals(Constants.KID_EMERGENCY)) {
                Log.e("Tag",""+qbUser.getTags());
                if (qbUser.getTags().toString().replace("[", "").replace("]", "").equals("kids")){
                    newUserList.add(qbUser);
                }
            }
            if (mSharePreferences.getEmergencyTag().equals(Constants.ADULT_EMERGENCY)) {
                Log.e("Tag", "" + qbUser.getTags());
                if (qbUser.getTags().toString().replace("[", "").replace("]", "").equals("adult")){
                    newUserList.add(qbUser);
                }
            }
            if (mSharePreferences.getEmergencyTag().equals(Constants.NORMAL_CALL)) {
                Log.e("Tag", "" + qbUser.getTags());
                if (mSharePreferences.getUserType().equals(Constants.BundleKeys.DOCTOR)){
                    Log.e("CustomData",""+qbUser.getCustomData());
                    if (StringUtils.isNotEmpty(qbUser.getCustomData())) {
                        if (qbUser.getCustomData().equalsIgnoreCase(Constants.BundleKeys.PATIENT)) {
                            newUserList.add(qbUser);
                        }
                    }
                }else {
                    if (StringUtils.isNotEmpty(qbUser.getCustomData())) {
                        if (qbUser.getCustomData().equalsIgnoreCase(mSharePreferences.getMedicalSpecialist())) {
                            newUserList.add(qbUser);
                        }else {
                            Log.e("Name",""+qbUser.getLogin());
                        }
                    }
                }

            }
        }
        DataHolder.usersList = newUserList;
        // Prepare users list for simple adapter.
        opponentsAdapter = new OpponentsAdapter(this, newUserList);
        opponentsListView.setAdapter(opponentsAdapter);
    }

    private void initUI() {
        btnAudioCall = (Button) findViewById(R.id.btnVideoCall);
        btnAudioCall.setOnClickListener(this);

        btnVideoCall = (Button) findViewById(R.id.btnVideoCall);
        btnVideoCall.setOnClickListener(this);
        opponentsListView = (ListView) findViewById(R.id.opponentsList);
        mMainLayout = (LinearLayout)findViewById(R.id.mainContainer);
        mMainLayout.setBackgroundResource(R.drawable.bg);

        emptyTextView = (TextView) findViewById(R.id.userName);
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

            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("any_custom_data", "some data");
            userInfo.put("my_avatar_url", "avatar_reference");

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

        } else if (opponentsAdapter.getSelected().size() > 1) {
            Toast.makeText(this, getString(R.string.only_peer_to_peer_calls), Toast.LENGTH_LONG).show();
        } else if (opponentsAdapter.getSelected().size() < 1) {
            Toast.makeText(this, getString(R.string.choose_one_opponent), Toast.LENGTH_LONG).show();
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

}
