package com.sjx.Action;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;

import java.lang.reflect.Method;

class Sim1SignalStrengthsListener extends PhoneStateListener {
    private static final String TAG="信号：";
    private static String SIMsignal;
    //private static TextView tvw;
    public Sim1SignalStrengthsListener(int subId) {
        super();
        ReflectUtil.setFieldValue(this, "mSubId", subId);
        //tvw=tv;
    }


    public static String getSIMsignal() {
        return SIMsignal;
    }


    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        String level = getSignalStrengthsLevel(signalStrength)+"";
//        //level返回的即是信号强度0-5

      //  String res=signalStrength.getGsmSignalStrength()+"";
        Log.i("Cdma:", level);
      //  Log.i("Gsm:",res);

    }

    private int getSignalStrengthsLevel(SignalStrength signalStrength) {
        int level = 0;
        try {
            Method levelMethod = SignalStrength.class.getDeclaredMethod("getCdmaDbm");
            level = (int) levelMethod.invoke(signalStrength);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return level;
    }
}
