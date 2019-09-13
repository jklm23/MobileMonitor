package com.sjx.Action;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sjx.Model.BasicInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SendInfor {
    private static BasicInfo bi;
    private static final String url="http://jklm23.wezoz.com/MobileMonitorServer/getInformation";
    private static final String tag="sendInfo";
    private static int time;
    public SendInfor(BasicInfo bii){

        //time=10;
        bi=bii;
    }

    public static int getTime() {
        return time;
    }

    public static void setTime(int time) {
        SendInfor.time = time;
    }

    public static void toSendInfo(final Context context) {
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        Log.i("调用发送","调用");
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response!=null)
                            time=Integer.parseInt(response);
                        Log.i("收到的time",time+"");
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("发送失败", error.getMessage(), error);
            }

        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                //map.put("Content-Type", "application/json; charset=utf-8");
                map.put("brand", bi.getBrand());
                map.put("model",bi.getModel());
                map.put("IMEI",bi.getIMEI());
                map.put("running_APP",bi.getRunning_APP());
                map.put("memory",bi.getMemory());
                map.put("a_memory",bi.getA_memory());
                map.put("currentLevel",bi.getCurrentLevel());
                map.put("location",bi.getLocation());
                map.put("date",new Date().toString());
                Log.i("发送数据",bi.getBrand());
                return map;
            }
        };

        //设置Tag标签
        request.setTag(tag);

        //将请求添加到队列中
        requestQueue.add(request);
    }
}
