package com.jam00.www.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.jam00.www.activity.BaseActivity;
import com.jam00.www.db.City;
import com.jam00.www.db.County;
import com.jam00.www.db.Provice;
import com.jam00.www.gson.Login;
import com.jam00.www.gson.Result;
import com.jam00.www.gson.Tag;
import com.jam00.www.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by weijingtong20 on 2016/12/26.
 * 工具类
 */
public class Utility {
    //加载框
    private static ProgressDialog progressDialog;

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvinces = new JSONArray(response);
                for(int i =0;i < allProvinces.length();i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Provice provice = new Provice();
                    provice.setProviceName(provinceObject.getString("name"));
                    provice.setProviceCode(provinceObject.getInt("id"));
                    provice.setWeatherId(provinceObject.getString("hf_code"));
                    provice.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCities = new JSONArray(response);
                for(int i =0;i < allCities.length();i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setWeatherId(cityObject.getString("hf_code"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounties = new JSONArray(response);
                for(int i =0;i < allCounties.length();i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("hf_code"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 将返回的数据解析成 weather 实体类
     */
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 将返回的数据解析成 对应的 实体类
     */
    public static Object handleResponse(String response, Class t){
        try{
            return new Gson().fromJson(response, (Class<? extends Class>) t);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取当前时间（格式化）
     */
    public static String getNowTime(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
        return sdformat.format(date);
    }
    //计算listView 的高度
    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    //显示对话框
    public static void showProgressDialog(Activity activity, String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            if("".equals(msg)){
                msg = "正在请求...";
            }
            progressDialog.setMessage(msg);
            //点击屏幕其它地方是否会消失
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //关闭对话框
    public static void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
