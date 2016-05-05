package com.midoconline.app.ui.activities;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.midoconline.app.Util.SessionManager;
import com.midoconline.app.Util.SharePreferences;
import com.midoconline.app.Util.StringUtils;
import com.midoconline.app.ui.fragments.AudioConversationFragment;
import com.midoconline.app.ui.fragments.BaseConversationFragment;
import com.midoconline.app.ui.fragments.IncomeCallFragment;
import com.midoconline.app.ui.fragments.VideoConversationFragment;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.quickblox.videochat.webrtc.QBRTCException;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import org.jivesoftware.smack.SmackException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//import com.quickblox.videochat.webrtc.QBRTCException;

/**
 * Created by tereha on 16.02.15.
 */
public class CallActivity extends BaseActivity {


    public static final String INCOME_CALL_FRAGMENT = "income_call_fragment";
    public static final String CONVERSATION_CALL_FRAGMENT = "conversation_call_fragment";
    private static final String TAG = CallActivity.class.getSimpleName();
    protected QBUser loginedUser;
    private Runnable showIncomingCallWindowTask;
    private Handler showIncomingCallWindowTaskHandler;
    private boolean closeByWifiStateAllow = true;
    private String hangUpReason;
    private boolean isInCommingCall;
    private Constants.CALL_DIRECTION_TYPE call_direction_type;
    private QBRTCTypes.QBConferenceType call_type;
    private List<Integer> opponentsList;
    private MediaPlayer ringtone;
    private BroadcastReceiver callBroadcastReceiver;
    private SharePreferences mSharePreferences;
    private boolean UpdateHistoryTwo = false;
    private boolean UpdateHistoryThree = false;
    private boolean isChargeApplied = false;
    private boolean isCallConnected = false;
    private HashMap<String, String> userData;

    public static void start(Context context, QBRTCTypes.QBConferenceType qbConferenceType,
                             List<Integer> opponentsIds, Map<String, String> userInfo,
                             Constants.CALL_DIRECTION_TYPE callDirectionType) {
        Intent intent = new Intent(context, CallActivity.class);
        intent.putExtra(Constants.CALL_DIRECTION_TYPE_EXTRAS, callDirectionType);
        intent.putExtra(Constants.CALL_TYPE_EXTRAS, qbConferenceType);
        intent.putExtra(Constants.USER_INFO_EXTRAS, (Serializable) userInfo);
        intent.putExtra(Constants.OPPONENTS_LIST_EXTRAS, (Serializable) opponentsIds);
        if (callDirectionType == Constants.CALL_DIRECTION_TYPE.INCOMING) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_activity);
        mSharePreferences = new SharePreferences(this);
        registerCallbackListener();

        if (getIntent().getExtras() != null) {
            parseIntentExtras(getIntent().getExtras());
        }
        if (call_direction_type == Constants.CALL_DIRECTION_TYPE.INCOMING) {
            isInCommingCall = true;
            addIncomeCallFragment(opponentsList, SessionManager.getCurrentSession());

        } else if (call_direction_type == Constants.CALL_DIRECTION_TYPE.OUTGOING) {
            isInCommingCall = false;
            addConversationFragment(opponentsList, call_type, call_direction_type);
        }

        if (QBChatService.isInitialized()) {
            if (QBChatService.getInstance().isLoggedIn()) {
                loginedUser = QBChatService.getInstance().getUser();
            }
        }
    }

    private void parseIntentExtras(Bundle extras) {
        call_direction_type = (Constants.CALL_DIRECTION_TYPE) extras.getSerializable(
                Constants.CALL_DIRECTION_TYPE_EXTRAS);
        call_type = (QBRTCTypes.QBConferenceType) extras.getSerializable(Constants.CALL_TYPE_EXTRAS);
        opponentsList = (List<Integer>) extras.getSerializable(Constants.OPPONENTS_LIST_EXTRAS);
        Log.e("UserData", "" + (HashMap<String, String>) extras.getSerializable(Constants.USER_INFO_EXTRAS));
        userData = (HashMap<String, String>) extras.getSerializable(Constants.USER_INFO_EXTRAS);
    }

    @Override
    void processCurrentConnectionState(boolean isConnected) {
        if (!isConnected) {
            Log.d(TAG, "Internet is turned off");
            if (closeByWifiStateAllow) {
                if (SessionManager.getCurrentSession() != null) {
                    Log.d(TAG, "currentSession NOT null");
                    // Close session safely
                    disableConversationFragmentButtons();
                    stopOutBeep();

                    hangUpCurrentSession();

                    hangUpReason = Constants.WIFI_DISABLED;
                } else {
                    Log.d(TAG, "Call finish() on activity");
                    finish();
                }
            }
        } else {
            Log.d(TAG, "Internet is turned on");
        }
    }

    private void disableConversationFragmentButtons() {
        BaseConversationFragment fragment = (BaseConversationFragment) getFragmentManager().findFragmentByTag(CONVERSATION_CALL_FRAGMENT);
        if (fragment != null) {
            fragment.actionButtonsEnabled(false);
        }
    }

    private void initIncommingCallTask() {
        showIncomingCallWindowTaskHandler = new Handler(Looper.myLooper());
        showIncomingCallWindowTask = new Runnable() {
            @Override
            public void run() {
                IncomeCallFragment incomeCallFragment = (IncomeCallFragment) getFragmentManager().findFragmentByTag(INCOME_CALL_FRAGMENT);
                if (incomeCallFragment == null) {
                    BaseConversationFragment conversationFragment = (BaseConversationFragment) getFragmentManager().findFragmentByTag(CONVERSATION_CALL_FRAGMENT);
                    if (conversationFragment != null) {
                        disableConversationFragmentButtons();
                        stopOutBeep();
                        hangUpCurrentSession();
                    }
                } else {
                    rejectCurrentSession();
                    finish();
                }
            }
        };
    }

    public void rejectCurrentSession() {
        if (SessionManager.getCurrentSession() != null) {
            Map<String, String> params = new HashMap<>();
            params.put("reason", "manual");
            SessionManager.getCurrentSession().rejectCall(params);
        }
    }

    public void hangUpCurrentSession() {
        if (SessionManager.getCurrentSession() != null) {
            SessionManager.getCurrentSession().hangUp(new HashMap<String, String>());
        }

    }

    private void startIncomeCallTimer() {
        showIncomingCallWindowTaskHandler.postAtTime(showIncomingCallWindowTask, SystemClock.uptimeMillis() + TimeUnit.SECONDS.toMillis(QBRTCConfig.getAnswerTimeInterval()));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void stopIncomeCallTimer() {
        Log.d(TAG, "stopIncomeCallTimer");
        showIncomingCallWindowTaskHandler.removeCallbacks(showIncomingCallWindowTask);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopOutBeep();

    }

    private void forbidenCloseByWifiState() {
        closeByWifiStateAllow = false;
    }


    // ---------------Chat callback methods implementation  ----------------------//

    public void onReceiveNewSession() {

        Log.e("ReceiveSession", "Receive Call");
    }

    public void onUserNotAnswer(Integer userID) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(R.string.noAnswer);
                finish();
            }
        });
    }

    public void onStartConnectToUser(Integer userID) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(R.string.checking);
                stopOutBeep();
            }
        });
    }

    public void onCallRejectByUser(Integer userID, Map<String, String> userInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(R.string.rejected);
                stopOutBeep();
                finish();
            }
        });
    }

    public void onConnectionClosedForUser(Integer userID) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(R.string.closed);
                // Close app after session close of network was disabled
                Log.d(TAG, "onConnectionClosedForUser()");
                if (hangUpReason != null && hangUpReason.equals(Constants.WIFI_DISABLED)) {
                    Intent returnIntent = new Intent();
                    setResult(Constants.CALL_ACTIVITY_CLOSE_WIFI_DISABLED, returnIntent);
                    finish();
                }
            }
        });
    }

    public void onConnectedToUser(final Integer userID) {
        forbidenCloseByWifiState();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isInCommingCall) {
                    stopIncomeCallTimer();
                }
                showToast(R.string.connected);
                startTimer();
                isCallConnected = true;
                BaseConversationFragment fragment = (BaseConversationFragment) getFragmentManager().findFragmentByTag(CONVERSATION_CALL_FRAGMENT);
                if (fragment != null) {
                    fragment.actionButtonsEnabled(true);
                }

                try {
                    // create a message
                    QBChatMessage chatMessage = new QBChatMessage();
                    chatMessage.setProperty("param1", "value1");
                    chatMessage.setProperty("param2", "value2");
                    chatMessage.setBody("system body");

                    chatMessage.setRecipientId(QBChatService.getInstance().getUser().getId());

                    QBChatService.getInstance().getSystemMessagesManager().sendSystemMessage(chatMessage);
                    updateHistoryAfterVideoCall();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (IllegalStateException ee) {
                    ee.printStackTrace();
                }
                //
                //
            }
        });
    }

    private void startTimer() {
        VideoConversationFragment fragment = (VideoConversationFragment) getFragmentManager().findFragmentByTag(CONVERSATION_CALL_FRAGMENT);
        if (fragment != null) {
            fragment.startTimer();
        }

    }

    private void stopTimer() {
        VideoConversationFragment fragment = (VideoConversationFragment) getFragmentManager().findFragmentByTag(CONVERSATION_CALL_FRAGMENT);
        if (fragment != null) {
            fragment.stopTimer();
        }

    }

    public void onDisconnectedTimeoutFromUser(Integer userID) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(R.string.time_out);
                hangUpCurrentSession();
            }
        });
    }

    public void onConnectionFailedWithUser(Integer userID) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(R.string.failed);
            }
        });
    }

    public void onError(QBRTCException e) {
    }

    public void onSessionClosed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isInCommingCall) {
                    stopIncomeCallTimer();
                    Log.d(TAG, "isInCommingCall - " + isInCommingCall);
                }

                SessionManager.setCurrentSession(null);

                Log.d(TAG, "Stop session");

                stopTimer();
                closeByWifiStateAllow = true;
                finish();

                // hit final api for server after call closed
                if (isCallConnected) {
                    EndVideoCall();
                    if (mSharePreferences.getUserType().equalsIgnoreCase(Constants.BundleKeys.PATIENT)) {
                        if (!isInCommingCall) {
                            ApplyCallCharge();
                        }
                    }
                }
            }
        });
    }

    public void onSessionStartClose() {
        stopOutBeep();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                disableConversationFragmentButtons();
            }
        });
    }

    public void onDisconnectedFromUser(Integer userID) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(R.string.disconnected);
            }
        });
    }

    public void onReceiveHangUpFromUser(final Integer userID) {
        // TODO update view of this user
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(R.string.hungUp);
            }
        });
        finish();
    }

    private void addIncomeCallFragment(List<Integer> opponentsList, QBRTCSession session) {
        startOutBeep();
        if (session != null) {
            try {
                initIncommingCallTask();
                startIncomeCallTimer();
                Fragment fragment = new IncomeCallFragment();
                Bundle bundle = new Bundle();
                if (opponentsList.size() > 0) {
                    bundle.putInt(Constants.OPPONANT_ID, opponentsList.get(0));
                }
                bundle.putString(Constants.BundleKeys.OPPONANT_NAME, "" + userData.get(Constants.BundleKeys.OPPONANT_NAME));
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, INCOME_CALL_FRAGMENT).commit();
            } catch (Exception e) {
                    e.printStackTrace();
            }

        } else {
            Log.d(TAG, "SKIP addIncomeCallFragment method");
            finish();
        }
    }

    public void addConversationFragment(List<Integer> opponents,
                                        QBRTCTypes.QBConferenceType qbConferenceType,
                                        Constants.CALL_DIRECTION_TYPE callDirectionType) {

        if (SessionManager.getCurrentSession() == null && callDirectionType == Constants.CALL_DIRECTION_TYPE.OUTGOING) {
            startOutBeep();
            try {
                QBRTCSession newSessionWithOpponents = QBRTCClient.getInstance().createNewSessionWithOpponents(opponents, qbConferenceType);
                SessionManager.setCurrentSession(newSessionWithOpponents);
                SessionManager.setUserInfo(userData);

                Log.d(TAG, "addConversationFragmentStartCall. Set session " + newSessionWithOpponents);

            } catch (IllegalStateException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        BaseConversationFragment fragment = (QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO.equals(
                qbConferenceType)) ? new VideoConversationFragment() : new AudioConversationFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CALL_DIRECTION_TYPE_EXTRAS, callDirectionType.ordinal());
        bundle.putInt(Constants.OPPONANT_ID, opponents.get(0));
        bundle.putString(Constants.BundleKeys.OPPONANT_NAME, userData.get(Constants.BundleKeys.OPPONANT_NAME));
        fragment.setArguments(bundle);

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, CONVERSATION_CALL_FRAGMENT).commit();
    }

    private void startOutBeep() {
        ringtone = MediaPlayer.create(this, R.raw.pushringtone);
        ringtone.setLooping(true);
        ringtone.start();
    }

    public void stopOutBeep() {
        if (ringtone != null) {
            try {
                ringtone.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ringtone.release();
            ringtone = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!closeByWifiStateAllow) {
            if (SessionManager.getCurrentSession() != null) {
                if (SessionManager.getCurrentSession().getState() == QBRTCSession.QBRTCSessionState.QB_RTC_SESSION_ACTIVE) {
                    hangUpCurrentSession();
                } else if (SessionManager.getCurrentSession().getState() == QBRTCSession.QBRTCSessionState.QB_RTC_SESSION_NEW) {
                    rejectCurrentSession();
                }
            }
        }
        SessionManager.setCurrentSession(null);
        unregisterReceiver(callBroadcastReceiver);
    }

    @Override
    public void onAttachedToWindow() {
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void registerCallbackListener() {
        callBroadcastReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.CALL_RESULT)) {

                    int callTask = intent.getIntExtra(Constants.CALL_ACTION_VALUE, 0);
                    final Integer userID = intent.getIntExtra(Constants.USER_ID, 0);
                    final Map<String, String> userInfo = (Map<String, String>) intent
                            .getSerializableExtra(Constants.USER_INFO_EXTRAS);
                    final QBRTCException exception = (QBRTCException) intent
                            .getSerializableExtra(Constants.QB_EXCEPTION_EXTRAS);

                    switch (callTask) {
                        case Constants.RECEIVE_NEW_SESSION:
                            onReceiveNewSession();
                            break;
                        case Constants.USER_NOT_ANSWER:
                            onUserNotAnswer(userID);
                            break;
                        case Constants.CALL_REJECT_BY_USER:
                            onCallRejectByUser(userID, userInfo);
                            break;
                        case Constants.RECEIVE_HANG_UP_FROM_USER:
                            onReceiveHangUpFromUser(userID);
                            break;
                        case Constants.SESSION_CLOSED:
                            onSessionClosed();
                            break;
                        case Constants.SESSION_START_CLOSE:
                            onSessionStartClose();
                            break;
                        case Constants.START_CONNECT_TO_USER:
                            onStartConnectToUser(userID);
                            break;
                        case Constants.CONNECTED_TO_USER:
                            onConnectedToUser(userID);
                            break;
                        case Constants.CONNECTION_CLOSED_FOR_USER:
                            onConnectionClosedForUser(userID);
                            break;
                        case Constants.DISCONNECTED_FROM_USER:
                            onDisconnectedFromUser(userID);
                            break;
                        case Constants.DISCONNECTED_TIMEOUT_FROM_USER:
                            onDisconnectedTimeoutFromUser(userID);
                            break;
                        case Constants.CONNECTION_FAILED_WITH_USER:
                            onConnectionFailedWithUser(userID);
                            break;
                        case Constants.ERROR:
                            onError(exception);
                            break;
                    }
                }

            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.CALL_RESULT);
        registerReceiver(callBroadcastReceiver, intentFilter);
    }

    private void updateHistoryAfterVideoCall() {
        if (!UpdateHistoryTwo) {
            RequestQueue queue = Volley.newRequestQueue(this);
            HttpsTrustManager.allowAllSSL();
            String url = Constants.URL.UPDATE_HISTORY_BEFORE_VIDEO_CALL + "/" + mSharePreferences.getHistoryID() + "/update_call_history";
            StringRequest stringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG + "Update Video After Call", response);
                    UpdateHistoryTwo = true;
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError && response != null) {
                        try {
                            String res = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            JSONObject obj = new JSONObject(res);
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
                    params.put("authentication_token", mSharePreferences.getAuthenticationToken());
                    params.put("key", mSharePreferences.getKey());
                    params.put("call_status", "received");
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
    }

    private void EndVideoCall() {
        if (!UpdateHistoryThree) {
            RequestQueue queue = Volley.newRequestQueue(this);
            HttpsTrustManager.allowAllSSL();
            String url = Constants.URL.UPDATE_HISTORY_BEFORE_VIDEO_CALL + "/" + mSharePreferences.getHistoryID() + "/update_history";
            StringRequest stringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG + "Update End Call", response);
                    UpdateHistoryThree = true;
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    finish();
                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError && response != null) {
                        try {
                            String res = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            JSONObject obj = new JSONObject(res);
//                            mSharePreferences.setStripKey(null);
//                            mSharePreferences.setCardDetailsAvailable(false);
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
                    params.put("authentication_token", mSharePreferences.getAuthenticationToken());
                    params.put("key", mSharePreferences.getKey());
                    params.put("duration", "" + getCallTime());
                    params.put("call_status", "completed");
                    params.put("receiver_email", mSharePreferences.getQBEmail());

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
    }

    public void ApplyCallCharge() {
        if (!isChargeApplied) {
            RequestQueue queue = Volley.newRequestQueue(this);
            Log.e("Url", Constants.URL.CHARGE_PAYMENT_URL);
            StringRequest stringrequest = new StringRequest(Request.Method.POST, Constants.URL.CHARGE_PAYMENT_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Charge Apply", response);
                    isChargeApplied = true;
                    ParseChargePaymentReponse(response);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "" + error.toString());
                    mSharePreferences.setStripKey(null);
                    mSharePreferences.setCardDetailsAvailable(false);
                    NetworkResponse response = error.networkResponse;
                    if (response != null) {
                        try {
                            String res = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            JSONObject obj = new JSONObject(res);
                            Log.d(TAG, "" + obj.toString());
                            if (obj.has("message")) {
                                String message = obj.getString("message");
                            }
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
                    params.put("key", "" + mSharePreferences.getKey());
                    params.put("authentication_token", "" + mSharePreferences.getAuthenticationToken());
                    params.put("email", "" + mSharePreferences.getQBEmail());
                    params.put("stripeToken", "" + mSharePreferences.getStripeKey());
                    params.put("amount", "70000");

                    return params;
                }
            };
            queue.add(stringrequest);
        }

    }

    private void ParseChargePaymentReponse(String response) {
        if (StringUtils.isNotEmpty(response)) {
            try {
                JSONObject mainObject = new JSONObject(response);
                String status = mainObject.getString("status");
                String msg = mainObject.getString("message");
                if (status.equalsIgnoreCase("success")) {
                    mSharePreferences.setStripKey(null);
                    mSharePreferences.setCardDetailsAvailable(false);
                    ShowDialog(msg);
                } else {
                    Toast.makeText(CallActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void ShowDialog(String msg) {

        final Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_custom_logout_dialog);
        mDialog.setCancelable(true);
        mDialog.show();
        TextView message = (TextView) mDialog.findViewById(R.id.text);
        message.setText(msg);
        LinearLayout mainWrapper = (LinearLayout) mDialog.findViewById(R.id.mainWrapper);
        mDialog.findViewById(R.id.cancel_logout_btn).setVisibility(View.GONE);
        Button okBtn = (Button) mDialog.findViewById(R.id.logout_yes_btn);
        okBtn.setText("Ok");
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    private long getCallTime() {
        VideoConversationFragment fragment = (VideoConversationFragment) getFragmentManager().findFragmentByTag(CONVERSATION_CALL_FRAGMENT);
        if (fragment != null) {
            Log.e("Call End time", "" + fragment.getCallTime());
            return fragment.getCallTime();
        }
        return 0;
    }

}

