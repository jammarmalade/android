package com.test.www.test.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.test.www.test.activity.BaseActivity;
import com.test.www.test.activity.WeatherActivity;
import com.test.www.test.gson.Weather;
import com.test.www.test.util.HttpUtil;
import com.test.www.test.util.LbsUtil;
import com.test.www.test.util.LogUtil;
import com.test.www.test.util.Utility;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 自动更新天气服务
 */
public class AutoUpdateService extends Service {
    public static final String TAG = "AutoUpdateService";
    private LocationClient mLocationClient;

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags ,int startId){
//        updateWeather();
//        updateBingPic();
        //获取定位信息
        getLocation();
        LogUtil.d(TAG, "exe - "+Utility.getNowTime());
        //创建定时任务
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
//        int anHour = 8 * 60 * 60 * 1000;//8小时的毫秒
        int anHour = 10 * 1000;//测试 10秒
        // SystemClock.elapsedRealtime() 从开机到现在的毫秒书（手机睡眠(sleep)的时间也包括在内）
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this , AutoUpdateService.class);
        /**
         *  Intent是立即使用的，而PendingIntent可以等到事件发生后触发，PendingIntent可以cancel；
         *  PendingIntent自带Context，而Intent需要在某个Context内运行；
         *  Intent在原task中运行，PendingIntent在新的task中运行。
         */
        PendingIntent pi = PendingIntent.getService(this , 0 ,i ,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime ,pi);
        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 更新天气信息
     */
    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString!=null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = BaseActivity.REQUEST_HOST+"weather?cityid="+weatherId+"&key="+ BaseActivity.AUTH_KEY;
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if(weather!=null && "ok".equals(weather.status)){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }
                }
            });
        }
    }
    /**
     * 更新必应每日一图
     */
    private void updateBingPic(){
        String requestPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
    }
    /**
     * 获取用户坐标信息
     */
    public void getLocation(){
        mLocationClient = new LocationClient(getApplicationContext());
        //注册定位监听器
        mLocationClient.registerLocationListener(new MyLocationListener());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }
    /**
     * 销毁移动定位
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        mLocationClient.stop();
    }
    /**
     * 监听器，定位结果会回调
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location){
            String type = "";
            if(location.getLocType()==BDLocation.TypeGpsLocation){
                type = "GPS";
            }else if(location.getLocType()==BDLocation.TypeNetWorkLocation){
                type = "网络";
            }
            final String finaltype = type;

            Map<String,String> locationInfo = new HashMap<String,String>(){{
                put("latitude",Double.toString(location.getLatitude()));
                put("longitude",Double.toString(location.getLongitude()));
                put("type",finaltype);
                put("county",location.getCountry());//国家
                put("province",location.getProvince());//省
                put("city",location.getCity());//城市
                put("district",location.getDistrict());//区
                put("street",location.getStreet());//街道
            }};
            LogUtil.d(TAG, locationInfo.toString());
            mLocationClient.stop();
        }
    }
}
