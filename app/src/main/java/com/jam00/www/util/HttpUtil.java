package com.jam00.www.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jam00.www.activity.BaseActivity;
import com.jam00.www.activity.NavBaseActivity;

import java.net.URLEncoder;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by weijingtong20 on 2016/12/26.
 * http 操作
 */
public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
    /**
     * okHttp post同步请求
     * @param url  接口地址
     * @param paramsMap   请求参数
     */
    public static void postRequest(String url, HashMap<String, String> paramsMap,okhttp3.Callback callback) {
        //处理参数
        //创建请求的参数body
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : paramsMap.keySet()) {
            builder.add(key, paramsMap.get(key));
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getContext());
        String authkey = prefs.getString("authkey","");
        builder.add("authkey", authkey);
        RequestBody body = builder.build();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(callback);
    }

}
