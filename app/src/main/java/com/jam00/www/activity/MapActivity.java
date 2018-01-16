package com.jam00.www.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.jam00.www.R;
import com.jam00.www.util.LbsUtil;
import com.jam00.www.util.LogUtil;

/**
 * 百度地图
 * 要重写 onResume onPause onDestroy 三个方法，保证资源能够及时释放
 */
public class MapActivity extends BaseActivity {

    private MapView mapView;
    //lbs 类
    private LbsUtil lbsUtil;
    //地图总控制器
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;
    private double latitude,longitude;
    private String locationAddress;
    //是否执行地址回调监听，传入地址的情况将不执行
    private boolean exeListener = true;
    //通用title左右图标按钮和标题
    private Button btnLeft,btnRight;
    private TextView locationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取传入的经纬度
        String lat = getIntent().getStringExtra("latitude");
        if(!"".equals(lat)){
            latitude = Double.valueOf(lat);
        }
        String lon = getIntent().getStringExtra("longitude");
        if(!"".equals(lon)){
            longitude = Double.valueOf(lon);
        }
        locationAddress = getIntent().getStringExtra("location");

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
        //对定位的图标进行配置，需要MyLocationConfiguration实例，这个类是用设置定位图标的显示方式的
        MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING,true,null);
        baiduMap.setMyLocationConfigeration(myLocationConfiguration);

        btnLeft = (Button)findViewById(R.id.common_btn_left);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(!"".equals(locationAddress) && locationAddress!=null){
            if(locationAddress.indexOf("·") > 0){
                String[] addressArr = locationAddress.split("·");
                int count = addressArr.length;
                if(count > 2){
                    //显示后面两个详细地址
                    locationAddress = addressArr[count-2]+"·"+addressArr[count-1];
                }
            }
            btnRight = (Button)findViewById(R.id.common_btn_right);
            btnRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exeListener = true;
                    locationAddress = "";
                }
            });
            btnRight.setVisibility(View.VISIBLE);
        }

        //地理位置
        locationText = (TextView) findViewById(R.id.common_title);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return true;
    }

    /**
     * 启动本 活动
     */
    public static void actionStart(Context context,String latitude,String longitude,String location) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("location", location);
        context.startActivity(intent);
    }
    /**
     * 移动到地图的某个位置
     */
    private void navigateTo(BDLocation location){
        if(isFirstLocate){
            if(!"".equals(latitude) && latitude!=0.0){
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                locationText.setText(locationAddress);
                exeListener = false;
            }
            //要移动的坐标点
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            /**
             * 地图缩放级别
             * zoomTo 方法接收一个 float 参数，3-19
             * 值越大，越精细
             */
            //2018-01-16 10:56 修改
            MapStatus newMapStatus = new MapStatus.Builder().target(ll).zoom(18.0f).build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(newMapStatus);
            /**
            //更新坐标
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            //执行移动
            baiduMap.animateMapStatus(update);
             update = MapStatusUpdateFactory.zoomTo(16f);
             */
            //执行更改
            baiduMap.animateMapStatus(mapStatusUpdate);
            //防止多次调用
            isFirstLocate = false;
        }
        if("".equals(locationAddress)){
            locationText.setText(location.getDistrict()+location.getStreet()+location.getStreetNumber());
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
                if(exeListener){
                    navigateTo(location);
                }
            }
        }
    }
}
