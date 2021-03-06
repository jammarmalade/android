package com.jam00.www.fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jam00.www.R;
import com.jam00.www.activity.BaseActivity;
import com.jam00.www.activity.ChooseAreaActivity;
import com.jam00.www.activity.MainActivity;
import com.jam00.www.activity.WeatherActivity;
import com.jam00.www.db.City;
import com.jam00.www.db.County;
import com.jam00.www.db.Provice;
import com.jam00.www.util.BaseApplication;
import com.jam00.www.util.HttpUtil;
import com.jam00.www.util.LogUtil;
import com.jam00.www.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by weijingtong20 on 2016/12/26.
 * 遍历省市县数据
 */
public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE =0 ;
    public static final int LEVEL_CITY =1 ;
    public static final int LEVEL_COUNTY =2 ;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    /**
     * 省
     */
    private  List<Provice> proviceList;
    /**
     * 市
     */
    private  List<City> cityList;
    /**
     * 县
     */
    private  List<County> countyList;
    /**
     * 选中的省
     */
    private  Provice selectProvince;
    /**
     * 选中的城市
     */
    private  City selectCity;
    /**
     * 当前选择的级别
     */
    private int currentLevel;

    /**
     * 特殊城市
     */
    private ArrayList specialCity = new ArrayList();

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout._choose_area,container,false);
        titleText = (TextView)view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView)view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getContext(),R.layout.simple_list_item_black, dataList);
        listView.setAdapter(adapter);
        //特殊城市
        int[] s = {45052,45053,45054,45055,534,5025,516,517,518,519,520,521,522,523,524,526,525,527,528,529,530,531,532,533};
        for(int i=0 ;i<s.length;i++){
            specialCity.add(s[i]);
        }
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String weatherId = "0";
                String countyName = "";
                if(currentLevel == LEVEL_PROVINCE){
                    //获取城市
                    selectProvince = proviceList.get(i);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    if(specialCity.contains(cityList.get(i).getCityCode())){
                        weatherId = cityList.get(i).getWeatherId();
                        //若天气代号等于0，则用上级的天气代号
                        if("0".equals(weatherId)){
                            weatherId = selectProvince.getWeatherId();
                        }
                        countyName = cityList.get(i).getCityName();
                    }else{
                        //获取县
                        selectCity = cityList.get(i);
                        queryCounties();
                    }
                }else if(currentLevel == LEVEL_COUNTY){
                    weatherId = countyList.get(i).getWeatherId();
                    //若天气代号等于0，则用上级的天气代号
                    if("0".equals(weatherId)){
                        weatherId = selectCity.getWeatherId();
                    }
                    countyName = countyList.get(i).getCountyName();
                }
                //若是有天气id就执行获取天气
                if(!"0".equals(weatherId)){
                    //保存城市名称，用于显示
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                    editor.putString("countyName",countyName);
                    editor.apply();
                    //区分当前碎片(fragment)是在 MainActivity 还是 WeatherActivity 中的
                    if(getActivity() instanceof ChooseAreaActivity){
                        //在 MainActivity 中打开的，跳转到 WeatherActivity
                        WeatherActivity.actionStart(getActivity() ,weatherId);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActivity){
                        //在 WeatherActivity 中打开的，直接请求数据，并刷新当前页
                        WeatherActivity activity = (WeatherActivity)getActivity();
                        //关闭侧滑栏
                        activity.drawerLayout.closeDrawers();
                        //设置正在获取数据
                        activity.swipeRefresh.setRefreshing(true);
                        //调用 requestWeather 获取数据
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }
    /**
     * 获取所有省级，先查数据库，后查服务器
     */
    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        proviceList = DataSupport.findAll(Provice.class);
        if(proviceList.size() > 0){
            dataList.clear();
            for(Provice provice : proviceList){
                dataList.add(provice.getProviceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            String address = BaseActivity.REQUEST_HOST+"/weather/province";
            queryFromServer(address, "province");
        }
    }
    /**
     * 获取所有市级，先查数据库，后查服务器
     */
    private void queryCities(){
        titleText.setText(selectProvince.getProviceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId = ?", String.valueOf(selectProvince.getProviceCode())).find(City.class);
        if(cityList.size() > 0){
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            int provinceCode = selectProvince.getProviceCode();
            String address = BaseActivity.REQUEST_HOST+"/weather/city/"+provinceCode;
            queryFromServer(address, "city");
        }
    }/**
     * 获取所有县级，先查数据库，后查服务器
     */
    private void queryCounties(){
        titleText.setText(selectCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId = ?",String.valueOf(selectCity.getCityCode())).find(County.class);

        if(countyList.size() > 0){
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else{
//            int provinceCode = selectProvince.getProviceCode();
            int cityCode = selectCity.getCityCode();
            String address = BaseActivity.REQUEST_HOST+"/weather/area/"+cityCode;
            queryFromServer(address, "county");
        }
    }
    /**
     * 获取服务器上的数据
     */
    private void queryFromServer(String address , final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //在主线程中执行
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        BaseActivity.mToastStatic("加载失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                LogUtil.d(BaseActivity.TAG,"260");
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText ,selectProvince.getProviceCode());
                }else if("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText ,selectCity.getCityCode());
                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }
    //显示对话框
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            //点击屏幕其它地方是否会消失
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //关闭对话框
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
