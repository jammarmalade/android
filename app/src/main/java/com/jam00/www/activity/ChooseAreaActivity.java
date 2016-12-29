package com.jam00.www.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.jam00.www.R;

public class ChooseAreaActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);
        //判断是否有缓存数据
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("weather",null)!=null){
            //有缓存数据，直接跳向天气界面
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
    /**
     * 启动本 活动
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ChooseAreaActivity.class);
        context.startActivity(intent);
    }
}
