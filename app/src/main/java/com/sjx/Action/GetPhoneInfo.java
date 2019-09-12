package com.sjx.Action;

import android.Manifest;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import androidx.annotation.NonNull;

import static android.util.Log.i;


public class GetPhoneInfo {
    //private static Activity activity;


    /**
     * 获取手机品牌
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机型号
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取设备的唯一标识， 需要 “android.permission.READ_Phone_STATE”权限
     * flag为是否已经获取到了权限
     */

    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId="@string/noReadPhoneState";
        //permissionCheck= checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE);
        if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED){
            deviceId = tm.getDeviceId();
        }
        return deviceId;
    }
    private final static String TAG = "peter.log.AppUsageUtil";
    private final static String PACKAGE_NAME_UNKNOWN = "unknown";
    public static void checkUsageStateAccessPermission(Context context) {
        if(!checkAppUsagePermission(context)) {
            requestAppUsagePermission(context);
        }
    }

    public static boolean checkAppUsagePermission(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if(usageStatsManager == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        // try to get app usage state in last 1 min
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 60 * 1000, currentTime);
        if (stats.size() == 0) {
            return false;
        }

        return true;
    }

    public static void requestAppUsagePermission(Context context) {
        //请求权限，跳转至“有权查看使用情况的应用”
        Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            i(TAG,"Start usage access settings activity fail!");
        }
    }

    public static String getTopActivityPackageName(@NonNull Context context) {
        final UsageStatsManager usageStatsManager = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
        String PackagesName="未知";
        if(usageStatsManager == null) {
            return PACKAGE_NAME_UNKNOWN;
        }

        long time = System.currentTimeMillis();
        // 查询最后几秒钟使用应用统计数据
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time -1000*1000, time);
        // 以最后使用时间为标准进行排序
        if(usageStatsList != null) {
            PackagesName="";
            SortedMap<Long, UsageStats> sortedMap = new TreeMap<Long, UsageStats>(
                    new Comparator<Long>() {
                        @Override
                        public int compare(Long o1, Long o2) {
                            return o2.compareTo(o1);
                        }
                    }


            );
            for (UsageStats usageStats : usageStatsList) {
                if(!usageStats.getPackageName().equals("com.sjx.ceshi")&&!usageStats.getPackageName().equals("com.miui.home"))//去掉自身和小米桌面
                    sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            Set s = sortedMap.entrySet();
            Iterator i = s.iterator();
            List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
            boolean flag=false;
            int maxApp=5,k=0;
            while (i.hasNext()) {
                if(k==maxApp)break;
                Map.Entry m=(Map.Entry)i.next();
                UsageStats us=(UsageStats)m.getValue();
                String packagename=us.getPackageName();
                for(int ii=0;ii<packages.size();ii++){
                    //Log.i("packageName",packages.get(ii).packageName);
                    if(packagename.equals(packages.get(ii).packageName)) {
                        Log.i("应用包名",packagename);
                        packagename = packages.get(ii).applicationInfo.loadLabel(context.getPackageManager()).toString();
                        Log.i("应用名称",packagename);

                    }
                }
                if(flag)
                    PackagesName+="\n";
                else
                    flag=true;
                PackagesName+=packagename;
                k++;
            }


        }
//            int n=sortedMap.size();
//            if(n != 0) {//***********不能用i=0到-=n，key！=1,2,3....
//                for(int i=0;i<n;i++) {
//                    if(i!=0)
//                        PackagesName+="\n";
//                    Long ii=new Long(i);
//
//                    if(sortedMap.get(ii).getPackageName()!=null)
//                      PackagesName += sortedMap.get(ii).getPackageName();
//                    Log.d(TAG, "Top activity package name = " + sortedMap.get(ii).getPackageName());
//                }

        return PackagesName;
    }

    /**
     * 获取总内存大小，通过读取/proc/meminfo
    */
    public static String getTotalMemory(Context context){
        String str1="proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory=0;
        try{
            FileReader localFileReader=new FileReader(str1);
            BufferedReader localBufferedReader=new BufferedReader(localFileReader,8192);

            str2=localBufferedReader.readLine();
            arrayOfString=str2.split("\\s+");

            for(String num:arrayOfString){
                Log.i(str2,num+"\t");
                int i=Integer.valueOf(arrayOfString[1]).intValue();
                initial_memory=new Long((long)i*1024);//以GB的形式输出
                localBufferedReader.close();
            }
        }catch (IOException e){

        }
        return Formatter.formatFileSize(context,initial_memory);
    }

    /**
    * 获取已用内存，通过读取/proc/meminfo
     */
    public static String getAvailMemory(Context context){
        ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);// mi.availMem; 当前系统的可用内存
         return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化

    }



}
