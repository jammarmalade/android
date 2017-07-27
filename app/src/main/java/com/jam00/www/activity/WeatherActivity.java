package com.jam00.www.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.bumptech.glide.Glide;
import com.jam00.www.R;
import com.jam00.www.gson.Forecast;
import com.jam00.www.gson.Weather;
import com.jam00.www.service.AutoUpdateService;
import com.jam00.www.util.HttpUtil;
import com.jam00.www.util.LbsUtil;
import com.jam00.www.util.Utility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 显示城市的天气数据
 */
public class WeatherActivity extends BaseActivity {

    private final String BING_PIC_URL = "http://guolin.tech/api/bing_pic";//获取必应每日一图
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carwashText;
    private TextView sportText;
    private ImageView bingPicImg;//背景图
    //刷新天气
    public SwipeRefreshLayout swipeRefresh;
    //滑动菜单
    public DrawerLayout drawerLayout;
    private Button navButton;
    //位置
    private TextView locationText;
    //lbs 类
    private LbsUtil lbsUtil;
    private Button toMapBtn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //背景图和状态栏融合；5.0以上才支持
        if(Build.VERSION.SDK_INT >= 21){
            //获取当前活动 DecorView
            View decorView = getWindow().getDecorView();
            //setSystemUiVisibility 改变系统 UI 显示，表示活动布局会显示在状态栏上
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //设置状态栏为透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化控件
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        comfortText = (TextView)findViewById(R.id.comfort_text);
        carwashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        //滑动菜单
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navButton = (Button)findViewById(R.id.nav_button);
        //地理位置
        locationText = (TextView) findViewById(R.id.location);
        toMapBtn = (Button)findViewById(R.id.to_map);

        //下拉刷新
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        //设置下拉刷新的进度条颜色
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        final String weatherId;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString!=null){
            //读取缓存
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //服务器获取
            weatherId = getIntent().getStringExtra("weather_id");
            //没有数据，先隐藏 ScrollView
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        //下拉刷新
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
        //设置背景图
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
        //切换城市按钮点击事件
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openDrawer 打开滑动菜单
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //获取定位信息
        lbsUtil = new LbsUtil();
        lbsUtil.getLocation(WeatherActivity.this, new MyLocationListener());
        //跳转到地图界面的按钮
        toMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity.actionStart(WeatherActivity.this,"","");
            }
        });

        //友盟推送
//        PushAgent mPushAgent = PushAgent.getInstance(this);
//        mPushAgent.onAppStart();
//        String info = String.format("DeviceToken:%s\n" + "SdkVersion:%s\nAppVersionCode:%s\nAppVersionName:%s",
//                mPushAgent.getRegistrationId(), MsgConstant.SDK_VERSION,
//                UmengMessageDeviceConfig.getAppVersionCode(this), UmengMessageDeviceConfig.getAppVersionName(this));
//        LogUtil.d(TAG," 173 - "+info);
    }
    /**
     * 根据天气id 获取城市天气信息
     */
    public void requestWeather(final String weatherId){
        final String weatehrUrl = REQUEST_HOST+"/weather/weather/"+weatherId;
        HttpUtil.sendOkHttpRequest(weatehrUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                //在主线程中执行
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BaseActivity.mToastStatic("获取天气信息失败");
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather !=null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            BaseActivity.mToastStatic("获取天气信息失败");
                        }
                        //设置刷新状态
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }
    /**
     * 处理并展示 weather 实体类中的数据
     */
    public void showWeatherInfo(Weather weather){
        if(weather!=null && "ok".equals(weather.status)){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String cityName = prefs.getString("countyName",null);
//            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature+"℃";
            String weatherInfo = weather.now.more.info;
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);
            //清空所有“未来几天天气”的数据
            forecastLayout.removeAllViews();
            for(Forecast forecast : weather.forecastList){
                View view = LayoutInflater.from(this).inflate(R.layout.weather_forecast_item, forecastLayout, false);
                TextView dateText = (TextView)view.findViewById(R.id.date_text);
                TextView infoText = (TextView)view.findViewById(R.id.info_text);
                TextView maxText = (TextView)view.findViewById(R.id.max_text);
                TextView minText = (TextView)view.findViewById(R.id.min_text);
                dateText.setText(forecast.date);
                infoText.setText(forecast.more.info);
                maxText.setText(forecast.temperature.max);
                minText.setText(forecast.temperature.min);
                forecastLayout.addView(view);
            }
            if(weather.aqi!=null){
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }
            String comfort = "舒适度："+weather.suggestion.comfort.info;
            String carWash = "洗车指数："+weather.suggestion.carWash.info;
            String sport = "运动建议："+weather.suggestion.sport.info;
            comfortText.setText(comfort);
            carwashText.setText(carWash);
            sportText.setText(sport);
            weatherLayout.setVisibility(View.VISIBLE);
            //启动自动更新服务
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        }else{
            mToast("获取天气信息失败");
        }
    }
    /**
     * 获取背景图，必应的每日一图
     */
    private void loadBingPic(){
        HttpUtil.sendOkHttpRequest(BING_PIC_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
    /**
     * 申请定位所需权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            mToast("必须同意所有权限才能使用本程序");
                            finish();
                            return;
                        }
                    }
                    lbsUtil.requestLocation();
                } else {
                    mToast("发生未知错误");
                    finish();
                }
                break;
            default:
        }
    }
    /**
     * 销毁移动定位
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        lbsUtil.stop();
    }
    /**
     * 启动本 活动
     */
    public static void actionStart(Context context, String weatherId) {
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.putExtra("weather_id", weatherId);
        context.startActivity(intent);
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
            //获取详细地址信息
            String s = locationInfo.get("county")+locationInfo.get("province")+locationInfo.get("city")+locationInfo.get("district")+locationInfo.get("street");

            locationText.setText(s);
        }
    }
}
