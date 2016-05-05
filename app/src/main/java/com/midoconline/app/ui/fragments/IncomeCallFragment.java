package com.midoconline.app.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.midoconline.app.beans.DataHolder;
import com.midoconline.app.ui.activities.BaseActivity;
import com.midoconline.app.ui.activities.CallActivity;
import com.quickblox.chat.QBChatService;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tereha on 16.02.15.
 */
public class IncomeCallFragment extends Fragment implements Serializable {

    private static final String TAG = IncomeCallFragment.class.getSimpleName();
    private TextView typeIncCallView;
    private TextView callerName;
    private TextView otherIncUsers;
    private ImageView rejectBtn;
    private ImageView takeBtn;

    private List<Integer> opponents;
    private List<QBUser> opponentsFromCall = new ArrayList<>();
    private MediaPlayer ringtone;
    private Vibrator vibrator;
    private QBRTCTypes.QBConferenceType conferenceType;
    private View view;
    private boolean isVideoCall;
    private Map<String, String> userInfo;
    private SharePreferences mSharePreferences;
    private String OpponantEmail;
    private int receiverID;
    private String OpponantName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            view = inflater.inflate(R.layout.fragment_income_call, container, false);
            mSharePreferences = new SharePreferences(getActivity());
            if (getArguments() != null) {
                receiverID =  getArguments().getInt(Constants.OPPONANT_ID);
                OpponantName = getArguments().getString(Constants.BundleKeys.OPPONANT_NAME);
            }
            initCallData();
            initUI(view);
            initButtonsListener();
        }

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);

        Log.d(TAG, "onCreate() from IncomeCallFragment");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        startCallNotification();
    }

    private void initCallData(){
        QBRTCSession currentSession = SessionManager.getCurrentSession();
        if ( currentSession != null){
            opponents = currentSession.getOpponents();
            conferenceType = currentSession.getConferenceType();
            userInfo = currentSession.getUserInfo();
        }
    }

    private void initUI(View view) {
        isVideoCall = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO.equals(conferenceType);

        typeIncCallView = (TextView) view.findViewById(R.id.typeIncCallView);
        typeIncCallView.setText(isVideoCall ? R.string.incoming_video_call : R.string.incoming_audio_call);

        callerName = (TextView) view.findViewById(R.id.callerName);
        callerName.setText("Incoming call From "+OpponantName);
        OpponantEmail = DataHolder.getUserEmailID(SessionManager.getCurrentSession().getCallerID());

        otherIncUsers = (TextView) view.findViewById(R.id.otherIncUsers);
        otherIncUsers.setText(getOtherIncUsersNames(opponents));

        rejectBtn = (ImageView) view.findViewById(R.id.rejectBtn);
        takeBtn = (ImageView) view.findViewById(R.id.takeBtn);
    }

    private void initButtonsListener() {
            rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rejectBtn.setClickable(false);
                    takeBtn.setClickable(false);
                    Log.d(TAG, "Call is rejected");
                    stopCallNotification();
                    ((CallActivity) getActivity()).rejectCurrentSession();
                    getActivity().finish();
                }
            });

            takeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takeBtn.setClickable(false);
                    rejectBtn.setClickable(false);
                    stopCallNotification();
                    if (opponents.size() > 0) {
                        ((CallActivity) getActivity())
                                .addConversationFragment(
                                        opponents, conferenceType, Constants.CALL_DIRECTION_TYPE.INCOMING);
                        Log.d(TAG, "Call is started");
                    }
                }
            });
    }

    public void startCallNotification() {

        ringtone = MediaPlayer.create(getActivity(), R.raw.pushringtone);
        ringtone.setLooping(true);
        ringtone.start();

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        long[] vibrationCycle = {0, 1000, 1000};
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(vibrationCycle, 1);
        }

    }

    private void stopCallNotification() {
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

        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private String getOtherIncUsersNames(List<Integer> opponents) {
        List<Integer> otherOpponents = new ArrayList<>(opponents);
        StringBuffer s = new StringBuffer("");
//        opponentsFromCall.addAll(DataHolder.getUsersList());
        otherOpponents.remove(QBChatService.getInstance().getUser().getId());

        for (Integer i : otherOpponents) {
            for (QBUser usr : opponentsFromCall) {
                if (usr.getId().equals(i)) {
                    if (otherOpponents.indexOf(i) == (otherOpponents.size() - 1)) {
                        s.append(usr.getFullName() + " ");
                        break;
                    } else {
                        s.append(usr.getFullName() + ", ");
                    }
                }
            }
        }
        return s.toString();
    }

    public void onStop() {
        stopCallNotification();
        super.onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
