package com.jam00.www.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.jam00.www.R;
import com.jam00.www.util.LbsUtil;
import com.jam00.www.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 百度地图
 * 要重写 onResume onPause onDestroy 三个方法，保证资源能够及时释放
 */
public class MapActivity extends BaseActivity {

    private MapView mapView;
    //位置
    private TextView locationText;
    //lbs 类
    private LbsUtil lbsUtil;
    //地图总控制器
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取定位信息
        lbsUtil = new LbsUtil();
        lbsUtil.getLocation(MapActivity.this, new MapLocationListener());
        //初始化地图，要在  setContentView 之前
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        mapView = (MapView)findViewById(R.id.bmapView) ;
        //获取地图控制器
        baiduMap = mapView.getMap();
        //显示当前位置小光标
        baiduMap.setMyLocationEnabled(true);
        //地理位置
        locationText = (TextView) findViewById(R.id.location_text);

    }
    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onPause(){
        super.onPause();
        mapView.onPause();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        lbsUtil.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    /**
     * 启动本 活动
     */
    public static void actionStart(Context context,String latitude,String longitude) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        context.startActivity(intent);
    }
    /**
     * 移动到地图的某个位置
     */
    private void navigateTo(BDLocation location){
        if(isFirstLocate){
            //要移动的坐标点
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            //更新坐标
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            //执行移动
            baiduMap.animateMapStatus(update);
            /**
             * 地图缩放级别
             * zoomTo 方法接收一个 float 参数，3-19
             * 值越大，越精细
             */
            update = MapStatusUpdateFactory.zoomTo(16f);
            //执行更改
            baiduMap.animateMapStatus(update);
            //防止多次调用
            isFirstLocate = false;
        }
        //让小光标在地图上移动
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        locationBuilder.accuracy(location.getRadius());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }
    /**
     * 监听器，定位结果会回调
     */
    public class MapLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            if(location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation){
                //执行移动
                navigateTo(location);
            }
        }
    }
}
