package com.midoconline.app.Util;


import com.quickblox.videochat.webrtc.QBRTCSession;

import java.util.HashMap;

/**
 * Created by tereha on 10.07.15.
 */
public class SessionManager {

    private static QBRTCSession currentSession;
    public static HashMap<String, String> userInfo;

    public static QBRTCSession getCurrentSession() {
        return currentSession;
    }

    public static void setCurrentSession(QBRTCSession qbCurrentSession) {
        currentSession = qbCurrentSession;
    }

    public static void setUserInfo(HashMap<String, String> userInfo) {
        SessionManager.userInfo = userInfo;
    }
}
