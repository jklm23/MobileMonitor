package com.sjx.ceshi;

import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.sjx.Action.BatteryReceiver;
import com.sjx.Action.Connection;
import com.sjx.Action.GetPhoneInfo;
import com.sjx.Action.GetSimInfo;
import com.sjx.Action.SendInfor;
import com.sjx.Model.BasicInfo;

import java.util.ArrayList;
import java.util.List;

import static com.sjx.Action.GetPhoneInfo.checkUsageStateAccessPermission;
import static com.sjx.Action.GetPhoneInfo.getAvailMemory;

public class MainActivity extends AppCompatActivity {

    private TextView tBrand;
    private TextView tModel;
    private TextView tIMEI;
    private TextView trun_Proc;
    private TextView tMemory;
    private TextView tA_memory;
//    private TextView tSIM1;
//    private TextView tSIM2;
    private TextView tCurrentLevel;
    private TextView tLocation;
    private TextView tTimeInterVal;

    private int i;

    private int timeInterVal;//采集时间间隔


    private BasicInfo bi=new BasicInfo();
    private BatteryReceiver m_receiver;
    private Connection conn;
    public LocationClient mLocationClient = null;
    private MyLocationListener myLocationListener = new MyLocationListener();
    private SendInfor si;


    private static final int PERMISSION_REQUEST_CODE =1;
    String[] PermissionsArrays={Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET

    };
    private static List<String> NoPermissionsArrays;
    /**
     * 检查是否拥有指定的所有权限
     */
    @SuppressLint("NewApi")
    private boolean checkPermissionAllGranted(String[] permissions) {
        boolean res=true;
        NoPermissionsArrays=new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                res=false;
                //将未获得的权限加入
                NoPermissionsArrays.add(permission);
            }
        }
        return res;
    }

    /***
     * 在授权权限返回结果时候 处理
     *
     * **/
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
       int n=grantResults.length;
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for(int i=0;i<n;i++){
                switch (permissions[i]){
                    case Manifest.permission.READ_PHONE_STATE:
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,R.string.noReadPhoneState,Toast.LENGTH_SHORT).show();
                            //phoneState=false;
                        }
                        else{
                            //Toast.makeText(this,R.string.ReadPhoneState,Toast.LENGTH_SHORT).show();

                            //获取了权限，更新信息
                            bi.setIMEI(GetPhoneInfo.getIMEI(this));
                            tIMEI.setText(bi.getIMEI());
                            //phoneState=true;
                        }
                }
            }
        }
    }

    /**
     * 要执行的信息获取函数
     *
     */
    private void refresh(){
        if(!conn.isFlag())
            conn.toConnect(this);

        bi.setBrand(GetPhoneInfo.getDeviceBrand());
        bi.setModel(GetPhoneInfo.getDeviceModel());
        bi.setIMEI(GetPhoneInfo.getIMEI(this));

        bi.setMemory(GetPhoneInfo.getTotalMemory(this));
        bi.setA_memory(getAvailMemory(this));

        bi.setCurrentLevel(m_receiver.getmCurrentLevel()+"%");
        bi.setLocation(myLocationListener.getStreet()+" "+myLocationListener.getLocationDescribe());
        tBrand.setText(bi.getBrand());
        tModel.setText(bi.getModel());
        tIMEI.setText(bi.getIMEI());

        tMemory.setText(bi.getMemory());
        tA_memory.setText(bi.getA_memory());
        tCurrentLevel.setText(bi.getCurrentLevel());
        tLocation.setText(bi.getLocation());
        String timeInter=timeInterVal+"";
        tTimeInterVal.setText(timeInter);
       // Log.i("采集",timeInter);
        bi.setRunning_APP(GetPhoneInfo.getTopActivityPackageName(this));
        trun_Proc.setText(bi.getRunning_APP());
        if(conn.isFlag()) {
            timeInterVal = conn.getTimeInterVal();
            timeInter=timeInterVal+"";
            tTimeInterVal.setText(timeInter);
            si=new SendInfor(bi);
            si.toSendInfo(this);

        }

    }

    /**
     * 定时刷新函数
     * @param savedInstanceState
     */
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            refresh();
        }
    };


    private Runnable mRunnabel=new Runnable() {
        @Override
        public void run() {
            while (true){
                try {
                    Thread.sleep(timeInterVal*1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                mHandler.sendMessage(mHandler.obtainMessage());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //检查权限，没有的话尝试获取权限
        if(checkPermissionAllGranted(PermissionsArrays)==false) {
            requestPermissions(NoPermissionsArrays.toArray(new String[NoPermissionsArrays.size()]), PERMISSION_REQUEST_CODE);
        }
        checkUsageStateAccessPermission(this);




//        tSIM1=(TextView)findViewById(R.id.tSIM1);
//        tSIM2=(TextView)findViewById(R.id.tSIM2);
        tBrand=(TextView)findViewById(R.id.tBrand);
        tModel=(TextView)findViewById(R.id.tModel);
        tIMEI=(TextView)findViewById(R.id.tIMEI);
        tMemory=(TextView)findViewById(R.id.tMemory);
        tA_memory=(TextView)findViewById(R.id.tA_memory);
        trun_Proc=(TextView)findViewById(R.id.trun_proc);
        tCurrentLevel=(TextView)findViewById(R.id.tCurrentLevel);
        tLocation=(TextView)findViewById(R.id.tLocation);
        tTimeInterVal=(TextView)findViewById(R.id.tTimeInterVal);

        GetSimInfo si1=new GetSimInfo();
        GetSimInfo si2=new GetSimInfo();


        timeInterVal=10;


        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        m_receiver=new BatteryReceiver();
        registerReceiver(m_receiver,intentFilter);

        //定位


        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myLocationListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，设置定位模式，默认高精度
//LocationMode.Hight_Accuracy：高精度；
//LocationMode. Battery_Saving：低功耗；
//LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
//可选，设置返回经纬度坐标类型，默认GCJ02
//GCJ02：国测局坐标；
//BD09ll：百度经纬度坐标；
//BD09：百度墨卡托坐标；
//海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setScanSpan(1000);
//可选，设置发起定位请求的间隔，int类型，单位ms
//如果设置为0，则代表单次定位，即仅定位一次，默认为0
//如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
//可选，设置是否使用gps，默认false
//使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
//可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
//可选，定位SDK内部是一个service，并放到了独立进程。
//设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
//可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5*60*1000);
//可选，V7.2版本新增能力
//如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

        option.setEnableSimulateGps(false);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setIsNeedAddress(true);
        //不设置则getAddr()为空
        option.setIsNeedLocationDescribe(true);
        //getLocationDescribe

        mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.start();


        conn=new Connection(timeInterVal);
        conn.toConnect(this);
        refresh();
        new Thread(mRunnabel).start();
    }


        @Override
        protected void onDestroy() {
        // 结束启动
            mLocationClient.stop();
            super.onDestroy();
        }

}
