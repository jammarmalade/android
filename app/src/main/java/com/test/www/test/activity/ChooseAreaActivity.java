package com.test.www.test.activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.www.test.R;
import com.test.www.test.model.Area;
import com.test.www.test.model.BaseApplication;
import com.test.www.test.util.CacheUtil;
import com.test.www.test.util.HttpCallbackListener;
import com.test.www.test.util.HttpUtil;
import com.test.www.test.util.LogUtil;
import com.test.www.test.util.Utility;

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

    private ProgressDialog progressDialog;
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

    @Override
    protected void onCreate(Bundle sis) {
        super.onCreate(sis);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //若当前级别是省级，加载城市数据
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    getCities();
                } else if (currentLevel == LEVEL_CITY) {
                    //若是市级 ，加载县/区数据
                    selectedCity = cityList.get(position);
                    getCunties();
                }
            }
        });
        //加载省级数据
        getProvinces();
    }

    //获取所有省级数据
    private void getProvinces() {
        List cacheData = CacheUtil.readJson(BaseApplication.getContext() , "province");

        try{
            JSONArray tmpProvinceList = new JSONArray(cacheData.get(0).toString());
            for(int i=0;i <tmpProvinceList.length();i++){
                JSONObject province = tmpProvinceList.getJSONObject(i);
                Area area = new Area();
                area.setId(province.getInt("id"));
                area.setName(province.getString("name"));
                area.setAlias(province.getString("alias"));
                area.setCode(province.getString("code"));
                area.setUpid(province.getInt("upid"));
                provinceList.add(area);
            }
        }catch (Exception e){
            e.printStackTrace();
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
            sendRequest("", "province");
        }
    }

    //获取所有市级数据
    private void getCities() {

    }

    //获取所有区县级数据
    private void getCunties() {

    }

    private void sendRequest(final String code, final String type) {
        String url = "";
        if (type == "province") {
            url = RESQUEST_URL + "/province";
        } else if (type == "city") {
            url = RESQUEST_URL + "/city/" + code;
        } else if (type == "area") {
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
                if (res.equals("")) {
                    closeProgressDialog();
                    Toast.makeText(ChooseAreaActivity.this, "解析数据失败", Toast.LENGTH_SHORT).show();
                }
                String cacheName = type;
                if ("province".equals(type)) {
                    cacheName = type;
                } else if ("city".equals(type)) {
                    cacheName = type + "-" + code;
                } else if ("area".equals(type)) {
                    cacheName = type + "-" + code;
                }
                String cacheData = gson.toJson(res);
                CacheUtil.writeJson(BaseApplication.getContext(), cacheData, cacheName, false);

                //通过 runOnUiThread() 方法回到主线程处理逻辑
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        closeProgressDialog();
//                        if ("province".equals(type)) {
//                            getProvinces();
//                        } else if ("city".equals(type)) {
//                            getCities();
//                        } else if ("area".equals(type)) {
//                            getCunties();
//                        }
//                    }
//                });

            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //显示对话框
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            //点击屏幕其它地方是否会消失
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //关闭对话框
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
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
            for (int i = 0; i < result.length(); i++) {
                JSONObject areaTmp = (JSONObject) result.opt(i);
                Area area = new Area();
                area.setId(areaTmp.getInt("id"));
                area.setName(areaTmp.getString("name"));
                area.setAlias(areaTmp.getString("alias"));
                area.setCode(areaTmp.getString("zh_code"));
                area.setUpid(areaTmp.getInt("upid"));
                areaList.add(area);
            }
            return areaList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
