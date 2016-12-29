package com.test.www.test.activity;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.test.www.test.R;

/**
 * 欢迎页
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ChooseAreaActivity.actionStart(MainActivity.this);
                //启动主Activity后销毁自身
                finish();
            }
        }, 2000);

    }

}
