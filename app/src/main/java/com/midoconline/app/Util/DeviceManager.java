package com.midoconline.app.Util;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

/**
 * Created by Prashant on 18/9/15.
 */
public class DeviceManager {
    private Context mContext;

    public DeviceManager(Context mContext) {
        this.mContext = mContext;
    }

    public String getDeviceId() {
        final TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getDeviceId() != null) {
            return tm.getDeviceId();
        } else {
            String android_id = Settings.Secure.getString(mContext.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            return android_id;
        }
    }

}
