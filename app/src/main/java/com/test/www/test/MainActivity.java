package com.test.www.test;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.net.Inet4Address;

public class MainActivity extends BaseActivity {
    public static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"Main Task id is "+getTaskId());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        Button startSecond = (Button)findViewById(R.id.start_second_activity);
        startSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this , SecondActivity.class);
//                startActivity(intent);
                //最佳的启动活动的方法
                SecondActivity.actionStart(MainActivity.this, "this is test");
            }
        });

        //判断活动被销毁之前是否有保存数据(onSaveInstanceState方法调用)
        if(savedInstanceState != null){
            String tmpData = savedInstanceState.getString("data");
            Log.d(TAG,tmpData);
        }
    }
    /**
     * 在活动由不可见变为可见的时候调用
     */
    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG,"onStart");
    }
    /**
     * 在活动准备好和用户进行交互的时候调用
     */
    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG,"onResume");
    }
    /**
     * 在系统准备去启动或恢复另一个活动的时候调用
     */
    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG,"onPause");
    }
    /**
     * 在活动完全不可见的时候调用
     */
    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG,"onStop");
    }
    /**
     * 在活动被销毁之前调用
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }
    /**
     * 在活动由停止状态变为运行状态之前调用
     */
    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG,"onRestart");
    }
    /**
     * 在活动被回收之前调用
     */
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        String tmpData = "save some data";
        outState.putString("data",tmpData);
        Log.d(TAG,"onSaveInstanceState");
    }
}
