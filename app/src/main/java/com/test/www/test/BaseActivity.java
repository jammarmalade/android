package com.test.www.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by weijingtong20 on 2016/5/30.
 * 所有活动类的基类
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity",getClass().getSimpleName());
        //添加到活动管理器中
        ActivityCollector.addActivity(this);
    }

    /**
     * 在活动被销毁之前 从活动管理器中移除
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
