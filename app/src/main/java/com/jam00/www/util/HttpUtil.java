package com.jam00.www.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

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
}
