package com.midoconline.app.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.midoconline.app.R;
import com.midoconline.app.Util.Constants;
import com.midoconline.app.Util.SessionManager;
import com.midoconline.app.Util.SharePreferences;
import com.midoconline.app.Util.Utils;
import com.midoconline.app.beans.HistoryHandler;
import com.midoconline.app.ui.activities.CallActivity;
import com.midoconline.app.ui.activities.MainActivity;
import com.midoconline.app.ui.activities.OpponentsActivity;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.quickblox.videochat.webrtc.QBRTCException;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientConnectionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;

import org.jivesoftware.smack.SmackException;
import org.webrtc.VideoCapturerAndroid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Prashant on 08.07.15.
 */
public class IncomeCallListenerService extends Service implements QBRTCClientSessionCallbacks, QBRTCClientConnectionCallbacks {

    private static final String TAG = IncomeCallListenerService.class.getSimpleName();
    private QBChatService chatService;
    private String login;
    private String password;
    private PendingIntent pendingIntent;
    private int startServiceVariant;
    private BroadcastReceiver connectionStateReceiver;
    private boolean needMaintainConnectivity;
    private boolean isConnectivityExists;
    private String CallType;

    @Override
    public void onCreate() {
        super.onCreate();
        QBSettings.getInstance().fastConfigInit(Constants.APP_ID, Constants.AUTH_KEY, Constants.AUTH_SECRET);
        initConnectionManagerListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");

        if (!QBChatService.isInitialized()) {
            QBChatService.init(getApplicationContext());
        }

        chatService = QBChatService.getInstance();

        if (intent != null && intent.getExtras() != null) {
            pendingIntent = intent.getParcelableExtra(Constants.PARAM_PINTENT);
            parseIntentExtras(intent);
            if (TextUtils.isEmpty(login) && TextUtils.isEmpty(password)) {
                getUserDataFromPreferences();
            }
        }

        if (!QBChatService.getInstance().isLoggedIn()) {
            createSession(login, password);
        } else {
            startActionsOnSuccessLogin(login, password);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private Notification createNotification() {
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(context);
        notificationBuilder.setSmallIcon(R.drawable.app_logo)
                .setContentIntent(contentIntent)
                .setTicker(getResources().getString(R.string.service_launched))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(" Login as User");

        Notification notification = notificationBuilder.build();

        return notification;
    }

    private void initQBRTCClient() {
        Log.d(TAG, "initQBRTCClient()");
        try {
            QBChatService.getInstance().startAutoSendPresence(60);
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        }

        // Add signalling manager
        QBChatService.getInstance().getVideoChatWebRTCSignalingManager().addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
            @Override
            public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                if (!createdLocally) {
                    QBRTCClient.getInstance().addSignaling((QBWebRTCSignaling) qbSignaling);
                }
            }
        });

        QBRTCClient.getInstance().setCameraErrorHendler(new VideoCapturerAndroid.CameraErrorHandler() {
            @Override
            public void onCameraError(final String s) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        });

        QBRTCConfig.setAnswerTimeInterval(Constants.ANSWER_TIME_INTERVAL);

        // Add activity as callback to RTCClient
        QBRTCClient.getInstance().addSessionCallbacksListener(this);
        QBRTCClient.getInstance().addConnectionCallbacksListener(this);

        // Start mange QBRTCSessions according to VideoCall parser's callbacks
        QBRTCClient.getInstance().prepareToProcessCalls(getApplicationContext());
    }

    private void parseIntentExtras(Intent intent) {
        login = intent.getStringExtra(Constants.USER_LOGIN);
        password = intent.getStringExtra(Constants.USER_PASSWORD);
        startServiceVariant = intent.getIntExtra(Constants.START_SERVICE_VARIANT, Constants.AUTOSTART);
        CallType = intent.getStringExtra(Constants.BundleKeys.CALL_TYPE);

        Log.e("Login", login);
        Log.e("password", password);
        Log.e("startServiceVariant",""+ startServiceVariant);
        Log.e("CallType",""+ CallType);
    }

    protected void getUserDataFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        login = sharedPreferences.getString(Constants.USER_LOGIN, null);
        password = sharedPreferences.getString(Constants.USER_PASSWORD, null);
    }

    private void createSession(final String login, final String password) {
        if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)) {
            final QBUser user = new QBUser(login, password);
            QBAuth.createSession(login, password, new QBEntityCallbackImpl<QBSession>() {
                @Override
                public void onSuccess(QBSession session, Bundle bundle) {
                    Log.d(TAG, "onSuccess create session with params");
                    user.setId(session.getUserId());

                    if (chatService.isLoggedIn()) {
                        Log.d(TAG, "chatService.isLoggedIn()");
                        startActionsOnSuccessLogin(login, password);
                    } else {
                        Log.d(TAG, "!chatService.isLoggedIn()");
                        chatService.login(user, new QBEntityCallbackImpl<QBUser>() {

                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess login to chat");
                                startActionsOnSuccessLogin(login, password);
                            }

                            @Override
                            public void onError(List errors) {
                                sendResultToActivity(false);
                                Toast.makeText(IncomeCallListenerService.this, "Error when login", Toast.LENGTH_SHORT).show();
                                for (Object error : errors) {
                                    Log.d(TAG, error.toString());
                                }
                            }
                        });
                    }
                }

                @Override
                public void onError(List<String> errors) {
                    Utils.closeProgress();
                    for (String s : errors) {
                        Log.d(TAG, s);
                    }
                    sendResultToActivity(false);
                    Toast.makeText(IncomeCallListenerService.this, "Error when login, check test users login and password", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Utils.closeProgress();
            sendResultToActivity(false);
            stopForeground(true);
            stopService(new Intent(getApplicationContext(), IncomeCallListenerService.class));
        }
    }

    private void startActionsOnSuccessLogin(String login, String password) {
        Utils.closeProgress();
        if (CallType.equalsIgnoreCase(Constants.BundleKeys.NORMAL_CALL)) {
            initQBRTCClient();
            sendResultToActivity(true);
            startOpponentsActivity();
            saveUserDataToPreferences(login, password);
            needMaintainConnectivity = true;
        } else if (CallType.equalsIgnoreCase(Constants.BundleKeys.CREATE_USER_SESSION_WITH_QB)) {
            initQBRTCClient();
            sendResultToActivity(true);
            saveUserDataToPreferences(login, password);
            needMaintainConnectivity = true;
        }else {
            initQBRTCClient();
            sendResultToActivity(true);
            QBRTCTypes.QBConferenceType qbConferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;
            Map<String, String> userInfo = new HashMap<>();
            ArrayList<Integer> ids = new ArrayList<>();
            if (Constants.BundleKeys.DOCTOR.equalsIgnoreCase(new SharePreferences(IncomeCallListenerService.this).getUserType())) {
                ids.add(Integer.parseInt(HistoryHandler.getInstance().getCallHistoryObj.patient_id));
            }else {
                ids.add(Integer.parseInt(HistoryHandler.getInstance().getCallHistoryObj.doctor_id));
            }
            startCallerActivity(IncomeCallListenerService.this, qbConferenceType, ids,
                    userInfo, Constants.CALL_DIRECTION_TYPE.OUTGOING);
            saveUserDataToPreferences(login, password);
            needMaintainConnectivity = true;
        }
    }

    private void saveUserDataToPreferences(String login, String password) {
        Log.d(TAG, "saveUserDataToPreferences()");
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(Constants.USER_LOGIN, login);
        ed.putString(Constants.USER_PASSWORD, password);
        ed.commit();
    }

    private void startOpponentsActivity() {
        Log.d(TAG, "startOpponentsActivity()");
        if (startServiceVariant != Constants.AUTOSTART) {
            Intent intent = new Intent(IncomeCallListenerService.this, OpponentsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void startCallerActivity(Context context, QBRTCTypes.QBConferenceType qbConferenceType,
                                     List<Integer> opponentsIds, Map<String, String> userInfo,
                                     Constants.CALL_DIRECTION_TYPE callDirectionType) {
        Log.d(TAG, "startOpponentsActivity()");
        if (startServiceVariant != Constants.AUTOSTART) {
            Intent intent = new Intent(context, CallActivity.class);
            intent.putExtra(Constants.CALL_DIRECTION_TYPE_EXTRAS, callDirectionType);
            intent.putExtra(Constants.CALL_TYPE_EXTRAS, qbConferenceType);
            intent.putExtra(Constants.USER_INFO_EXTRAS, (Serializable) userInfo);
            intent.putExtra(Constants.OPPONENTS_LIST_EXTRAS, (Serializable) opponentsIds);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        }
    }


    private void sendResultToActivity(boolean isSuccess) {
        Utils.closeProgress();
        Log.d(TAG, "sendResultToActivity()");
        if (startServiceVariant == Constants.LOGIN) {
            try {
                Intent intent = new Intent().putExtra(Constants.LOGIN_RESULT, isSuccess);
                pendingIntent.send(IncomeCallListenerService.this, Constants.LOGIN_RESULT_CODE, intent);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
//        try {
//            QBChatService.getInstance().logout();
//        } catch (SmackException.NotConnectedException e) {
//            e.printStackTrace();
//        }
//        QBRTCClient.getInstance().removeSessionsCallbacksListener(this);
//        QBRTCClient.getInstance().removeConnectionCallbacksListener(this);
//        SessionManager.setCurrentSession(null);
//
        if (connectionStateReceiver != null) {
            unregisterReceiver(connectionStateReceiver);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }


    private void initConnectionManagerListener() {
        connectionStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Connection state was changed");
                boolean isConnected = processConnectivityState(intent);
                updateStateIfNeed(isConnected);
            }

            private boolean processConnectivityState(Intent intent) {
                int connectivityType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1);
                // Check does connectivity equal mobile or wifi types
                boolean connectivityState = false;
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();

                if (networkInfo != null) {
                    if (connectivityType == ConnectivityManager.TYPE_MOBILE
                            || connectivityType == ConnectivityManager.TYPE_WIFI
                            || networkInfo.getTypeName().equals("WIFI")
                            || networkInfo.getTypeName().equals("MOBILE")) {
                        //should check null because in air plan mode it will be null
                        if (networkInfo.isConnected()) {
                            // Check does connectivity EXISTS for connectivity type wifi or mobile internet
                            // Pay attention on "!" symbol  in line below
                            connectivityState = true;
                        }
                    }
                }
                return connectivityState;
            }

            private void updateStateIfNeed(boolean connectionState) {
                if (isConnectivityExists != connectionState) {
                    processCurrentConnectionState(connectionState);
                }
            }

            private void processCurrentConnectionState(boolean isConnected) {
                if (!isConnected) {
                    Log.d(TAG, "Connection is turned off");
                } else {
                    if (needMaintainConnectivity) {
                        Log.d(TAG, "Connection is turned on");
                        if (!QBChatService.isInitialized()) {
                            QBChatService.init(getApplicationContext());
                        }
                        chatService = QBChatService.getInstance();
                        if (!QBChatService.getInstance().isLoggedIn()) {
                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                            String login = sharedPreferences.getString(Constants.USER_LOGIN, null);
                            String password = sharedPreferences.getString(Constants.USER_PASSWORD, null);
                            reloginToChat(login, password);
                        }
                    }
                }
            }

        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionStateReceiver, intentFilter);
    }

    private void reloginToChat(String login, String password) {
        final QBUser user = new QBUser(login, password);
        QBAuth.createSession(login, password, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle bundle) {
                Log.d(TAG, "onSuccess create session with params");
                user.setId(session.getUserId());
                chatService.login(user, new QBEntityCallbackImpl<QBUser>() {

                    @Override
                    public void onSuccess(QBUser result, Bundle params) {
                        Log.d(TAG, "onSuccess login to chat with params");
                    }

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess login to chat");
                    }

                    @Override
                    public void onError(List errors) {
                        Toast.makeText(IncomeCallListenerService.this, "Error when login", Toast.LENGTH_SHORT).show();
                        for (Object error : errors) {
                            Log.d(TAG, error.toString());
                        }
                    }
                });
            }

            @Override
            public void onSuccess() {
                super.onSuccess();
                Log.d(TAG, "onSuccess create session");
            }

            @Override
            public void onError(List<String> errors) {
                for (String s : errors) {
                    Log.d(TAG, s);
                }
            }
        });
    }

    private void sendBroadcastMessages(int callbackAction, Integer usedID,
                                       Map<String, String> userInfo, QBRTCException exception) {
        Intent intent = new Intent();
        intent.setAction(Constants.CALL_RESULT);
        intent.putExtra(Constants.CALL_ACTION_VALUE, callbackAction);
        intent.putExtra(Constants.USER_ID, usedID);
        intent.putExtra(Constants.USER_INFO_EXTRAS, (Serializable) userInfo);
        intent.putExtra(Constants.QB_EXCEPTION_EXTRAS, exception);
        sendBroadcast(intent);
    }


    //========== Implement methods ==========//

    @Override
    public void onReceiveNewSession(QBRTCSession qbrtcSession) {
        if (SessionManager.getCurrentSession() == null) {
            SessionManager.setCurrentSession(qbrtcSession);
            Log.e("Incoming", "" + qbrtcSession.getUserInfo());
            CallActivity.start(this,
                    qbrtcSession.getConferenceType(),
                    qbrtcSession.getOpponents(),
                    qbrtcSession.getUserInfo(),
                    Constants.CALL_DIRECTION_TYPE.INCOMING);
        } else if (SessionManager.getCurrentSession() != null && !qbrtcSession.equals(SessionManager.getCurrentSession())) {
            qbrtcSession.rejectCall(qbrtcSession.getUserInfo());
        }
    }

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {
        sendBroadcastMessages(Constants.USER_NOT_ANSWER, integer, null, null);
    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        sendBroadcastMessages(Constants.CALL_REJECT_BY_USER, integer, map, null);
    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer) {
        if (qbrtcSession.equals(SessionManager.getCurrentSession())) {
            sendBroadcastMessages(Constants.RECEIVE_HANG_UP_FROM_USER, integer, null, null);
        }
    }

    @Override
    public void onSessionClosed(QBRTCSession qbrtcSession) {
        if (qbrtcSession.equals(SessionManager.getCurrentSession())) {
            sendBroadcastMessages(Constants.SESSION_CLOSED, null, null, null);
        }
    }

    @Override
    public void onSessionStartClose(QBRTCSession qbrtcSession) {
        sendBroadcastMessages(Constants.SESSION_START_CLOSE, null, null, null);
    }

    @Override
    public void onStartConnectToUser(QBRTCSession qbrtcSession, Integer integer) {
        sendBroadcastMessages(Constants.START_CONNECT_TO_USER, integer, null, null);
    }

    @Override
    public void onConnectedToUser(QBRTCSession qbrtcSession, Integer integer) {
        sendBroadcastMessages(Constants.CONNECTED_TO_USER, integer, null, null);
    }

    @Override
    public void onConnectionClosedForUser(QBRTCSession qbrtcSession, Integer integer) {
        sendBroadcastMessages(Constants.CONNECTION_CLOSED_FOR_USER, integer, null, null);
    }

    @Override
    public void onDisconnectedFromUser(QBRTCSession qbrtcSession, Integer integer) {
        sendBroadcastMessages(Constants.DISCONNECTED_FROM_USER, integer, null, null);
    }

    @Override
    public void onDisconnectedTimeoutFromUser(QBRTCSession qbrtcSession, Integer integer) {
        sendBroadcastMessages(Constants.DISCONNECTED_TIMEOUT_FROM_USER, integer, null, null);
    }

    @Override
    public void onConnectionFailedWithUser(QBRTCSession qbrtcSession, Integer integer) {
        sendBroadcastMessages(Constants.CONNECTION_FAILED_WITH_USER, integer, null, null);
    }

    @Override
    public void onError(QBRTCSession qbrtcSession, QBRTCException e) {
        sendBroadcastMessages(Constants.ERROR, null, null, null);
    }
}
