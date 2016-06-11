package com.test.www.test.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.test.www.test.model.ActivityCollector;
import com.test.www.test.model.BaseApplication;
import com.test.www.test.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/6/11.
 * 所有 Activity 的父类
 */
public class BaseActivity extends Activity {
    public static final String TAG = "BaseActivity";

    public static final String REQUEST_HOST = "http://192.168.1.46";

    @Override
    protected void onCreate(Bundle sis){
        super.onCreate(sis);
        //加入活动管理器中
        ActivityCollector.addActivity(this);

        //获取当前 Activity 的名称
//        LogUtil.d(TAG, getClass().getSimpleName());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭所以活动
        ActivityCollector.removeActivity(this);
    }


}
