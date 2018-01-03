package com.jam00.www.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jam00.www.activity.BaseActivity;
import com.jam00.www.activity.HomeActivity;
import com.jam00.www.activity.NavBaseActivity;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by weijingtong20 on 2016/12/26.
 * http 操作
 */
public class HttpUtil {
    //参数类型
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * okHttp post同步请求
     *
     * @param url       接口地址
     * @param paramsMap 请求参数
     */
    public static void postRequest(String url, HashMap<String, String> paramsMap, okhttp3.Callback callback) {
        //处理参数
        //创建请求的参数body
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : paramsMap.keySet()) {
            builder.add(key, paramsMap.get(key));
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getContext());
        String authkey = prefs.getString("authkey", "");
        builder.add("authkey", authkey);
        RequestBody body = builder.build();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 上传图片，多图（form表单提交）
     *
     * @param url       接口地址
     * @param paramsMap 其它数据
     * @param pathList  图片路径列表
     * @param callback  回调函数
     */
    public static void formMultipleUpload(String url, HashMap<String, String> paramsMap, ArrayList<String> pathList, okhttp3.Callback callback) {
        MultipartBody.Builder mBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getContext());
        String authkey = prefs.getString("authkey", "");
        mBody.addFormDataPart("authkey", authkey);
        //其它数据
        String tmpVal = "";
        if (paramsMap.size() > 0) {
            for (String key : paramsMap.keySet()) {
                tmpVal = paramsMap.get(key);
                if(tmpVal==null || tmpVal.equals("")){
                    tmpVal = "";
                }
                mBody.addFormDataPart(key, tmpVal);
            }
        }
        //图片数据
        if (pathList.size() > 0) {
            int i = 0;
            for (String path : pathList) {
                File tmpImg = new File(path);
                if (tmpImg.exists()) {
                    mBody.addFormDataPart("image" + i, tmpImg.getName(), RequestBody.create(MEDIA_TYPE_JPG, tmpImg));
                    i++;
                }
            }
        }
        RequestBody requestBody = mBody.build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }

}
