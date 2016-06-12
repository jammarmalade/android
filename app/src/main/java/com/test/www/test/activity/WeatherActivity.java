package com.test.www.test.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.www.test.R;
import com.test.www.test.model.Area;
import com.test.www.test.model.BaseApplication;
import com.test.www.test.util.CacheUtil;
import com.test.www.test.util.HttpCallbackListener;
import com.test.www.test.util.HttpUtil;
import com.test.www.test.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * Created by weijingtong20 on 2016/6/12.
 */
public class WeatherActivity extends BaseActivity implements View.OnClickListener{
    public static final String RESQUEST_URL = REQUEST_HOST + "/advanced/api/web/index.php/v1/weather/weather/";

    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;//城市名称
    private TextView publishText;//发布时间
    private TextView weatherDespText;//天气描述
    private TextView temp1Text;//温度1（低）
    private TextView temp2Text;//温度2（高）
    private TextView currentDateText;//当前日期
    private Button switchCity;//切换城市的按钮
    private Button refreshWeather;//刷新天气的按钮

    protected void onCreate(Bundle sis){
        super.onCreate(sis);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_weather);
        //初始化各控件
        weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText = (TextView)findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        //获取从上一个活动传过来的 城市天气id
        String weatherCode = getIntent().getStringExtra("weather_code");
        if (!TextUtils.isEmpty(weatherCode)) {
            // 有天气代号时就去查询天气
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherInfo(weatherCode);
        } else {
            // 没有天气代号时就直接显示本地天气
            showWeather();
        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from","weather");//来源
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                //获取当前缓存的城市天气代码
                String weatherCode = prefs.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }

    //获取城市天气
    private void queryWeatherInfo(String code){
        String url = RESQUEST_URL+code;
        showProgressDialog();
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                String res = parseResult(response);

                if (res.equals("")) {
                    closeProgressDialog();
                    Toast.makeText(WeatherActivity.this, "解析数据失败", Toast.LENGTH_SHORT).show();
                }else{
                    //存储数据
                    saveWeatherData(res);
                    //通过 runOnUiThread() 方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //显示天气信息
                            showWeather();
                            //关闭弹窗
                            closeProgressDialog();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(WeatherActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    //保存天气信息
    private void saveWeatherData(String res) {
        try{
            JSONObject jsonObject = new JSONObject(res);
            JSONObject weatherInfo = jsonObject.getJSONObject("data");
            //环境信息（区县没有）
            String weatherDesp = "无";
            if(weatherInfo.isNull("environment")){

            }else{
                JSONObject environment = weatherInfo.getJSONObject("environment");
                weatherDesp = environment.getString("suggest");
            }

            //昨天信息
            JSONObject yesterday = weatherInfo.getJSONObject("yesterday");
            //未来几天的信息
            JSONObject forecast = weatherInfo.getJSONObject("forecast");
            JSONArray weatherList = forecast.getJSONArray("weather");
            //今天的最低温度和最高温度
            String tempLow,tempUp;
            JSONObject tmpDay = weatherList.getJSONObject(0);
            tempUp = tmpDay.getString("high").split(" ")[1];
            tempLow = tmpDay.getString("low").split(" ")[1];
//            for(int i=0;i<weatherList.length();i++) {

//            }

            //使用 SharedPreferences 保存数据
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年M 月d 日", Locale.CHINA);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
            editor.putBoolean("city_selected", true);
            editor.putString("city_name", weatherInfo.getString("city"));
            editor.putString("weather_code", weatherInfo.getString("code"));
            editor.putString("tempLow", tempLow);
            editor.putString("tempUp", tempUp);
            editor.putString("weather_desp", weatherDesp);
            editor.putString("publish_time", weatherInfo.getString("updatetime"));
            editor.putString("current_date", sdf.format(System.currentTimeMillis()));
            editor.putLong("cache_time", System.currentTimeMillis());
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //只负责解析返回的数据
    public String parseResult(String jsonData){
        try {
            JSONObject jsonObject = new JSONObject(jsonData).getJSONObject("weather");
            boolean status = jsonObject.getBoolean("status");
            String message = jsonObject.getString("message");
            if (status == false) {
                Toast.makeText(BaseApplication.getContext(), message, Toast.LENGTH_SHORT).show();
                return "";
            }
            JSONObject result = jsonObject.getJSONObject("result");

            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    //显示信息
    private void showWeather(){
        //获取缓存信息并显示
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        cityNameText.setText( prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("tempLow", ""));
        temp2Text.setText(prefs.getString("tempUp", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }

}
