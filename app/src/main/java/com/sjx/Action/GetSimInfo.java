package com.sjx.Action;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Method;

public class GetSimInfo{
    private static final String TAG="信号：";
    public static void sim(Context context, int subId) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Log.i("卡：",mTelephonyManager.getNetworkType()+"");
        SubscriptionInfo sub0=null;
        SubscriptionManager mSubscriptionManager = SubscriptionManager.from(context);
        if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED) {
            sub0 = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(subId);//根据subId获取SIM卡
        }
        if (null != sub0) {
            //如果不为空 说明sim卡存在
    /*
        sub0.getSubscriptionId() 获取sim卡1的 subId
    */
            Sim1SignalStrengthsListener mSim1SignalStrengthsListener = new Sim1SignalStrengthsListener(sub0.getSubscriptionId());
         //   String temp=(sub0.getSubscriptionId()+"")+",tvw:"+(tvw.getId()+"");
           // Log.i("监听信息：",temp);
            //开始监听
            if (mTelephonyManager != null) {
                mTelephonyManager.listen(mSim1SignalStrengthsListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            }
        }


        boolean isSimCardExist = false;
        try {
            Method method = TelephonyManager.class.getMethod("getSimState", int.class);
            int simState = (Integer) method.invoke(mTelephonyManager, new Object[]{0});
            if (TelephonyManager.SIM_STATE_READY == simState) {
                isSimCardExist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG,"isSimCardExist"+(subId+1)+":" + isSimCardExist);
    }

}