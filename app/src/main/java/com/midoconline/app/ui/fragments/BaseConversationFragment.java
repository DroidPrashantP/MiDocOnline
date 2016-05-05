package com.midoconline.app.ui.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

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
import com.midoconline.app.beans.DataHolder;
import com.midoconline.app.beans.HistoryHandler;
import com.midoconline.app.ui.activities.BaseActivity;
import com.midoconline.app.ui.activities.CallActivity;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tereha on 15.07.15.
 */
public abstract class BaseConversationFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = BaseConversationFragment.class.getSimpleName();

    protected List<Integer> opponents;
    protected QBRTCTypes.QBConferenceType qbConferenceType;
    protected int startReason;
    protected String callerName;

    private ToggleButton dynamicToggleVideoCall;
    private ImageView flashToggleVideoCall;
    private ImageView handUpVideoCall;
    private TextView opponentNameView;
    private boolean isAudioEnabled = true;
    private boolean isMessageProcessed;
    private IntentFilter intentFilter;
    private AudioStreamReceiver audioStreamReceiver;
    private Integer callerID;
    private SharePreferences mSharePreferences;
    private Integer receiverID;
    private boolean UpdateHistoryOne = false;
    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Camera.Parameters params;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getContentView(), container, false);

//        ((CallActivity) getActivity()).initActionBarWithTimer();

        if (getArguments() != null) {
            startReason = getArguments().getInt(Constants.CALL_DIRECTION_TYPE_EXTRAS);
            receiverID =  getArguments().getInt(Constants.OPPONANT_ID);
            Log.d(TAG, "receiverID." + receiverID);
            callerName = getArguments().getString(Constants.BundleKeys.OPPONANT_NAME);
        }
        mSharePreferences = new SharePreferences(getActivity());
        initCallData();
        initViews(view);

        if (qbConferenceType.getValue() != 0) {
            updateHistoryBeforeVideoCall();
        }
        hasFlash = getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        return view;
    }

    protected void initCallData() {
        QBRTCSession session = SessionManager.getCurrentSession();
        if (session != null){
            opponents = session.getOpponents();
            callerID = session.getCallerID();
            callerName = mSharePreferences.getUserName();
            qbConferenceType = session.getConferenceType();
        }
    }

    protected abstract int getContentView();

    public void actionButtonsEnabled(boolean enability) {

        flashToggleVideoCall.setEnabled(enability);
        dynamicToggleVideoCall.setEnabled(enability);

        // inactivate toggle buttons
        flashToggleVideoCall.setActivated(enability);
        dynamicToggleVideoCall.setActivated(enability);
    }


    @Override
    public void onStart() {
        getActivity().registerReceiver(audioStreamReceiver, intentFilter);

        super.onStart();
        QBRTCSession session = SessionManager.getCurrentSession();
        if (!isMessageProcessed && session != null) {
            if (startReason == Constants.CALL_DIRECTION_TYPE.INCOMING.ordinal()) {
                Log.d(TAG, "acceptCall() from " + TAG);
                session.acceptCall(session.getUserInfo());
            } else {
                Log.d(TAG, "startCall() from " + TAG);
                session.startCall(SessionManager.userInfo);
            }
            isMessageProcessed = true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intentFilter = new IntentFilter();
        intentFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);
        intentFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
        audioStreamReceiver = new AudioStreamReceiver();
    }

    protected void initViews(View view) {
        dynamicToggleVideoCall = (ToggleButton) view.findViewById(R.id.dynamicToggleVideoCall);
        dynamicToggleVideoCall.setOnClickListener(this);
        flashToggleVideoCall = (ImageView) view.findViewById(R.id.flashToggleVideoCall);
        flashToggleVideoCall.setOnClickListener(this);

        opponentNameView = (TextView) view.findViewById(R.id.incUserName);
        if (startReason == Constants.CALL_DIRECTION_TYPE.OUTGOING.ordinal()) {
            opponentNameView.setText(DataHolder.getUserNameByID(receiverID));
        } else if (startReason == Constants.CALL_DIRECTION_TYPE.INCOMING.ordinal())  {
            opponentNameView.setText(callerName);
        }

        handUpVideoCall = (ImageView) view.findViewById(R.id.handUpVideoCall);
        handUpVideoCall.setOnClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(audioStreamReceiver);
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dynamicToggleVideoCall:
                if (SessionManager.getCurrentSession() != null) {
                    Log.d(TAG, "Dynamic switched");
                    SessionManager.getCurrentSession().switchAudioOutput();
                }
                break;
            case R.id.flashToggleVideoCall:
                if (SessionManager.getCurrentSession() != null) {
//                    if (isFlashOn) {
//                        turnOffFlash();
//                        flashToggleVideoCall.setImageResource(R.drawable.flash_touch_grey);
//                    } else {
//                        turnOnFlash();
//                        flashToggleVideoCall.setImageResource(R.drawable.flash_touch_blue);
//                    }

//                    if (isAudioEnabled) {
//                        Log.d(TAG, "Mic is off");
//                        SessionManager.getCurrentSession().setAudioEnabled(false);
//                        isAudioEnabled = false;
//                    } else {
//                        Log.d(TAG, "Mic is on");
//                        SessionManager.getCurrentSession().setAudioEnabled(true);
//                        isAudioEnabled = true;
//                    }
                }
                break;
            case R.id.handUpVideoCall:
                actionButtonsEnabled(false);
                handUpVideoCall.setEnabled(false);
                Log.d(TAG, "Call is stopped");

                ((CallActivity) getActivity()).hangUpCurrentSession();
                handUpVideoCall.setEnabled(false);
                handUpVideoCall.setActivated(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class AudioStreamReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(AudioManager.ACTION_HEADSET_PLUG)){
                Log.d(TAG, "ACTION_HEADSET_PLUG " + intent.getIntExtra("state", -1));
            } else if (intent.getAction().equals(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)){
                Log.d(TAG, "ACTION_SCO_AUDIO_STATE_UPDATED " + intent.getIntExtra("EXTRA_SCO_AUDIO_STATE", -2));
            }

            if (intent.getIntExtra("state", -1) == 0 /*|| intent.getIntExtra("EXTRA_SCO_AUDIO_STATE", -1) == 0*/){
                dynamicToggleVideoCall.setChecked(false);
            } else if (intent.getIntExtra("state", -1) == 1) {
                dynamicToggleVideoCall.setChecked(true);
            }
            dynamicToggleVideoCall.invalidate();
        }
    }

    private void updateHistoryBeforeVideoCall() {
        if (!UpdateHistoryOne) {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            HttpsTrustManager.allowAllSSL();
            StringRequest stringrequest = new StringRequest(Request.Method.POST, Constants.URL.UPDATE_HISTORY_BEFORE_VIDEO_CALL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (StringUtils.isNotEmpty(response)) {
                        try {
                            String historyID = new JSONObject(response).getString("id");
                            mSharePreferences.setHistoryID(historyID);
                            Log.e("HistoryID",historyID );
                            UpdateHistoryOne = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d("Update Before Call", response);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
                    try {
                        params.put("authentication_token", mSharePreferences.getAuthenticationToken());
                        params.put("key", mSharePreferences.getKey());
                        if (StringUtils.isNotEmpty(DataHolder.getUserEmailID(receiverID))) {
                            params.put("receiver_email", "" + DataHolder.getUserEmailID(receiverID));
                        } else {
                            if (Constants.BundleKeys.DOCTOR.equalsIgnoreCase(mSharePreferences.getUserType())) {
                                params.put("receiver_email", "" + HistoryHandler.getInstance().getCallHistoryObj.patient_email);
                            } else {
                                params.put("receiver_email", "" + HistoryHandler.getInstance().getCallHistoryObj.doctor_email);
                            }
                        }

                        params.put("caller_id", "" + mSharePreferences.getQBEmail());
                        params.put("started_time", "" + new Date());
                        params.put("chat_type", "video");
                        if (mSharePreferences.getUserType().equals(Constants.BundleKeys.DOCTOR)) {
                            params.put("amount", "0");
                        } else {
                            params.put("amount", "70000");
                        }
                        params.put("currency", "dollar");
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
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


    private void turnOnFlash() {

        if(!isFlashOn) {
            if(camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;
        }

    }

    private void turnOffFlash() {

        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
        }
    }
}
