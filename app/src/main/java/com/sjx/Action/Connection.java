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

import java.util.HashMap;
import java.util.Map;


public class Connection {
    private static final String url="http://jklm23.wezoz.com/MobileMonitorServer/testservlet";
    private static final String tag="connection";
    private static boolean flag=false;//连接服务器
    private static int timeInterVal;//间隔采集时间
    private static String IMEI;
    public static boolean isFlag() {
        return flag;
    }

    public static void setFlag(boolean flag) {
        Connection.flag = flag;
    }

    public static int getTimeInterVal() {
        return timeInterVal;
    }

    public static void setTimeInterVal(int timeInterVal) {
        Connection.timeInterVal = timeInterVal;
    }

    public Connection(String IMEI){
        this.IMEI=IMEI;
    }



    public static void toConnect(final Context context) {
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response!=null)
                        {
                            timeInterVal=Integer.parseInt(response);
                        }
                        //Toast.makeText(context,"连接成功，间隔时间："+(timeInterVal+""),Toast.LENGTH_SHORT).show();
                        Log.i("连接成功","间隔时间："+(timeInterVal+""));
                        flag=true;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                if(!flag)
                    Toast.makeText(context,"连接失败",Toast.LENGTH_SHORT).show();
                flag=false;
                Log.e("连接失败", error.getMessage(), error);
            }
        })
        {
            @Override
            public Map<String,String> getParams() throws AuthFailureError {
                Map<String,String>map=new HashMap<>();
                //map.put("Content-Type", "application/json; charset=utf-8");
                map.put("imei",IMEI);//;发出连接请求
                return map;
            }
        };

        //设置Tag标签
        request.setTag(tag);

        //将请求添加到队列中
        requestQueue.add(request);
    }

}
