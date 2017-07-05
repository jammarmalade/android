package com.jam00.www.activity;


import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.jam00.www.R;

/**
 * 欢迎页
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式状态栏
        if(Build.VERSION.SDK_INT >= 21){
            //获取当前活动 DecorView
            View decorView = getWindow().getDecorView();
            //setSystemUiVisibility 改变系统 UI 显示，表示活动布局会显示在状态栏上
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //设置状态栏为透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ChooseAreaActivity.actionStart(MainActivity.this);
//                HomeActivity.actionStart(MainActivity.this);
                //启动主Activity后销毁自身
                finish();
            }
        }, 2000);

    }

}
