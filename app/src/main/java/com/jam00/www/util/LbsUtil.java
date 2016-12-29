package com.jam00.www.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.jam00.www.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.Manifest;

/**
 * 定位，基于百度sdk
 * 文件名必须为 jniLibs
 */

public class LbsUtil {
    public LocationClient mLocationClient;

    /**
     * 获取地理位置
     */
    public void getLocation(Activity activity, BDLocationListener MyLocationListener){
        mLocationClient = new LocationClient(activity.getApplicationContext());
        //注册定位监听器
        mLocationClient.registerLocationListener(MyLocationListener);
        //权限集合(为了一次性赋予权限)
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(activity, permissions, 1);
        }else{
            requestLocation();
        }
    }
    /**
     * 执行定位
     */
    public void requestLocation(){
        initLocation();
        mLocationClient.start();
    }

    /**
     * 移动定位
     */
    public void initLocation(){
        LocationClientOption option = new LocationClientOption();
        //5 秒更新一次当前位置
        option.setScanSpan(5000);
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        /** 定位模式
         * Hight_Accuracy   高精度模式，GPS信号正常的情况下优先使用GPS，否则使用网络定位(默认)
         * Device_Sensors   传感器模式，只使用GPS定位
         * Battery_Saving   省电模式，只使用网络定位
         */
//        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        //表示获取当前位置详细的地址信息
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }
    /**
     * 停止 mLocationClient
     */
    public void stop(){
        mLocationClient.stop();
    }
}
