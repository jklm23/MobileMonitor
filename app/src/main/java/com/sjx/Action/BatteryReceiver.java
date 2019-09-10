package com.sjx.Action;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BatteryReceiver extends BroadcastReceiver {
    int mCurrentLevel=0;
    int m_total=0;
    String m_strPercent;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action=intent.getAction();
        if(action.equalsIgnoreCase(Intent.ACTION_BATTERY_CHANGED))
            Log.i("battery","get battter change broad");
        mCurrentLevel=intent.getIntExtra("level",0);//当前电量
        m_total=intent.getExtras().getInt("scale");//总电量
        int percent=mCurrentLevel*100/m_total;//百分比
        m_strPercent=percent+"%";
        Log.i("当前电量：",mCurrentLevel+"");
    }

    public int getmCurrentLevel() {
        return mCurrentLevel;
    }

    public int getM_total() {
        return m_total;
    }

    public String getM_strPercent() {
        return m_strPercent;
    }
}
