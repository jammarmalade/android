package com.test.www.test.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.test.www.test.R;
import com.test.www.test.model.Area;
import com.test.www.test.model.BaseApplication;
import com.test.www.test.util.CacheUtil;
import com.test.www.test.util.HttpCallbackListener;
import com.test.www.test.util.HttpUtil;
import com.test.www.test.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/11.
 * 选择区域
 */
public class ChooseAreaActivity extends BaseActivity {
    public static final String RESQUEST_URL = REQUEST_HOST + "/advanced/api/web/index.php/v1/weather";

    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    public static final int LEVEL_COUNTY = 3;


    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private List<Area> provinceList = new ArrayList<>();//省列表
    private List<Area> cityList = new ArrayList<>();//市列表
    private List<Area> countyList = new ArrayList<>();//县/区列表
    private List<Area> areaList = new ArrayList<>();//返回数据的存储列表

    private Area selectedProvince;//选中的省
    private Area selectedCity;//选中的市

    private int currentLevel;//当前选中的级别

    private Gson gson = new Gson();

    private String from;
    @Override
    protected void onCreate(Bundle sis) {
        super.onCreate(sis);
        from = getIntent().getStringExtra("from");
        //判断是否选择过城市，选择过就直接跳到显示天气的活动中
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //选择过城市，并且不是从 天气活动中跳转过来的
        if (prefs.getBoolean("city_selected", false) && from==null) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //若有地区天气代号，就跳向天气活动中
                String weatherCode = "0";
                int level = 1;

                //若当前级别是省级，加载城市数据
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    weatherCode = provinceList.get(position).getCode();
                    level = 1;
                } else if (currentLevel == LEVEL_CITY) {
                    //若是市级 ，加载县/区数据
                    selectedCity = cityList.get(position);
                    weatherCode = cityList.get(position).getCode();
                    level = 2;
                } else if (currentLevel == LEVEL_COUNTY) {
                    weatherCode = countyList.get(position).getCode();
                    level = 3;
                }

                if(weatherCode.equals("0")){
                    if(level == 3){
                        Toast.makeText(ChooseAreaActivity.this, "当前城市没有天气信息", Toast.LENGTH_SHORT).show();
                    }else{
                        if (currentLevel == LEVEL_PROVINCE) {
                            getCities();
                        } else if (currentLevel == LEVEL_CITY) {
                            getCunties();
                        }
                    }
                }else{
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("weather_code", weatherCode);//区县的天气代号传过去
                    startActivity(intent);
                    finish();
                }
            }
        });
        //加载省级数据
        getProvinces();
    }

    //获取所有省级数据
    private void getProvinces() {
        List cacheData = CacheUtil.readJson(BaseApplication.getContext() , "province",3600);
        if(cacheData.size()!=0){
            String jsonData = cacheData.get(0).toString();
            provinceList = parseCacheData(jsonData);
        }

        if(provinceList.size() > 0){
            dataList.clear();
            for(Area province : provinceList){
                dataList.add(province.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else{
            sendRequest(0, "province");
        }
    }

    //获取所有市级数据
    private void getCities() {
        cityList.clear();
        String fileName = "city-"+selectedProvince.getId();
        List cacheData = CacheUtil.readJson(BaseApplication.getContext() ,fileName ,3600);
        String jsonData ="";
        if(cacheData.size()!=0){
            jsonData = cacheData.get(0).toString();
            cityList = parseCacheData(jsonData);
        }

        if(cityList.size() > 0){
            dataList.clear();
            for(Area city : cityList){
                dataList.add(city.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getName());
            currentLevel = LEVEL_CITY;
        }else if(jsonData.equals("\"empty\"")){
            Toast.makeText(BaseApplication.getContext(), "没有下级数据", Toast.LENGTH_SHORT).show();
        }else{
            sendRequest(selectedProvince.getId(), "city");
        }
    }

    //获取所有区县级数据
    private void getCunties() {
        countyList.clear();
        String fileName = "area-"+selectedCity.getId();
        List cacheData = CacheUtil.readJson(BaseApplication.getContext() ,fileName ,3600);
        String jsonData ="";
        if(cacheData.size()!=0){
            jsonData = cacheData.get(0).toString();
            countyList = parseCacheData(jsonData);
        }
        if(countyList.size() > 0){
            dataList.clear();
            for(Area county : countyList){
                dataList.add(county.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getName());
            currentLevel = LEVEL_COUNTY;
        }else if(jsonData.equals("\"empty\"")){
            Toast.makeText(BaseApplication.getContext(), "没有下级数据", Toast.LENGTH_SHORT).show();
        }else{
            sendRequest(selectedCity.getId(), "area");
        }
    }
    //解析缓存的数据
    private List<Area> parseCacheData(String cacheData){
        List<Area> tmpList = new ArrayList<>();
        try{
            JSONArray tmpProvinceList = new JSONArray(cacheData);
            for(int i=0;i <tmpProvinceList.length();i++){
                JSONObject province = tmpProvinceList.getJSONObject(i);
                Area area = new Area();
                area.setId(province.getInt("id"));
                area.setName(province.getString("name"));
                area.setAlias(province.getString("alias"));
                area.setCode(province.getString("code"));
                area.setUpid(province.getInt("upid"));
                area.setLevel(province.getInt("level"));
                tmpList.add(area);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return tmpList;
    }
    private void sendRequest(final int code, final String type) {
        String url = "";
        if (type.equals("province")) {
            url = RESQUEST_URL + "/province";
        } else if (type.equals("city")) {
            url = RESQUEST_URL + "/city/" + code;
        } else if (type.equals("area")) {
            url = RESQUEST_URL + "/area/" + code;
        }
        if (url.equals("")) {
            return;
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Object res = parseResult(response);

                String cacheName = type;
                if ("province".equals(type)) {
                    cacheName = type;
                } else if ("city".equals(type)) {
                    cacheName = type + "-" + code;
                } else if ("area".equals(type)) {
                    cacheName = type + "-" + code;
                }
                if (res.equals("")) {
                    onError("解析数据失败",new Exception());
                }else if(res.equals("empty")){
                    onError("",new Exception());
                    //存一份空数据 empty
                    CacheUtil.writeJson(BaseApplication.getContext(), res.toString(),cacheName , false);
                }
                String cacheData = gson.toJson(res);
                CacheUtil.writeJson(BaseApplication.getContext(), cacheData, cacheName, false);

                //通过 runOnUiThread() 方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        if ("province".equals(type)) {
                            getProvinces();
                        } else if ("city".equals(type)) {
                            getCities();
                        } else if ("area".equals(type)) {
                            getCunties();
                        }
                    }
                });

            }

            @Override
            public void onError(final String msg,Exception e) {
                if(!msg.equals("")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(ChooseAreaActivity.this,msg , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }



    /**
     * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            getCities();
        } else if (currentLevel == LEVEL_CITY) {
            getProvinces();
        } else {
            //若是从天气活动中跳转来的，就跳回天气活动
            if (from.equals("weather")) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

    //解析http请求的json数据
    public Object parseResult(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData).getJSONObject("weather");
            boolean status = jsonObject.getBoolean("status");
            String message = jsonObject.getString("message");
            if (status == false) {
                Toast.makeText(BaseApplication.getContext(), message, Toast.LENGTH_SHORT).show();
                return "";
            }
            JSONArray result = jsonObject.getJSONArray("result");
            if(result.length()==0){
                return "empty";
            }
            areaList.clear();
            for (int i = 0; i < result.length(); i++) {
                JSONObject areaTmp = (JSONObject) result.opt(i);
                Area area = new Area();
                area.setId(areaTmp.getInt("id"));
                area.setName(areaTmp.getString("name"));
                area.setAlias(areaTmp.getString("alias"));
                area.setCode(areaTmp.getString("zh_code"));
                area.setUpid(areaTmp.getInt("upid"));
                area.setLevel(areaTmp.getInt("level"));
                areaList.add(area);
            }
            return areaList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
