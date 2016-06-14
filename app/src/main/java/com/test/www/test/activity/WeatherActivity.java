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
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.test.www.test.R;
import com.test.www.test.model.Area;
import com.test.www.test.model.BaseApplication;
import com.test.www.test.model.ForecastAdapter;
import com.test.www.test.model.Index;
import com.test.www.test.model.IndexAdapter;
import com.test.www.test.model.Weather;
import com.test.www.test.util.CacheUtil;
import com.test.www.test.util.HttpCallbackListener;
import com.test.www.test.util.HttpUtil;
import com.test.www.test.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


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
                    onError("解析数据失败",new Exception());
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
            public void onError(final String msg,Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(WeatherActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                //没有就显示风向
                weatherDesp = weatherInfo.getString("fengxiang");
            }else{
                JSONObject environment = weatherInfo.getJSONObject("environment");
                weatherDesp = environment.getString("suggest");
            }

            //昨天信息
            JSONObject yesterday = weatherInfo.getJSONObject("yesterday");
            //未来几天的信息
            JSONObject forecast = weatherInfo.getJSONObject("forecast");
            JSONArray weatherList = forecast.getJSONArray("weather");
            //指数
            JSONObject zhishus = weatherInfo.getJSONObject("zhishus");
            JSONArray zhishu = zhishus.getJSONArray("zhishu");
            //今天的最低温度和最高温度
            String tempLow,tempUp;
            JSONObject tmpDay = weatherList.getJSONObject(0);
            tempUp = tmpDay.getString("high").split(" ")[1];
            tempLow = tmpDay.getString("low").split(" ")[1];
//            for(int i=0;i<weatherList.length();i++) {
//
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
            editor.putString("weatherList", weatherList.toString());//未来几天天气
            editor.putString("indexs", zhishu.toString());//各项指数
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
        //查看缓存时间
        long cacheTime = prefs.getLong("cache_time",0);
        int expireTime = 600 * 1000;//10分钟缓存
        if((System.currentTimeMillis() - cacheTime) > expireTime){
            queryWeatherInfo(prefs.getString("weather_code","0"));
        }else{
            cityNameText.setText(prefs.getString("city_name", ""));
            temp1Text.setText(prefs.getString("tempLow", ""));
            temp2Text.setText(prefs.getString("tempUp", ""));
            weatherDespText.setText(prefs.getString("weather_desp", ""));
            publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
            currentDateText.setText(prefs.getString("current_date", ""));
            weatherInfoLayout.setVisibility(View.VISIBLE);
            cityNameText.setVisibility(View.VISIBLE);
            //未来几天数据
            String weatherListJson = prefs.getString("weatherList", "");
            if(!weatherListJson.equals("")){
                List<Weather> weatherList = new ArrayList<Weather>();
                try{
                    JSONArray weatherListArr = new JSONArray(weatherListJson);
                    //今天就不显示
                    for(int i=1;i <weatherListArr.length();i++) {
                        JSONObject tmpWeather = (JSONObject) weatherListArr.opt(i);
                        Weather weather = new Weather();
                        weather.setDate(tmpWeather.getString("date"));
                        weather.setTmpLow(tmpWeather.getString("low").split(" ")[1]);
                        weather.setTmpHigh(tmpWeather.getString("high").split(" ")[1]);
                        //天气类型
                        JSONObject tmpDay = tmpWeather.getJSONObject("day");
                        weather.setType(tmpDay.getString("type"));
                        weatherList.add(weather);
                    }

                    ForecastAdapter adapter = new ForecastAdapter(WeatherActivity.this, R.layout.weather_item, weatherList);
                    ListView listView = (ListView)findViewById(R.id.forecast_list);
                    listView.setAdapter(adapter);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //指数信息
            String indexListJson = prefs.getString("indexs", "");
            if(!indexListJson.equals("")){
                List<Index> indexList = new ArrayList<>();
                try{
                    JSONArray indexListArr = new JSONArray(indexListJson);
                    /* 使用 自定义 adapter 布局
                    for(int i=0;i <indexListArr.length();i++) {
                        JSONObject tmpIndex = (JSONObject) indexListArr.opt(i);
                        Index index = new Index();
                        index.setName(tmpIndex.getString("name"));
                        index.setValue(tmpIndex.getString("value"));
                        index.setDetail(tmpIndex.getString("detail"));
                        indexList.add(index);
                    }
                    IndexAdapter adapter = new IndexAdapter(WeatherActivity.this, R.layout.index_item, indexList);
                    ListView listView = (ListView)findViewById(R.id.zhishu_list);
                    listView.setAdapter(adapter);
                    */
                    /* 使用 SimpleAdapter 布局 ListView */
                    List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
                    for(int i=0;i <indexListArr.length();i++) {
                        JSONObject tmpIndex = (JSONObject) indexListArr.opt(i);
                        Map<String , Object> listItem = new HashMap<>();
                        listItem.put("name",tmpIndex.getString("name"));
                        listItem.put("value",tmpIndex.getString("value"));
                        listItem.put("detail",tmpIndex.getString("detail"));
                        listItems.add(listItem);
                    }
                    SimpleAdapter simpleAdapter = new SimpleAdapter(this,listItems ,R.layout.index_item ,
                            new String[]{"name" ,"value" , "detail"},//listItems 中对应的 key 值
                            new int[]{ R.id.name, R.id.value, R.id.detail});//要显示的布局id，与上面的 String[] 一一对应
                    ListView listView = (ListView)findViewById(R.id.zhishu_list);
                    listView.setAdapter(simpleAdapter);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}
